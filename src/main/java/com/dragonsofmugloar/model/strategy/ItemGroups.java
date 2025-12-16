package com.dragonsofmugloar.model.strategy;

import java.util.List;

public record ItemGroups(
        List<String> basic,
        List<String> advanced
) {
}