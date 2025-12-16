package com.dragonsofmugloar.model.responses;

public record BuyItemResponse(
        boolean shoppingSuccess,
        int gold,
        int lives,
        int level,
        int turn
) {
}
