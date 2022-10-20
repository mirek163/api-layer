/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.integration.authentication.oauth2;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.zowe.apiml.integration.authentication.pat.ValidateRequestModel;
import org.zowe.apiml.util.http.HttpRequestUtils;
import org.zowe.apiml.util.requests.Endpoints;

import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OktaOauth2Test {

    public static final URI VALIDATE_ENDPOINT = HttpRequestUtils.getUriFromGateway(Endpoints.VALIDATE_OAUTH2_TOKEN);
    @Test
    @Tag("OktaOauth2Test")
    void authorizeWithOkta() {
        String username = "pw623414@broadcom.net";
        String password = "Iron-Maiden";

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", password);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        RestAssured.useRelaxedHTTPSValidation();
        Response loginResponse = given()
            .headers(headers)
            .body(requestBody.toString())
        .when()
            .post("https://dev-95727686.okta.com/api/v1/authn")
        .then()
            .statusCode(200)
            .extract().response();

        Headers loginHeaders = loginResponse.getHeaders();
        Cookies loginCookies = loginResponse.getDetailedCookies();
        String loginBody = loginResponse.getBody().asPrettyString();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", "0oa6a48mniXAqEMrx5d7");
        queryParams.put("redirect_uri", "https://oidcdebugger.com/debug");
        queryParams.put("response_type", "code token");
        queryParams.put("response_mode", "fragment");
        queryParams.put("scope", "openid");
        queryParams.put("state", "sss");
        queryParams.put("nonce", "nnnnn");
        queryParams.put("prompt", "none");
        Response authResponse = given()
            .cookies(loginCookies)
            .queryParams(queryParams)
            .when()
            .get("https://dev-95727686.okta.com/oauth2/v1/authorize")
            .then()
            .statusCode(200)
            .extract().response();

        String body = authResponse.getBody().asPrettyString();
    }
    @Test
    @Tag("OktaOauth2Test")
    void givenValidAccessToken_thenValidate() {
        String username = System.getProperty("okta.client.id");
        String password = System.getProperty("okta.client.password");
        Assertions.assertNotNull(username);
        Assertions.assertNotNull(password);
        Map<String, String> headers = new HashMap<>();
        String creds = username + ":" + password;
        byte[] base64encoded = Base64.getEncoder().encode(creds.getBytes());
        headers.put("authorization", "Basic " + new String(base64encoded));
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("accepts", "application/json");
        RestAssured.useRelaxedHTTPSValidation();
        Object accessToken = given().port(443).headers(headers).when().post("https://dev-95727686.okta.com:443/oauth2/default/v1/token?grant_type=client_credentials&scope=customScope")
            .then().statusCode(200).extract().body().path("access_token");
        if (accessToken instanceof String) {
            String token = (String) accessToken;
            ValidateRequestModel requestBody = new ValidateRequestModel();
            requestBody.setToken(token);
            given().contentType(ContentType.JSON).body(requestBody).when()
                .post(VALIDATE_ENDPOINT)
                .then().statusCode(200);
        } else {
            throw new RuntimeException("Incorrect format of response from authorization server.");
        }
    }

}
