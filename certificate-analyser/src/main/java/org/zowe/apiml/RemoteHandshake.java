/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml;

import javax.net.ssl.SSLHandshakeException;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("squid:S106") //ignoring the System.out System.err warinings
public class RemoteHandshake implements Verifier {

    private SSLContextFactory sslContextFactory;
    private HttpClient httpClient;

    public RemoteHandshake(SSLContextFactory sslContextFactory, HttpClient httpClient) {
        this.sslContextFactory = sslContextFactory;
        this.httpClient = httpClient;
    }

    public void verify() {
        String trustStore = sslContextFactory.getStores().getConf().getTrustStore();
        String serviceAddress = ((ApimlConf)sslContextFactory.getStores().getConf()).getRemoteUrl();
        try {
            System.out.println("Start of the remote SSL handshake.");
            getHandshakeMessage(serviceAddress);
            System.out.println("Handshake was successful. Service \"" + serviceAddress + "\" is trusted by truststore \"" + trustStore
                + "\".");
        } catch (MalformedURLException e) {
            System.out.println("Incorrect url \"" + serviceAddress + "\". Error message: " + e.getMessage());
        } catch (SSLHandshakeException e) {
            System.out.println("SSL Handshake failed for address \"" + serviceAddress +
                "\". Cause of error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed when calling url: \"" + serviceAddress + "\" Error message: " + e.getMessage());
        }
    }

    public String getHandshakeMessage(String serviceAddress) throws Exception{
        try {
            URL url = new URL(serviceAddress);
            httpClient.executeCall(url);
            System.out.println("Handshake was successful. Service \"" + serviceAddress + "\" is trusted by truststore \""
                + "\".");
        } catch (MalformedURLException e) {
            System.out.println("Incorrect url \"" + serviceAddress + "\". Error message: " + e.getMessage());
        } catch (SSLHandshakeException e) {
            System.out.println("SSL Handshake failed for address \"" + serviceAddress +
                "\". Cause of error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed when calling url: \"" + serviceAddress + "\" Error message: " + e.getMessage());
        }

        return null;
    }


}
