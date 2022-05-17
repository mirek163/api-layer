/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.sample.enable.health;

import com.netflix.appinfo.HealthCheckHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.zowe.apiml.eurekaservice.client.ApiMediationClient;

@Component
@RequiredArgsConstructor
public class HealthCheckIndicator implements HealthIndicator {
    private final ApiMediationClient apimlClient;
    private final HealthCheckHandler healthCheckHandler;

    private boolean handlerRegistered = false;

//    @Bean
//    public void registerHealthCheck() {
//        Eureka Client here is still null
//        apimlClient.getEurekaClient().registerHealthCheck(healthCheckHandler);
//    }

//    @Override
//    protected void doHealthCheck(Health.Builder builder) {
//        builder.down();
//    }

    @Override
    public Health health() {
        if (!handlerRegistered && apimlClient.getEurekaClient() != null) {
            handlerRegistered = true;
            apimlClient.getEurekaClient().registerHealthCheck(healthCheckHandler);
        }
        return Health.outOfService().build();
    }
}
