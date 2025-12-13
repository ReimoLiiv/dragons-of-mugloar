package com.dragonsofmugloar.runner;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.client.exception.MugloarApiException;
import com.dragonsofmugloar.model.GameStartResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameStartupRunner implements CommandLineRunner {

    private final MugloarApiClient apiClient;

    public GameStartupRunner(MugloarApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void run(String... args) {
        try {
            GameStartResponse response = apiClient.startGame();
            log.info("Game started: {}", response);
        } catch (MugloarApiException ex) {
            log.error("Startup failed, shutting down gracefully", ex);
        }
    }
}