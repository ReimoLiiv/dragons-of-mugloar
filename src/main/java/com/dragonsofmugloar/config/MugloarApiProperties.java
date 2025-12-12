package com.dragonsofmugloar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "mugloar")
public record MugloarApiProperties(
        String baseUrl,
        Duration timeout
) {
}