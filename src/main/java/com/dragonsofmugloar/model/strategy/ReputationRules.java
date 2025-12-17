package com.dragonsofmugloar.model.strategy;

public record ReputationRules(
        boolean enabled,
        int maxStateDeficit
) {
}
