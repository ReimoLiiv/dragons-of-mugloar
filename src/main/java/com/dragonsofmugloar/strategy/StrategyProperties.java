package com.dragonsofmugloar.strategy;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "strategy")
public record StrategyProperties(

        Map<Integer, Integer> minRiskScoreByLives,
        Map<String, Double> probabilityWeights,
        double rewardWeight,
        double expirationPenalty

) {
}
