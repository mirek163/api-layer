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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zowe.apiml.product.gateway.GatewayClient;
import org.zowe.apiml.product.model.KeyValue;
import org.zowe.apiml.security.common.login.AccessTokenProvider;
import org.zowe.apiml.security.common.login.AccessTokenRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApimlAccessTokenProvider implements AccessTokenProvider {

    public static final String TOKEN_PREFIX = "zwea_";
    @Qualifier("secureHttpClientWithKeystore")
    private final CloseableHttpClient httpClient;
    private final GatewayClient gatewayClient;
    @Value("${apiml.security.auth.provider}")
    private String authProvider;

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private AccessTokenContainer createContainer(AccessTokenRequest tokenRequest, String userId, String tokenValue) {
        Set<String> scopes = new HashSet<>();
        Collections.addAll(scopes,tokenRequest.getScopes());
        String hashToStore = DigestUtils.sha1Hex(tokenValue);
        return new AccessTokenContainer(
            userId,
            false,
            hashToStore,
            AccessTokenContainer.TOKEN_TYPE_BEARER,LocalDateTime.now(),
            LocalDateTime.now().plus(tokenRequest.getValidity(),ChronoUnit.DAYS),
            scopes,authProvider);
    }

    public String createToken(String userId, AccessTokenRequest accessTokenRequest) throws IOException{
        String tokenValue = generateTokenValue(userId);
        AccessTokenContainer atc = createContainer(accessTokenRequest, userId, tokenValue);
        if(storeToken(atc)>299) return "error";
        return tokenValue;
    }

    private String generateTokenValue(String userId){
        RandomStringGenerator rsg = new RandomStringGenerator.Builder().withinRange('0','z').filteredBy(LETTERS,DIGITS).build();
        String key = rsg.generate(40);
        key = key + userId + System.currentTimeMillis();
        return TOKEN_PREFIX + DigestUtils.sha1Hex(key);
    }

    public AccessTokenContainer generateDefault() {
       return createContainer(new AccessTokenRequest(90,null), "user","");
    }

    private int storeToken(AccessTokenContainer container) throws IOException {
        String cachingUrl = getCacheUrl();
        HttpPost request = new HttpPost(cachingUrl);
        String json = objectMapper.writeValueAsString(container);
        KeyValue keyValue = new KeyValue(container.getTokenValue(),json);
        json = objectMapper.writeValueAsString(keyValue);
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = httpClient.execute(request);

        return response.getStatusLine().getStatusCode();
    }

    private String getCacheUrl(){
        return String.format("%s://%s/%s/%s/%s/%s",gatewayClient.getGatewayConfigProperties().getScheme(),gatewayClient.getGatewayConfigProperties().getHostname(),"cachingservice","api","v1","cache");
    }

    public int validateToken(String token) throws IOException{
        String tokenHash = DigestUtils.sha1Hex(token);
        String cachingUrl = getCacheUrl() + "/" + tokenHash;
        HttpGet request = new HttpGet(cachingUrl);
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() > 299) {
            return 401;
        }
        KeyValue kv = objectMapper.readValue(response.getEntity().getContent(),KeyValue.class);
        AccessTokenContainer atc = objectMapper.readValue(kv.getValue(),AccessTokenContainer.class);
        if(atc.isRevoked || LocalDateTime.now().isAfter(atc.expiresAt)){
            return 401;
        }
        return response.getStatusLine().getStatusCode();
    }

    public int invalidateAllTokensForUser(String userId) throws IOException{
        List<String> keys = getTokensForUser(userId);
        int returnCode = 200;
        for(String key : keys) {
           returnCode = invalidateTokenByKey(key);
           if(returnCode > 299) {
               return returnCode;
           }
        }
        return returnCode;
    }

    public int invalidateAllTokens(String userId) throws IOException{
        Map<String, KeyValue> allTokens = getAllTokens();
        int returnCode = 200;
        for(Map.Entry<String, KeyValue> entry : allTokens.entrySet()) {
            returnCode = invalidateTokenByKey(entry.getKey());
            if(returnCode > 299) {
                return returnCode;
            }
        }
        return returnCode;
    }

    public int invalidateToken(String token) {
        String tokenHash = DigestUtils.sha1Hex(token);
        return invalidateTokenByKey(tokenHash);
    }

    private int invalidateTokenByKey(String key) {
        HttpDelete revokeRequest = new HttpDelete(getCacheUrl() + "/revoke/" + key);
        try {
            CloseableHttpResponse resp = httpClient.execute(revokeRequest);
            log.debug("Revoked hash: " + key + " with status" + resp.getStatusLine().getStatusCode());
            return resp.getStatusLine().getStatusCode();
        } catch (IOException e) {
            log.error("Error while revoking token with hash: " + key);
            return 500;
        }
    }

    private List<String> getTokensForUser(String userId) throws IOException{
        Map<String, KeyValue> tokens = getAllTokens();
        return tokens.entrySet().stream().filter((stringKeyValueEntry -> {
            AccessTokenContainer atc;
            try {
                atc = objectMapper.readValue(stringKeyValueEntry.getValue().getValue(), AccessTokenContainer.class);
                return userId.equals(atc.userId);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return false;
        })).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private Map<String, KeyValue> getAllTokens() throws IOException{
        CloseableHttpResponse resp = httpClient.execute(new HttpGet(getCacheUrl()));
        return objectMapper.readValue(resp.getEntity().getContent(), new TypeReference<Map<String, KeyValue>>() {
        });
    }

    @Data
    @AllArgsConstructor
    public static class AccessTokenContainer {

        public static final String TOKEN_TYPE_BEARER = "Bearer";
        public AccessTokenContainer() {
        }

        private String userId;
        private boolean isRevoked;
        private String tokenValue;
        private String tokenType;
        private LocalDateTime issuedAt;
        private LocalDateTime expiresAt;
        private Set<String> scopes;
        private String tokenProvider;

    }
}
