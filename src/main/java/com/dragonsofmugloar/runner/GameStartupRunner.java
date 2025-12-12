package com.dragonsofmugloar.runner;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.model.GameStartResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameStartupRunner implements CommandLineRunner {

    private final MugloarApiClient apiClient;

    public GameStartupRunner(MugloarApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void run(String... args) {
        GameStartResponse response = apiClient.startGame();
        System.out.println("Game started: " + response);
    }
}