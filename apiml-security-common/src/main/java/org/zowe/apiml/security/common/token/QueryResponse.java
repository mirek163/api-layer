/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.security.common.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.zowe.apiml.cache.EntryExpiration;

import java.util.Date;

/**
 * Represents the query JSON response with the token information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse implements EntryExpiration {

    private String domain;
    private String userId;
    private Date creation;
    private Date expiration;
    @JsonIgnore
    private Source source;

    @Override
    public boolean isExpired() {
        return expiration.before(new Date());
    }

    /**
     * An enumeration defines all possible sources of JWT token using to user authentication into Gateway, Discovery
     * service and catalog.
     */
    public enum Source {

            // JWT token is generated by Zowe (including ie. LTPA token from z/OSMF)
            ZOWE,
            // Zowe uses JWT token generated by z/OSMF
            ZOSMF

        ;

        /**
         * Find the source of JWT token by issuer inside the JWT tokens (see claims)
         * @param issuer issuer claim from JWT token
         * @return which system generated the JWT token
         */
        public static Source valueByIssuer(String issuer) {
            if (StringUtils.equalsIgnoreCase(issuer, "zOSMF")) {
                return ZOSMF;
            }
            if (StringUtils.equalsIgnoreCase(issuer, "APIML")) {
                return ZOWE;
            }
            throw new TokenNotValidException("Unknown token type : " + issuer);
        }

    }

}
