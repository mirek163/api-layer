/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.security.common.auth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommonLogoutSuccessHandlerTest {
    @Nested
    class WhenLogout {
        @Nested
        class GivenSessionDoesntExist {
            @Test
            void theSessionWillBeRemovedAndCookiesRemovedFromResponse(){
                CommonLogoutSuccessHandler underTest = new CommonLogoutSuccessHandler(new AuthConfigurationProperties());
                HttpServletRequest request = ;
                HttpServletResponse response = ;
                underTest.onLogoutSuccess(request, response, null);
            }
        }    

        @Nested
        class GivenSessionExists {
            @Test
            void thenRemoveCookiesFromResponse() {

            }
        }
    }
}