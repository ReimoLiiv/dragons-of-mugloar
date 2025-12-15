package com.dragonsofmugloar.model;

public record GameContext(
        int lives,
        int gold,
        int score,
        int turn
) {
}
