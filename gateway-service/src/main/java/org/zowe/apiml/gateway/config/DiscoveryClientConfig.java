/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.util.ProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zowe.apiml.gateway.discovery.ApimlDiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This configuration override bean EurekaClient with custom ApimlDiscoveryClient. This bean offer additional method
 * fetchRegistry. User can call this method to asynchronously fetch new data from discovery service. There is no time
 * to fetching.
 * <p>
 * Configuration also add listeners to call other beans waiting for fetch new registry. It speed up distribution of
 * changes in whole gateway.
 */
@Configuration
@RequiredArgsConstructor
public class DiscoveryClientConfig {
    private final ApplicationContext context;
    private final AbstractDiscoveryClientOptionalArgs<?> optionalArgs;

//    @Value("${apiml.service.discoveryServiceUrls:-}")
//    private String centralDiscoveryServiceUrls;

    @Bean(destroyMethod = "shutdown")
    @RefreshScope
    @Qualifier("local")
    public ApimlDiscoveryClient eurekaClient(ApplicationInfoManager manager,
                                             EurekaClientConfig config,
                                             @Autowired(required = false) HealthCheckHandler healthCheckHandler
    ) {
        ApplicationInfoManager appManager;
        if (AopUtils.isAopProxy(manager)) {
            appManager = ProxyUtils.getTargetObject(manager);
        } else {
            appManager = manager;
        }

        final ApimlDiscoveryClient discoveryClientClient = new ApimlDiscoveryClient(appManager, config, this.optionalArgs, this.context);
        discoveryClientClient.registerHealthCheck(healthCheckHandler);

        return discoveryClientClient;
    }
    @Bean(destroyMethod = "shutdown")
    @RefreshScope
    @Qualifier("central")
//    @ConditionalOnProperty(value = "apiml.service.discoveryServiceUrls")
    public ApimlDiscoveryClient eurekaClient2(ApplicationInfoManager manager,
                                             EurekaClientConfig config,
                                             @Autowired(required = false) HealthCheckHandler healthCheckHandler
    ) {
        ApplicationInfoManager appManager;
        if (AopUtils.isAopProxy(manager)) {
            appManager = ProxyUtils.getTargetObject(manager);
        } else {
            appManager = manager;
        }
        EurekaClientConfig configBean = updateConfig(config);
        final ApimlDiscoveryClient discoveryClientClient = new ApimlDiscoveryClient(appManager, configBean, this.optionalArgs, this.context);
        discoveryClientClient.registerHealthCheck(healthCheckHandler);

        return discoveryClientClient;
    }

    @Bean
    @Qualifier("local")
    public EurekaDiscoveryClient discoveryClient(@Qualifier("local")EurekaClient client,
                                                 EurekaClientConfig clientConfig) {
        return new EurekaDiscoveryClient(client, clientConfig);
    }
    @Bean
    @Qualifier("central")
//    @ConditionalOnProperty(value = "apiml.service.discoveryServiceUrls")
    public EurekaDiscoveryClient discoveryClient2(@Qualifier("central")EurekaClient client,
                                                 EurekaClientConfig config) {
        EurekaClientConfig configBean = updateConfig(config);
        return new EurekaDiscoveryClient(client, configBean);
    }

    public EurekaClientConfig updateConfig(EurekaClientConfig config){
        Map<String,String> urls = new HashMap<>();
        urls.put("defaultZone","https://localhost:10021/eureka/");
        EurekaClientConfigBean configBean = new EurekaClientConfigBean();
        BeanUtils.copyProperties(config, configBean);
        configBean.setServiceUrl(urls);
        return configBean;
    }
}
