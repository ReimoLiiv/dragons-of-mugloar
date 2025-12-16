package com.dragonsofmugloar.config;

import com.dragonsofmugloar.client.exception.MugloarClientException;
import com.dragonsofmugloar.client.exception.MugloarServerException;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableRetry
public class MugloarRestClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder, MugloarApiProperties properties) {

        var requestFactory = ClientHttpRequestFactoryBuilder.httpComponents()
                .build(HttpClientSettings.defaults()
                        .withTimeouts(properties.connectTimeout(), properties.readTimeout()));

        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new MugloarClientException(buildErrorMessage(request, response));
                        })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new MugloarServerException(buildErrorMessage(request, response));
                        }
                )
                .build();
    }

    private static String buildErrorMessage(HttpRequest request, ClientHttpResponse response
    ) {
        return """
                Request failed:
                %s %s
                Status: %s
                Response: %s
                """.formatted(request.getMethod(), request.getURI(), readStatus(response), readBody(response));
    }

    private static String readStatus(ClientHttpResponse response) {
        try {
            return response.getStatusCode().toString();
        } catch (Exception _) {
            return "<unknown status>";
        }
    }

    private static String readBody(ClientHttpResponse response) {
        try (var body = response.getBody()) {
            return new String(body.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception _) {
            return "<response body is empty or unable to read>";
        }
    }
}
