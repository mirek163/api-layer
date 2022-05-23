/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.security.common.login;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Add access token to response
 */
@Component
@RequiredArgsConstructor
public class SuccessfulAuthAccessTokenHandler implements AuthenticationSuccessHandler {

    private final AccessTokenProvider accessTokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object attributeObject = request.getAttribute(AccessTokenRequest.REQUEST_ATTRIBUTE_NAME);
        String token;
        String userId = authentication.getPrincipal().toString();
        AccessTokenRequest atr;
        if(attributeObject instanceof AccessTokenRequest) {
            atr = (AccessTokenRequest) attributeObject;
            token = accessTokenProvider.createToken(userId, atr);
        }else {
            token = accessTokenProvider.createToken(userId,new AccessTokenRequest(90,null));
        }

        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
