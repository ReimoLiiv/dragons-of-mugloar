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

    public GameEngine(MugloarApiClient apiClient, Strategy strategy) {
        this.apiClient = apiClient;
        this.strategy = strategy;
    }

    public GameResult play() {
        GameStartResponse start = apiClient.startGame();
        ReputationResponse reputation;
        GameState state = new GameState(start.lives(), start.gold(), start.score(), start.turn());

        String gameId = start.gameId();
        logGameStart(gameId, state);

        while (state.isAlive()) {
            reputation = apiClient.investigateReputation(gameId);
            boolean progressed = playTurn(gameId, state, reputation);
            if (!progressed) break;
        }

        return new GameResult(gameId, state.getScore());
    }

    private boolean playTurn(String gameId, GameState state, ReputationResponse reputation) {
        boolean didSomeMessage = false;

        Optional<MessageTask> task = pickMessage(gameId, reputation);

        if (task.isPresent()) {
            solveMessage(gameId, state, task.get());
            didSomeMessage = true;
        }
        boolean boughtSomething = buyItems(gameId, state);

        return didSomeMessage || boughtSomething;
    }

    private Optional<MessageTask> pickMessage(String gameId, ReputationResponse reputation) {
        List<MessageTask> messages = apiClient.getMessages(gameId);

        return strategy.chooseMessage(messages, reputation);
    }

    private void solveMessage(String gameId, GameState state, MessageTask task) {
        SolveResponse result = apiClient.solveMessage(gameId, task.adId());
        logMissionResult(state, result);

        state.applyNewState(result.lives(), result.gold(), result.score(), result.turn());
    }

    private boolean buyItems(String gameId, GameState state) {
        boolean boughtAnything = false;

        while (attemptPurchase(gameId, state)) {
            boughtAnything = true;
        }

        return boughtAnything;
    }

    private boolean attemptPurchase(String gameId, GameState state) {
        Optional<String> purchase = strategy.decideShopPurchase(state.getGold(), state.getLives(),
                state.getHealingPotionsBought());

        if (purchase.isEmpty()) {
            return false;
        }

        String item = purchase.get();
        BuyItemResponse buy = apiClient.buyItem(gameId, item);
        logShopPurchase(item, buy);

        if (!buy.shoppingSuccess()) {
            return false;
        }

        state.applyNewState(buy.lives(), buy.gold(), state.getScore(), state.getTurn());

        if ("hpot".equals(item)) {
            state.incrementHealingPotionsBought();
        }

        return true;
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
