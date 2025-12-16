package com.dragonsofmugloar.model.strategy;

import java.util.List;

public record MessageFilters(
        List<String> forbiddenPhrases
) {
}