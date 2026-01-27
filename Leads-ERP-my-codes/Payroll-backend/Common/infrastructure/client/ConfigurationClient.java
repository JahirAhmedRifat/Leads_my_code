package com.leads.microcube.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ConfigurationClient {
    private final WebClient client;
    private final ServiceDiscoveryClient discoveryClient;

    public ConfigurationClient(WebClient webClient, ServiceDiscoveryClient discoveryClient) {
        this.client = webClient;
        this.discoveryClient = discoveryClient;
    }

    public Mono<String> test() {
        String url = discoveryClient.getPortalUrl();
        return client.get()
                .uri(url + "test/test")
                .retrieve()
                .bodyToMono(String.class);
    }
}
