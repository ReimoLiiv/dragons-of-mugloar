package com.dragonsofmugloar.model;

public record BuyItemResponse(
        boolean shoppingSuccess,
        int gold,
        int lives,
        int level,
        int turn
) {
}
