package com.dragonsofmugloar.runner;

import com.dragonsofmugloar.client.exception.MugloarApiException;
import com.dragonsofmugloar.game.GameEngine;
import com.dragonsofmugloar.model.responses.GameResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameStartupRunner implements CommandLineRunner {

    private final GameEngine gameEngine;

    public GameStartupRunner(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run(String... args) {
        try {
            GameResult result = gameEngine.playOnce();
            log.info("Game finished: {}", result);
        } catch (MugloarApiException ex) {
            log.error("Game run failed, shutting down gracefully", ex);
        }
    }

}