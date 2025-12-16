package com.dragonsofmugloar.model.strategy;

public record ShopProperties(
        int emergencyHealLivesBelow,
        EnabledItemFlags enabled,
        BuyThresholds buyThresholds,
        ItemGroups itemGroups,
        ItemBuyingLimits limits
) {
}
