/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.security.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.zowe.apiml.security.common.token.OAuth2Provider;
import org.zowe.apiml.security.common.token.OAuth2TokenDetails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2TokenProvider implements OAuth2Provider {

    @Value("${apiml.security.oAuth.clientId:}")
    private String clientId;

    @Value("${apiml.security.oAuth.clientSecret:}")
    private String clientSecret;

    @Value("${apiml.security.oAuth.issuerUri:}")
    private String issuerUri;

    @Value("${apiml.security.oAuth.enabled:false}")
    private boolean isEnabled;

    @Autowired
    @Qualifier("secureHttpClientWithoutKeystore")
    private final CloseableHttpClient httpClient;


    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isValid(String token) {
        OAuth2TokenDetails details = introspect(token);
        if (details != null) {
            return details.getActive();
        }
        return false;
    }

    public OAuth2TokenDetails introspect(@NonNull String token) {
        HttpPost post = new HttpPost(issuerUri + "/v1/introspect");

        List<NameValuePair> bodyParams = new ArrayList<>();
        bodyParams.add(new BasicNameValuePair("token", token));
        bodyParams.add(new BasicNameValuePair("token_type_hint", "access_token"));
        post.setEntity(new UrlEncodedFormEntity(bodyParams, StandardCharsets.UTF_8));

        String credentials = clientId + ":" + clientSecret;
        byte[] base64encoded = Base64.getEncoder().encode(credentials.getBytes());
        post.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(base64encoded)));
        post.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        post.setHeader(new BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

        try {
            CloseableHttpResponse response = httpClient.execute(post);
            final int statusCode = response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : 0;
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    return mapper.readValue(responseEntity.getContent(), OAuth2TokenDetails.class);
                }
            } else {
                log.error("Failed to validate the oAuth2 access token. Unexpected response: {}", response.getStatusLine());
                return null;
            }
        } catch (IOException e) {
            log.error("Failed to validate the oAuth2 access token. ", e);
        }
        return null;
    }
}
