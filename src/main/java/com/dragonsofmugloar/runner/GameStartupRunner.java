package com.dragonsofmugloar.runner;

import com.dragonsofmugloar.client.exception.MugloarApiException;
import com.dragonsofmugloar.game.GameEngine;
import com.dragonsofmugloar.model.responses.GameResult;
import jakarta.annotation.Nullable;
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
    public void run(@Nullable String... args) {
        try {
            GameResult result = gameEngine.play();
            log.info("Game finished: {}", result);
        } catch (MugloarApiException ex) {
            log.error("Game run failed due to API error, shutting down gracefully", ex);

        } catch (RuntimeException ex) {
            log.error("Fatal unexpected error occurred, shutting down", ex);

        } catch (Throwable t) {
            log.error("Catastrophic JVM error, shutting down immediately", t);
            throw t;
        }
    }

}