package com.dragonsofmugloar.game;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.model.responses.*;
import com.dragonsofmugloar.model.strategy.GameState;
import com.dragonsofmugloar.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GameEngine {

    private final MugloarApiClient apiClient;
    private final Strategy strategy;

    private int healingPotionsBought = 0;

    public GameEngine(MugloarApiClient apiClient, Strategy strategy) {
        this.apiClient = apiClient;
        this.strategy = strategy;
    }

    public GameResult playOnce() {
        GameStartResponse start = apiClient.startGame();

        GameState state = new GameState(
                start.lives(),
                start.gold(),
                start.score(),
                start.turn()
        );

        String gameId = start.gameId();
        logGameStart(gameId, state);

        while (state.isAlive()) {
            boolean progressed = playTurn(gameId, state);
            if (!progressed) break;
        }

        return new GameResult(gameId, state.getScore());
    }

    private boolean playTurn(String gameId, GameState state) {
        Optional<MessageTask> task = pickMission(gameId);

        if (task.isPresent()) {
            solveMission(gameId, state, task.get());
            buyUntilExhausted(gameId, state);
            return true;
        }

        return buyUntilExhausted(gameId, state);
    }

    private Optional<MessageTask> pickMission(String gameId) {
        List<MessageTask> messages = apiClient.getMessages(gameId);
        for (MessageTask m : messages) {
            log.info("Mission: reward={} expiresIn={} prob={}", m.reward(), m.expiresIn(), m.probability());
        }
        return strategy.choose(messages);
    }

    private void solveMission(String gameId, GameState state, MessageTask task) {
        logMissionChosen(state.getTurn(), task);

        SolveResponse result = apiClient.solveMessage(gameId, task.adId());
        logMissionResult(state, result);

        state.applySolveResult(result.lives(), result.gold(), result.score(), result.turn());
    }

    private boolean buyUntilExhausted(String gameId, GameState state) {
        boolean boughtAnything = false;
        boolean canContinue = true;
        while (canContinue) {
            List<String> purchases = strategy.decideShopPurchases(state.getGold(), state.getLives(),
                    healingPotionsBought);

            boolean boughtThisRound = false;

            for (String item : purchases) {
                BuyItemResponse buy = apiClient.buyItem(gameId, item);
                logShopPurchase(item, buy);

                if (buy.shoppingSuccess()) {
                    boughtAnything = true;
                    boughtThisRound = true;

                    state.applySolveResult(buy.lives(), buy.gold(), state.getScore(), state.getTurn());

                    if ("hpot".equals(item)) {
                        healingPotionsBought++;
                    }
                }
            }

            canContinue = boughtThisRound && !purchases.isEmpty();
        }

        return boughtAnything;
    }

    private void logGameStart(String gameId, GameState state) {
        log.info(
                "Game started: id={}, lives={}, gold={}, score={}, turn={}",
                gameId,
                state.getLives(),
                state.getGold(),
                state.getScore(),
                state.getTurn()
        );
    }

    private void logMissionChosen(int turn, MessageTask task) {
        log.info(
                "Turn {}: chosen adId={}, reward={}, expiresIn={}, probability={}, message=\"{}\"",
                turn,
                task.adId(),
                task.reward(),
                task.expiresIn(),
                task.probability(),
                task.message()
        );
    }

    private void logMissionResult(GameState state, SolveResponse result) {
        log.info(
                "Turn {} result: success={}, lives {}->{} gold {}->{} score {}->{} nextTurn={}, msg=\"{}\"",
                state.getTurn(),
                result.success(),
                state.getLives(), result.lives(),
                state.getGold(), result.gold(),
                state.getScore(), result.score(),
                result.turn(),
                result.message()
        );
    }

    private void logShopPurchase(String item, BuyItemResponse buy) {
        log.info(
                "SHOP: Bought item={} success={}, gold={}, lives={}, level={}, turn={}",
                item,
                buy.shoppingSuccess(),
                buy.gold(),
                buy.lives(),
                buy.level(),
                buy.turn()
        );
    }
}
