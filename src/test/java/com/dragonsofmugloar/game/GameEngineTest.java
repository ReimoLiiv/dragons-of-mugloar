package com.dragonsofmugloar.game;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.model.responses.*;
import com.dragonsofmugloar.strategy.Strategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock
    MugloarApiClient apiClient;

    @Mock
    Strategy strategy;

    @InjectMocks
    GameEngine engine;

    @Test
    void playsUntilLivesRunOut() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse("game1", 2, 0, 0, 0, 0, 0));

        MessageTask task = new MessageTask("ad1", "msg", 10, 5, "Piece of cake");

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of(task));

        when(strategy.chooseMessage(any(), any()))
                .thenReturn(Optional.of(task));

        when(apiClient.solveMessage("game1", "ad1"))
                .thenReturn(new SolveResponse(true, 0, 10, 10, 10, 1, "done"));

        GameResult result = engine.play();
        assertThat(result.score()).isEqualTo(10);
        verify(apiClient, atLeastOnce()).solveMessage(any(), any());
    }

    @Test
    void endsGameWhenNoMissionsAndNoShopActions() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse("game1", 3, 0, 0, 0, 0, 0));

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of());

        when(strategy.decideShopPurchase(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        GameResult result = engine.play();
        assertThat(result.score()).isZero();
        verify(apiClient, never()).solveMessage(any(), any());
    }

    @Test
    void buysHealingPotionWhenLivesLow() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse("game1", 1, 100, 0, 0, 0, 0));

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of());

        when(strategy.decideShopPurchase(100, 1, 0))
                .thenReturn(Optional.of("hpot"))
                .thenReturn(Optional.empty());

        when(apiClient.buyItem("game1", "hpot"))
                .thenReturn(new BuyItemResponse(true, 50, 2, 0, 0));

        engine.play();

        verify(apiClient).buyItem("game1", "hpot");
    }

    @Test
    void solvesMissionThenBuysItem() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse("game1", 3, 200, 0, 0, 0, 0));

        MessageTask task =
                new MessageTask("ad1", "msg", 10, 5, "Piece of cake");

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of(task))
                .thenReturn(List.of());

        when(strategy.chooseMessage(any(), any()))
                .thenReturn(Optional.of(task))
                .thenReturn(Optional.empty());

        when(apiClient.solveMessage("game1", "ad1"))
                .thenReturn(new SolveResponse(true, 3, 250, 10, 10, 1, "ok"));

        when(strategy.decideShopPurchase(250, 3, 0))
                .thenReturn(Optional.of("cs"))
                .thenReturn(Optional.empty());

        when(apiClient.buyItem("game1", "cs"))
                .thenReturn(new BuyItemResponse(true, 200, 3, 0, 0));

        engine.play();

        verify(apiClient).solveMessage("game1", "ad1");
        verify(apiClient).buyItem("game1", "cs");
    }


    @Test
    void doesNotBuyMoreThanMaxHealingPotions() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse("game1", 1, 500, 0, 0, 0, 0));

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of());

        when(strategy.decideShopPurchase(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        engine.play();

        verify(apiClient, never()).buyItem(any(), eq("hpot"));
    }
}

