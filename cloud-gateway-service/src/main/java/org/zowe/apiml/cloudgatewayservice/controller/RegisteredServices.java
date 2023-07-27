/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.cloudgatewayservice.controller;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RegisteredServices {

    private Flux<List<ServiceInstance>> serviceInstances;
    private WebClient webClient;
    private Map<String, List<Map>> centralServiceRegistry = new HashMap<>();


    public RegisteredServices(ReactiveDiscoveryClient discoveryClient, WebClient webClient) {
        this.serviceInstances = discoveryClient.getServices()
            .flatMap(service -> discoveryClient.getInstances(service).collectList());
        this.webClient = webClient;
    }

    @GetMapping("/services-info")
    public Flux<Map> getAllServices(){
        return getServicesInfo();
    }
    @Scheduled(fixedRate = 100000)
    public void getInstanceMap(){

        serviceInstances.filter(instances -> !instances.isEmpty()).flatMap(Flux::fromIterable).filter(serviceInstance -> serviceInstance.getServiceId().startsWith("GATEWAY"))
            .collectMap(ServiceInstance::getInstanceId).subscribe(consumer ->{

                for(ServiceInstance serviceInstance : consumer.values()) {

                    webClient.method(HttpMethod.GET).uri(String.format("%s://%s:%d/gateway/services",serviceInstance.getScheme(),serviceInstance.getHost(),serviceInstance.getPort())).retrieve()
                        .bodyToFlux(Map.class).subscribe(domainServices ->{
                            String mapKey = serviceInstance.getServiceId().toLowerCase()+serviceInstance.getHost();
                            if(centralServiceRegistry.get(mapKey) == null) {
                                List<Map> list = new ArrayList<>();
                                list.add(domainServices);
                                centralServiceRegistry.put(mapKey, list);
                            } else {
                                centralServiceRegistry.get(mapKey).add(domainServices);
                            }
                        });
                }
           });
    }

    Flux<ServiceInstance> getInstances(){
      return serviceInstances.filter(instances -> !instances.isEmpty()).flatMap(Flux::fromIterable).filter(serviceInstance -> serviceInstance.getServiceId().startsWith("GATEWAY"));
    }

    Flux<Map> getServicesInfo(){
      return getInstances().flatMap(serviceInstance -> {
          return  webClient.method(HttpMethod.GET)
                .uri(String.format("%s://%s:%d/gateway/services",serviceInstance.getScheme(),serviceInstance.getHost(),serviceInstance.getPort()))
                .retrieve()
                .bodyToMono(List.class).map(list -> {
                  String mapKey = serviceInstance.getServiceId().toLowerCase()+serviceInstance.getHost();
                  Map<String, List> newMap = new HashMap<>();
                  newMap.put(mapKey,list);
                  return newMap;
              });
        });
    }
}
