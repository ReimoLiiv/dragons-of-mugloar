package com.dragonsofmugloar.model.strategy;

public record Shop(
        int emergencyHealLivesBelow,
        EnabledItemFlags enabled,
        BuyThresholds buyThresholds,
        ItemGroups itemGroups,
        ItemBuyingLimits limits
) {
}
