package com.dragonsofmugloar.model.responses;

public record GameStartResponse(
        String gameId,
        int lives,
        int gold,
        int level,
        int score,
        int highScore,
        int turn
) {
}