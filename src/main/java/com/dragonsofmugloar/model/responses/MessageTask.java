package com.dragonsofmugloar.model.responses;

public record MessageTask(
        String adId,
        String message,
        int reward,
        int expiresIn,
        String probability
) {
}
