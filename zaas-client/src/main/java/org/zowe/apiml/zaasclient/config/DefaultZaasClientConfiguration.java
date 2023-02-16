/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.zaasclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.zowe.apiml.security.common.audit.RauditxService;
import org.zowe.apiml.zaasclient.exception.ZaasConfigurationException;
import org.zowe.apiml.zaasclient.service.ZaasClient;
import org.zowe.apiml.zaasclient.service.internal.ZaasClientImpl;

public class DefaultZaasClientConfiguration {

    @Value("${apiml.service.hostname}")
    private String host;

    @Value("${apiml.service.customMetadata.apiml.gatewayPort}")
    private String port;

    @Value("${apiml.service.customMetadata.apiml.gatewayAuthEndpoint}")
    private String baseUrl;

    @Value("${apiml.service.ssl.keyStore}")
    private String keyStorePath;

    @Value("${apiml.service.ssl.keyStorePassword}")
    private char[] keyStorePassword;

    @Value("${apiml.service.ssl.keyStoreType}")
    private String keyStoreType;

    @Value("${apiml.service.ssl.trustStore}")
    private String trustStorePath;

    @Value("${apiml.service.ssl.trustStorePassword}")
    private char[] trustStorePassword;

    @Value("${apiml.service.ssl.trustStoreType}")
    private String trustStoreType;

    @Value("${apiml.service.ssl.nonStrictVerifySslCertificatesOfServices:false}")
    private boolean nonStrictVerifySslCertificatesOfServices;

    @Value("") // TODO define configuration name
    private boolean rauditxEnabled;

    @Bean
    public ConfigProperties getConfigProperties() {
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setApimlHost(host);
        configProperties.setApimlPort(port);
        configProperties.setApimlBaseUrl(baseUrl);
        configProperties.setKeyStorePath(keyStorePath);
        configProperties.setKeyStorePassword(keyStorePassword);
        configProperties.setKeyStoreType(keyStoreType);
        configProperties.setTrustStorePath(trustStorePath);
        configProperties.setTrustStorePassword(trustStorePassword);
        configProperties.setTrustStoreType(trustStoreType);
        configProperties.setNonStrictVerifySslCertificatesOfServices(nonStrictVerifySslCertificatesOfServices);
        return configProperties;
    }

    @Bean
    public ZaasClient zaasClient(ConfigProperties configProperties, RauditxService rauditxService) throws ZaasConfigurationException {
        if (rauditxEnabled) {
            return new ZaasClientImpl(configProperties, rauditxService);
        }
        return new ZaasClientImpl(configProperties);
    }
}
