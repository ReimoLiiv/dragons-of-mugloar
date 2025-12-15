package com.dragonsofmugloar.strategy;

import com.dragonsofmugloar.model.GameContext;
import com.dragonsofmugloar.model.MessageTask;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class Strategy implements MessageStrategy {

    private final StrategyProperties props;

    public Strategy(StrategyProperties props) {
        this.props = props;
    }

    @Override
    public MessageTask choose(List<MessageTask> messages, GameContext ctx) {

        int minRisk = props.minRiskScoreByLives()
                .getOrDefault(ctx.lives(), Integer.MAX_VALUE);

        return messages.stream()
                .filter(task -> riskScore(task) >= minRisk)
                .max(Comparator.comparingDouble(this::score))
                .orElseGet(() ->
                        messages.stream()
                                .max(Comparator.comparingInt(this::riskScore))
                                .orElseThrow()
                );
    }

    private double score(MessageTask task) {
        double probability = props.probabilityWeights().getOrDefault(task.probability(), 0.0);
        return (task.reward() * props.rewardWeight() * probability) - (task.expiresIn() * props.expirationPenalty());
    }

    private int riskScore(MessageTask task) {
        return switch (task.probability()) {
            case "Sure thing" -> 6;
            case "Piece of cake" -> 5;
            case "Walk in the park" -> 4;
            case "Quite likely" -> 3;
            case "Risky" -> 2;
            case "Rather detrimental" -> 1;
            default -> 0;
        };
    }
}
