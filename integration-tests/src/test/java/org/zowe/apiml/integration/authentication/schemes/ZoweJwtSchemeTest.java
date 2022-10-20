/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.integration.authentication.schemes;

import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.zowe.apiml.constants.ApimlConstants;
import org.zowe.apiml.util.TestWithStartedInstances;
import org.zowe.apiml.util.categories.DiscoverableClientDependentTest;
import org.zowe.apiml.util.categories.InfinispanStorageTest;
import org.zowe.apiml.util.categories.zOSMFAuthTest;
import org.zowe.apiml.util.config.ItSslConfigFactory;
import org.zowe.apiml.util.config.SslContext;
import org.zowe.apiml.util.http.HttpRequestUtils;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.zowe.apiml.util.SecurityUtils.*;

@zOSMFAuthTest
@DiscoverableClientDependentTest
class ZoweJwtSchemeTest implements TestWithStartedInstances {

    private static URI URL;

    @BeforeAll
    static void init() throws Exception {
        SslContext.prepareSslAuthentication(ItSslConfigFactory.integrationTests());
        URL = HttpRequestUtils.getUriFromGateway("/zowejwt/api/v1/request");
    }

    @Test
    void givenCorrectClientCertificateInRequest() {
        given()
            .config(SslContext.clientCertValid)
            .when()
            .get(URL)
            .then()
            .body("headers.cookie", startsWith(ApimlConstants.COOKIE_AUTH_NAME))
            .statusCode(200);
    }

    @Test
    void givenInvalidClientCertificateInRequest() {
        given()
            .config(SslContext.selfSignedUntrusted)
            .when()
            .get(URL)
            .then()
            .body("headers.x-zowe-auth-failure", is("ZWEAG160E No authentication provided in the request"))
            .header("x-zowe-auth-failure", is("ZWEAG160E No authentication provided in the request"))
            .statusCode(200);
    }

    @Nested
    class GivenCustomAuthHeader {
        @Test
        void thenAddAuthHeader() {
            String jwt = gatewayToken();
            given()
                .config(SslContext.tlsWithoutCert)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .when()
                .get(URL)
                .then()
                .body("headers.customjwtheader", is(jwt))
                .statusCode(200);
        }
    }

    @Nested
    class GivenJWTTest {

        @Test
        void forwardJWTToService() {
            String jwt = gatewayToken();
            given()
                .config(SslContext.tlsWithoutCert)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .when()
                .get(URL)
                .then()
                .body("headers.cookie", is(ApimlConstants.COOKIE_AUTH_NAME + "=" + jwt))
                .statusCode(200);
        }

        @Test
        void preserveCookies() {
            String jwt = gatewayToken();
            Cookie.Builder builder = new Cookie.Builder("XSRF-TOKEN","another-token-in-cookies");
            Cookies cookies = new Cookies(builder.build());
            given()
                .config(SslContext.tlsWithoutCert)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .cookies(cookies)
                .when()
                .get(URL)
                .then()
                .body("cookies.apimlAuthenticationToken", is(jwt))
                .body("cookies.XSRF-TOKEN", is("another-token-in-cookies"))
                .statusCode(200);
        }

        @Nested
        class GivenInvalidTokenTest {
            @Test
            void forwardJWTToService() {
                given()
                    .config(SslContext.tlsWithoutCert)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
                    .when()
                    .get(URL)
                    .then()
                    .header("x-zowe-auth-failure", is("ZWEAG102E Token is not valid"))
                    .statusCode(200);
            }
        }
    }

    @Nested
    class GivenPAT {
        @Test
        @InfinispanStorageTest
        void translateIntoJWTAndSendToService() {
            Set<String> scopes = new HashSet<>();
            scopes.add("zowejwt");
            String pat = personalAccessToken(scopes);
            given()
                .config(SslContext.tlsWithoutCert)
                .header(ApimlConstants.PAT_HEADER_NAME, pat)
                .when()
                .get(URL)
                .then()
                .body("headers.cookie", startsWith(ApimlConstants.COOKIE_AUTH_NAME))
                .statusCode(200);
        }
    }

    @Nested
    class GivenOAuth2AccessToken {
        @Nested
        class whenTokenInHeader {
            private Header oAuthHeader;
            @BeforeEach
            void setup() {
                String accessToken = oAuth2AccessToken();
                oAuthHeader = new Header(ApimlConstants.OAUTH2_HEADER_NAME, accessToken);
            }
            @Test
            void thenTranslateIntoJWTAndSendToService() {
                given()
                    .config(SslContext.tlsWithoutCert)
                    .header(oAuthHeader)
                    .when()
                    .get(URL)
                    .then()
                    .body("headers.cookie", containsString(ApimlConstants.COOKIE_AUTH_NAME))
                    .statusCode(200);
            }
        }
        @Nested
        class whenTokenInCookie {
            private Cookie oAuthCookie;
            @BeforeEach
            void setup() {
                String accessToken = oAuth2AccessToken();
                oAuthCookie = new Cookie.Builder(ApimlConstants.OAUTH2_COOKIE_AUTH_NAME, accessToken).build();
            }
            @Test
            void thenTranslateIntoJWTAndSendToService() {
                given()
                    .config(SslContext.tlsWithoutCert)
                    .cookie(oAuthCookie)
                    .when()
                    .get(URL)
                    .then()
                    .body("headers.cookie", containsString(ApimlConstants.COOKIE_AUTH_NAME))
                    .statusCode(200);
            }
        }
    }
}
