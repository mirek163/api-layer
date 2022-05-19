/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.security.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.zowe.apiml.security.common.auth.saf.SafMethodSecurityExpressionHandler;
import org.zowe.apiml.security.common.auth.saf.SafResourceAccessVerifying;
import org.zowe.apiml.security.common.login.AccessTokenProvider;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableConfigurationProperties(SafSecurityConfigurationProperties.class)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final SafSecurityConfigurationProperties safSecurityConfigurationProperties;
    private final SafResourceAccessVerifying safResourceAccessVerifying;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new SafMethodSecurityExpressionHandler(safSecurityConfigurationProperties, safResourceAccessVerifying);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenProvider accessTokenProvider(){
        return userId -> {
            throw new NotImplementedException();
        };
    }

}
