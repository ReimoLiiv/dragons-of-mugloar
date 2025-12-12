package com.dragonsofmugloar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MugloarApiProperties.class)
public class RestClientConfig {

    @Bean
    RestClient restClient(MugloarApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
}