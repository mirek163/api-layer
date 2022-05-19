/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.security.service.pat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.zowe.apiml.product.model.KeyValue;
import org.zowe.apiml.security.common.login.AccessTokenProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@Service
@AllArgsConstructor
public class ApimlAccessTokenProvider implements AccessTokenProvider {

    public static final String TOKEN_PREFIX = "zwea_";
    @Qualifier("secureHttpClientWithKeystore")
    private CloseableHttpClient httpClient;
    private DiscoveryClient discoveryClient;

    public AccessTokenContainer createContainer(int validity, String userId, String tokenValue) {
        Set<String> scopes = new HashSet<>();
        scopes.add("ZWE");

        String hashToStore = DigestUtils.sha1Hex(tokenValue);
        AccessTokenContainer atc = new AccessTokenContainer(userId, false, hashToStore,"Bearer",LocalDateTime.now(),LocalDateTime.now().plus(validity,ChronoUnit.DAYS),scopes);
        return atc;
    }

    public String getToken(String userId) throws IOException{
        String tokenValue = generateTokenValue(userId);
        AccessTokenContainer atc = createContainer(90, userId, tokenValue);
        storeToken(atc);
        return tokenValue;
    }

    public String generateTokenValue(String userId){
        RandomStringGenerator rsg = new RandomStringGenerator.Builder().withinRange('0','z').filteredBy(LETTERS,DIGITS).build();
        String key = rsg.generate(40);
        key = key + userId + System.currentTimeMillis();
        return TOKEN_PREFIX + DigestUtils.sha1Hex(key);
    }

    public AccessTokenContainer generateDefault() {
       return createContainer(90, "user","");
    }

    public void storeToken(AccessTokenContainer container) throws IOException {
//        EurekaServiceInstance cachingInstance = (EurekaServiceInstance)discoveryClient.getInstances("cachingservice").get(0);
       String cachingUrl = String.format("%s/%s/%s/%s","https://localhost:10010/cachingservice","api","v1","cache");
        HttpPost request = new HttpPost(cachingUrl);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
       String json = mapper.writeValueAsString(container);
        KeyValue keyValue = new KeyValue(container.getTokenValue(),json);
        json = mapper.writeValueAsString(keyValue);
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = httpClient.execute(request);
        System.out.println(response.getStatusLine().getStatusCode());
    }

    public int validateToken(String token) throws IOException{
        String tokenHash = DigestUtils.sha1Hex(token);
//        EurekaServiceInstance cachingInstance = (EurekaServiceInstance)discoveryClient.getInstances("cachingservice").get(0);
        String cachingUrl = String.format("%s/%s/%s/%s/%s","https://localhost:10010/cachingservice","api","v1","cache",tokenHash);
        HttpGet request = new HttpGet(cachingUrl);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        CloseableHttpResponse response = httpClient.execute(request);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Gson gson = new Gson();
        KeyValue kv = objectMapper.readValue(response.getEntity().getContent(),KeyValue.class);
//        AccessTokenContainer atc = gson.fromJson(kv.getValue(),AccessTokenContainer.class);
        AccessTokenContainer atc = objectMapper.readValue(kv.getValue(),AccessTokenContainer.class);
        return response.getStatusLine().getStatusCode();
    }

    @Data
    @AllArgsConstructor
    public static class AccessTokenContainer {
        public AccessTokenContainer() {
        }

        private String userId;
        private boolean isRevoked;
        private String tokenValue;
        private String tokenType;
        private LocalDateTime issuedAt;
        private LocalDateTime expiresAt;
        private Set<String> scopes;

    }
}
