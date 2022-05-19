package org.zowe.apiml.gateway.security.service.pat;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApimlAccessTokenProviderTest {

    @Test
    void getToken() {
        ApimlAccessTokenProvider aatp = new ApimlAccessTokenProvider(null,null);
        ApimlAccessTokenProvider.AccessTokenContainer token = aatp.generateDefault();
        assertEquals(OAuth2AccessToken.TokenType.BEARER, token.getTokenType());
        String newHash = DigestUtils.sha3_512Hex(token.getTokenValue());
        assertEquals(newHash, token.getTokenValue());
    }
}
