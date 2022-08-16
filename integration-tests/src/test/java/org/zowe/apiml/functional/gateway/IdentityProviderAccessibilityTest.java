/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.functional.gateway;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;

/**
 * Simple test to ensure that given Identity Provider (such as Okta) is accessible
 * and can be used in future functional or integration tests.
 */
class IdentityProviderAccessibilityTest {

    public IdentityProviderAccessibilityTest() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class GivenOktaIdentityProvider {

        /**
         * API token for authentication of API requests
         * Note: needs to be renewed every month, how to solve this???
         *
         * Doc: https://developer.okta.com/docs/guides/create-an-api-token/main/
         */
        private static final String API_TOKEN = "00_ZVs8yICLBLvSBmimpGVu33UNEMONH6nRXsNmpEK";

        /**
         * Note: where to store URL and request???
         */
        private static final String IDP_URL = "https://dev-27061119.okta.com";
        private static final String REQUEST_ENDPOINT = "/api/v1/users?limit=25";

        @Nested
        class WhenRequestedListOfUsers {

            @Test
            void thenResponseOk() {
                given()
                    .header("Authorization", "SSWS " + API_TOKEN)
                    .when()
                    .get(IDP_URL + REQUEST_ENDPOINT)
                    .then()
                    .statusCode(is(SC_OK));
            }
        }
    }
}
