/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.apicatalog.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "apiml.catalog.custom-style", ignoreInvalidFields = true)
public class CustomStyleConfig {
    private DashboardPage dashboardPage;
    private DetailPage detailPage;

    @Data
    public static class DashboardPage {
        private String backgroundColor = "";
        private String titlesColor = "";
    }

    @Data
    public static class DetailPage {
        private String backgroundColor = "";
    }
}
