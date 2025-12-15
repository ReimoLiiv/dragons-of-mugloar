package com.dragonsofmugloar.game;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.model.*;
import com.dragonsofmugloar.strategy.MessageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GameEngine {

    private final MugloarApiClient apiClient;
    private final MessageStrategy strategy;

    public GameEngine(MugloarApiClient apiClient, MessageStrategy strategy) {
        this.apiClient = apiClient;
        this.strategy = strategy;
    }

    public GameResult playOnce() {
        GameStartResponse game = apiClient.startGame();

        int lives = game.lives();
        int gold = game.gold();
        int score = game.score();
        int turn = game.turn();
        String gameId = game.gameId();

        log.info(
                "Game started: id={}, lives={}, gold={}, score={}, turn={}",
                gameId, lives, gold, score, turn
        );

        while (lives > 0) {
            List<MessageTask> messages = apiClient.getMessages(gameId);
            log.debug("Turn {}: fetched {} messages", turn, messages.size());

            if (messages.isEmpty()) {
                log.info("Turn {}: no messages left, ending game", turn);
                break;
            }

            GameContext context = new GameContext(lives, gold, score, turn);

            MessageTask chosen = strategy.choose(messages, context);

            log.info(
                    "Turn {}: chosen adId={}, reward={}, expiresIn={}, probability={}, message=\"{}\"",
                    turn,
                    chosen.adId(),
                    chosen.reward(),
                    chosen.expiresIn(),
                    chosen.probability(),
                    chosen.message()
            );

            SolveResponse result =
                    apiClient.solveMessage(gameId, chosen.adId());

            log.info(
                    "Turn {} result: success={}, lives {}->{} gold {}->{} score {}->{} nextTurn={}, msg=\"{}\"",
                    turn,
                    result.success(),
                    lives, result.lives(),
                    gold, result.gold(),
                    score, result.score(),
                    result.turn(),
                    result.message()
            );

            lives = result.lives();
            gold = result.gold();
            score = result.score();
            turn = result.turn();
        }

        log.info(
                "Game finished: id={}, finalScore={}, finalGold={}, finalLives={}, finalTurn={}",
                gameId, score, gold, lives, turn
        );

        return new GameResult(gameId, score);
    }
}
