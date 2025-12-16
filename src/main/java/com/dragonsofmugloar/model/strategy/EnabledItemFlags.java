package com.dragonsofmugloar.model.strategy;

public record EnabledItemFlags(
        boolean healingPotion,
        boolean basicItems,
        boolean advancedItems
) {
}