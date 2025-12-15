package com.dragonsofmugloar.client;

import com.dragonsofmugloar.client.exception.MugloarClientException;
import com.dragonsofmugloar.client.exception.MugloarServerException;
import com.dragonsofmugloar.config.MugloarApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@TestConfiguration
@EnableConfigurationProperties(MugloarApiProperties.class)
public class TestRestClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder, MugloarApiProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new MugloarClientException(buildErrorMessage(request, response));
                        })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new MugloarServerException(buildErrorMessage(request, response));
                        })
                .build();
    }

    private static String buildErrorMessage(HttpRequest request, ClientHttpResponse response) {
        try (var body = response.getBody()) {
            return """
                    Request failed:
                    %s %s
                    Status: %s
                    Response: %s
                    """.formatted(
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    new String(body.readAllBytes(), StandardCharsets.UTF_8)
            );
        } catch (Exception _) {
            return "<unable to read response>";
        }
    }
}
