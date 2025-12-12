package com.dragonsofmugloar.model;

public record MessageTask(
        String adId,
        String message,
        int reward,
        int expiresIn,
        String encrypted,
        String probability
) {
}
