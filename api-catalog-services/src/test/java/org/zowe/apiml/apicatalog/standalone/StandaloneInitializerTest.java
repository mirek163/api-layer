/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.apicatalog.standalone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StandaloneInitializerTest {

    @Mock
    SpringApplication application;

    @Mock
    ConfigurableApplicationContext registry;

    @Autowired
    private StandaloneLoaderService standaloneLoaderService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void testEventIsCalledOnlyOnce() {
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        ConfigurableApplicationContext ctx = mock(ConfigurableApplicationContext.class);
        when(event.getApplicationContext()).thenReturn(ctx);
        when(ctx.containsBean("standaloneInitializer")).thenReturn(true);

        publisher.publishEvent(event);
        publisher.publishEvent(event);

        verify(standaloneLoaderService, times(1)).initializeCache();
    }

    @Configuration
    public static class TestConfiguration {

        @MockBean
        private StandaloneLoaderService standaloneLoaderService;

        @Bean
        public StandaloneInitializer getStandaloneInitializer() {
            return new StandaloneInitializer(standaloneLoaderService);
        }

    }

}
