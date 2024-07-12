/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.x509;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.web.server.ServerWebExchange;
import org.zowe.apiml.constants.ApimlConstants;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zowe.apiml.constants.ApimlConstants.HTTP_CLIENT_USE_CLIENT_CERTIFICATE;

class ClientCertFilterFactoryTest {

    private static final String CLIENT_CERT_HEADER = "Client-Cert";
    private static final byte[] CERTIFICATE_BYTES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
    private static final String ENCODED_CERT = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo="; //Base64.getEncoder().encodeToString(CERTIFICATE_BYTES);
    private final X509Certificate[] x509Certificates = new X509Certificate[1];

    SslInfo sslInfo = mock(SslInfo.class);
    static ServerWebExchange exchange = mock(ServerWebExchange.class);
    static ServerHttpRequest request = mock(ServerHttpRequest.class);
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    ForwardClientCertFilterFactory filterFactory;
    ForwardClientCertFilterFactory.Config filterConfig = new ForwardClientCertFilterFactory.Config();
    ServerHttpRequest.Builder requestBuilder;

    @BeforeEach
    void setup() {
        x509Certificates[0] = mock(X509Certificate.class);
        filterFactory = new ForwardClientCertFilterFactory();
        requestBuilder = new ServerHttpRequestBuilderMock();
        ServerWebExchange.Builder exchangeBuilder = new ServerWebExchangeBuilderMock();

        when(sslInfo.getPeerCertificates()).thenReturn(x509Certificates);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getSslInfo()).thenReturn(sslInfo);

        when(request.mutate()).thenReturn(requestBuilder);
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Map<String, Object> attributes = new HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);
    }

    @Nested
    class GivenValidCertificateInRequest {

        @BeforeEach
        void setup() throws CertificateException {
            when(x509Certificates[0].getEncoded()).thenReturn(CERTIFICATE_BYTES);
        }

        @Test
        void whenFilter_thenAddHeaderToRequest() {
            GatewayFilter filter = filterFactory.apply(filterConfig);
            Mono<Void> result = filter.filter(exchange, chain);
            result.block();

            assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
            assertNotNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
            assertEquals(ENCODED_CERT, exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER).get(0));
            assertEquals(Boolean.TRUE, exchange.getAttributes().get(HTTP_CLIENT_USE_CLIENT_CERTIFICATE));
        }

        @Nested
        class WhenClientCertHeaderIsAlreadyInRequest {

            @BeforeEach
            void setup() {
                requestBuilder.header(CLIENT_CERT_HEADER, "This value cannot pass through the filter.");
            }

            @Test
            void whenFilter_thenHeaderContainsNewValue() {
                GatewayFilter filter = filterFactory.apply(filterConfig);
                Mono<Void> result = filter.filter(exchange, chain);
                result.block();

                assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
                assertNotNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
                assertEquals(ENCODED_CERT, exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER).get(0));
            }

            @Nested
            class WhenNoSSLSessionInformation {

                @BeforeEach
                void setup() {
                    when(request.getSslInfo()).thenReturn(null);
                }

                @Test
                void thenNoHeadersInRequest() {
                    GatewayFilter filter = filterFactory.apply(filterConfig);
                    Mono<Void> result = filter.filter(exchange, chain);
                    result.block();

                    assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
                    assertNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
                }
            }
        }

        @Nested
        class WhenNoSSLSessionInformation {

            @BeforeEach
            void setup() {
                when(request.getSslInfo()).thenReturn(null);
            }

            @Test
            void thenNoHeadersInRequest() {
                GatewayFilter filter = filterFactory.apply(filterConfig);
                Mono<Void> result = filter.filter(exchange, chain);
                result.block();

                assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
                assertNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
            }
        }
    }


    @Nested
    class GivenInvalidCertificateInRequest {

        @BeforeEach
        void setup() throws CertificateEncodingException {
            requestBuilder.header(CLIENT_CERT_HEADER, "This value cannot pass through the filter.");
            when(x509Certificates[0].getEncoded()).thenThrow(new CertificateEncodingException("incorrect encoding"));
        }


        @Test
        void thenProvideInfoInFailHeader() {
            GatewayFilter filter = filterFactory.apply(filterConfig);
            Mono<Void> result = filter.filter(exchange, chain);
            result.block();

            assertNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
            assertNotNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
            assertEquals("Invalid client certificate in request. Error message: incorrect encoding", exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER).get(0));
        }
    }

    @Nested
    class GivenNoClientCertificateInRequest {

        @BeforeEach
        void setup() {
            requestBuilder.header(CLIENT_CERT_HEADER, "This value cannot pass through the filter.");
            when(sslInfo.getPeerCertificates()).thenReturn(new X509Certificate[0]);
        }

        @Test
        void thenContinueFilterChainWithoutClientCertHeader() {
            GatewayFilter filter = filterFactory.apply(filterConfig);
            Mono<Void> result = filter.filter(exchange, chain);
            result.block();
            assertNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
            assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
        }
    }

    @Nested
    class GivenNoSSLSessionInformationInRequest {

        @BeforeEach
        void setup() {
            requestBuilder.header(CLIENT_CERT_HEADER, "This value cannot pass through the filter.");
            when(request.getSslInfo()).thenReturn(null);
        }

        @Test
        void thenContinueFilterChainWithoutClientCertHeader() {
            GatewayFilter filter = filterFactory.apply(filterConfig);
            Mono<Void> result = filter.filter(exchange, chain);
            result.block();

            assertNull(exchange.getRequest().getHeaders().get(ApimlConstants.AUTH_FAIL_HEADER));
            assertNull(exchange.getRequest().getHeaders().get(CLIENT_CERT_HEADER));
        }
    }

    public static class ServerHttpRequestBuilderMock implements ServerHttpRequest.Builder {
        HttpHeaders headers = new HttpHeaders();
        SslInfo sslInfo;

        @Override
        public ServerHttpRequest.Builder method(HttpMethod httpMethod) {
            return null;
        }

        @Override
        public ServerHttpRequest.Builder uri(URI uri) {
            return null;
        }

        @Override
        public ServerHttpRequest.Builder path(String path) {
            return null;
        }

        @Override
        public ServerHttpRequest.Builder contextPath(String contextPath) {
            return null;
        }

        @Override
        public ServerHttpRequest.Builder header(String headerName, String... headerValues) {
            headers.add(headerName, headerValues[0]);
            return this;
        }

        @Override
        public ServerHttpRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        @Override
        public ServerHttpRequest.Builder sslInfo(SslInfo sslInfo) {
            this.sslInfo = sslInfo;
            return this;
        }

        @Override
        public ServerHttpRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ServerHttpRequest build() {
            when(request.getHeaders()).thenReturn(headers);
            return request;
        }
    }

    public static class ServerWebExchangeBuilderMock implements ServerWebExchange.Builder {
        ServerHttpRequest request;

        @Override
        public ServerWebExchange.Builder request(Consumer<ServerHttpRequest.Builder> requestBuilderConsumer) {
            return null;
        }

        @Override
        public ServerWebExchange.Builder request(ServerHttpRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public ServerWebExchange.Builder response(ServerHttpResponse response) {
            return null;
        }

        @Override
        public ServerWebExchange.Builder principal(Mono<Principal> principalMono) {
            return null;
        }

        @Override
        public ServerWebExchange build() {
            when(exchange.getRequest()).thenReturn(request);
            return exchange;
        }
    }
}
