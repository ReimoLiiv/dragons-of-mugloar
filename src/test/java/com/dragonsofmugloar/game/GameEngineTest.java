package com.dragonsofmugloar.game;

import com.dragonsofmugloar.client.MugloarApiClient;
import com.dragonsofmugloar.model.GameResult;
import com.dragonsofmugloar.model.GameStartResponse;
import com.dragonsofmugloar.model.MessageTask;
import com.dragonsofmugloar.model.SolveResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock
    MugloarApiClient apiClient;

    @InjectMocks
    GameEngine engine;

    @Test
    void playsUntilLivesRunOut() {
        when(apiClient.startGame())
                .thenReturn(new GameStartResponse(
                        "game1", 2, 0, 0, 0, 0, 0
                ));

        when(apiClient.getMessages("game1"))
                .thenReturn(List.of(
                        new MessageTask("ad1", "msg", 10, 5, null, "Easy")
                ));

        when(apiClient.solveMessage("game1", "ad1"))
                .thenReturn(new SolveResponse(
                        true, 0, 10, 10, 10, 1, "done"
                ));

        GameResult result = engine.playOnce();

        assertThat(result.score()).isEqualTo(10);
        verify(apiClient, atLeastOnce()).solveMessage(any(), any());
    }
}

