/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.conformance;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service class that offers methods for checking onboarding information and also checks availability metadata from
 * a provided serviceId.
 */
@Service
@RequiredArgsConstructor
public class VerificationOnboardService {

    private final DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate;


    /**
     * Accepts serviceId and checks if the service is onboarded to the API Mediation Layer
     *
     * @param serviceId serviceId to check
     * @return true if the service is known by Eureka otherwise false.
     */
    public boolean checkOnboarding(String serviceId) {

        List<String> serviceLists = discoveryClient.getServices();

        return serviceLists.contains(serviceId);

    }


    /**
     * Accepts metadata and retrieves the Swagger url if it exists
     *
     * @param metadata to grab swagger from
     * @return SwaggerUrl when able, empty string otherwise
     */
    public Optional<String> findSwaggerUrl(Map<String, String> metadata) {

        String swaggerKey = null;
        for (String key : metadata.keySet()) {
            if (key.contains("swaggerUrl")) {        // Find the correct key for swagger docs, can be both apiml.apiInfo.0.swaggerUrl or apiml.apiInfo.api-v1.swaggerUrl for example
                swaggerKey = key;
                break;
            }
        }
        if (swaggerKey == null) {
            return Optional.empty();
        }
        String swaggerUrl = metadata.get(swaggerKey);
        if (swaggerUrl != null) {
            return Optional.of(swaggerUrl);
        }
        return Optional.empty();
    }


    /**
     * Retrieves swagger from the url
     *
     * @param swaggerUrl URL to retrieve from
     * @return Swagger as string
     */
    public String getSwagger(String swaggerUrl) {
        String response;
        response = restTemplate.getForEntity(swaggerUrl, String.class).getBody();
        return response;
    }

    /**
     * Checks if endpoints can be called and return documented responses
     *
     * @param getEndpoints GET endpoints to check
     * @return List of problems
     */
    public List<String> testGetEndpoints(Set<Endpoint> getEndpoints) {
        ArrayList<String> result = new ArrayList<>();

        for (Endpoint endpoint : getEndpoints) {
            String urlFromSwagger = endpoint.getUrl();
            // replaces parameters in {} in query
            String url = urlFromSwagger.replaceAll("\\{[^{}]*}", "dummy");

            ResponseEntity<String> response;
            try {
                response = restTemplate.getForEntity(url, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                response = ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
            }

            String responseBody = response.getBody();

            if (responseBody != null && response.getStatusCode() == HttpStatus.NOT_FOUND && responseBody.contains("ZWEAM104E")) {
                result.add("Documented endpoint at " + endpoint.getUrl() + " could not be located, attempting to call it through gateway gives the ZWEAM104E error");
            }

            if (!endpoint.isGetResponseCodeDocumented(String.valueOf(response.getStatusCode().value()))) {
                result.add("Calling endpoint at " + endpoint.getUrl() + " gives undocumented " + response.getStatusCode().value()
                    + " status code, documented responses are:" + endpoint.getValidResponses().get("GET"));
            }
        }

        return result;
    }

    public List<String> getProblemsWithEndpointUrls(AbstractSwaggerValidator swaggerParser) {
        return swaggerParser.getProblemsWithEndpointUrls();
    }
}