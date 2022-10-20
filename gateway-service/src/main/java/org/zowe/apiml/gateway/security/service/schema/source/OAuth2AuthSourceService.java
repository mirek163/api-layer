/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.security.service.schema.source;

import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.apiml.gateway.security.mapping.AuthenticationMapper;
import org.zowe.apiml.gateway.security.service.AuthenticationService;
import org.zowe.apiml.gateway.security.service.TokenCreationService;
import org.zowe.apiml.message.core.MessageType;
import org.zowe.apiml.message.log.ApimlLogger;
import org.zowe.apiml.product.logging.annotations.InjectApimlLogger;
import org.zowe.apiml.security.common.token.OAuth2Provider;
import org.zowe.apiml.security.common.token.OAuth2TokenDetails;
import org.zowe.apiml.security.common.token.QueryResponse;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
public class OAuth2AuthSourceService extends TokenAuthSourceService {
    @InjectApimlLogger
    protected final ApimlLogger logger = ApimlLogger.empty();

    private final AuthenticationMapper authenticationMapper;
    private final TokenCreationService tokenService;
    private final AuthenticationService authenticationService;
    private final OAuth2Provider oAuth2Provider;

    @Override
    protected ApimlLogger getLogger() {
        return logger;
    }

    public Function<String, AuthSource> getMapper() {
        return OAuth2AuthSource::new;
    }

    @Override
    public Optional<String> getToken(RequestContext context) {
        return authenticationService.getOAuth2TokenFromRequest(context.getRequest());
    }

    @Override
    public boolean isValid(AuthSource authSource) {
        if (authSource instanceof OAuth2AuthSource) {
            String token = (String) authSource.getRawSource();
            return oAuth2Provider.isValid(token);
        }
        return false;
    }

    @Override
    public AuthSource.Parsed parse(AuthSource authSource) {
        if (authSource instanceof OAuth2AuthSource) {
            String token = (String) authSource.getRawSource();
            OAuth2TokenDetails details = oAuth2Provider.introspect(token);
            if (Boolean.TRUE.equals(details.getActive()) && details.getUsername() != null) {
                return new ParsedTokenAuthSource(details.getUsername(), new Date(details.getIssuedAt()), new Date(details.getExpiresAt()), AuthSource.Origin.OAUTH2);
            }
        }
        return null;
    }

    @Override
    public String getLtpaToken(AuthSource authSource) {
        String zosmfToken = getJWT(authSource);
        AuthSource.Origin origin = getTokenOrigin(zosmfToken);
        if (AuthSource.Origin.ZOWE.equals(origin)) {
            zosmfToken = authenticationService.getLtpaToken(zosmfToken);
        }
        return zosmfToken;
    }

    @Override
    public String getJWT(AuthSource authSource) {
        if (authSource instanceof OAuth2AuthSource) {
            logger.log(MessageType.DEBUG, "Get JWT token from oAuth2 access token.");
            String userId = authenticationMapper.mapToMainframeUserId(authSource);
            if (userId == null) {
                logger.log(MessageType.DEBUG, "It was not possible to map provided distributed id to the mainframe id.");
                throw new AuthSchemeException("org.zowe.apiml.gateway.security.schema.x509.mappingFailed");
            }
            try {
                return tokenService.createJwtTokenWithoutCredentials(userId);
            } catch (Exception e) {
                logger.log(MessageType.DEBUG, "Gateway service failed to obtain token - authentication request to get token failed.", e.getLocalizedMessage());
                throw new AuthSchemeException("org.zowe.apiml.gateway.security.token.authenticationFailed");
            }
        }
        return null;
    }

    private AuthSource.Origin getTokenOrigin(String zosmfToken) {
        QueryResponse response = authenticationService.parseJwtToken(zosmfToken);
        return AuthSource.Origin.valueByIssuer(response.getSource().name());
    }
}
