package com.dragonsofmugloar.model;

public record SolveResponse(
        boolean success,
        int lives,
        int gold,
        int score,
        int highScore,
        int turn,
        String message
) {
}
