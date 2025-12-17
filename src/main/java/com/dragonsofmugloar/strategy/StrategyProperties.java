package com.dragonsofmugloar.strategy;

import com.dragonsofmugloar.model.strategy.MessageFilters;
import com.dragonsofmugloar.model.strategy.Probability;
import com.dragonsofmugloar.model.strategy.ReputationRules;
import com.dragonsofmugloar.model.strategy.Shop;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "strategy")
public record StrategyProperties(
        Map<Probability, Double> probabilityWeights,
        Shop shop,
        MessageFilters messageFilters,
        ReputationRules reputationRules
        ) {
}
