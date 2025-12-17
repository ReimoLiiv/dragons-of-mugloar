package com.dragonsofmugloar.strategy;

import com.dragonsofmugloar.model.responses.MessageTask;
import com.dragonsofmugloar.model.responses.ReputationResponse;
import com.dragonsofmugloar.model.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyTest {

    private Strategy strategy;

    @BeforeEach
    void setUp() {
        StrategyProperties props = new StrategyProperties(
                Map.of(Probability.PIECE_OF_CAKE, 0.9, Probability.RISKY, 0.5),
                new Shop(
                        2,
                        new EnabledItemFlags(true, true, true),
                        new BuyThresholds(100, 300),
                        new ItemGroups(List.of("cs"), List.of("ch")),
                        new ItemBuyingLimits(5)
                ), new MessageFilters(List.of("forbidden")),
                new ReputationRules(true, 1)
        );

        strategy = new Strategy(props);
    }

    @Test
    void allowsPeopleMissionWhenDeficitWithinLimit() {
        MessageTask task = new MessageTask("1", "help people in village", 10, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(4, -5, 0);

        Optional<MessageTask> chosen = strategy.chooseMessage(List.of(task), reputation);

        assertThat(chosen).isPresent();
    }

    @Test
    void rejectsPeopleMissionWhenDeficitTooHigh() {
        MessageTask task = new MessageTask("1", "help people in village", 10, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(4, -6, 0);

        Optional<MessageTask> chosen = strategy.chooseMessage(List.of(task), reputation);

        assertThat(chosen).isEmpty();
    }

    @Test
    void ignoresReputationWhenMessageDoesNotContainPeople() {
        MessageTask task = new MessageTask("1", "steal gold from merchant", 10, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(0, -10, 0);

        Optional<MessageTask> chosen = strategy.chooseMessage(List.of(task), reputation);

        assertThat(chosen).isPresent();
    }

    @Test
    void ignoresReputationRulesWhenDisabled() {
        StrategyProperties props = new StrategyProperties(
                Map.of(Probability.PIECE_OF_CAKE, 0.9),
                new Shop(
                        2,
                        new EnabledItemFlags(true, true, true),
                        new BuyThresholds(100, 300),
                        new ItemGroups(List.of("cs"), List.of("ch")),
                        new ItemBuyingLimits(5)
                ),
                new MessageFilters(List.of()),
                new ReputationRules(false, 0)
        );

        Strategy localStrategy = new Strategy(props);

        MessageTask task = new MessageTask("1", "help people", 10, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(0, -100, 0);

        Optional<MessageTask> chosen = localStrategy.chooseMessage(List.of(task), reputation);

        assertThat(chosen).isPresent();
    }

    @Test
    void choosesTaskWithHighestExpectedScore() {
        MessageTask low = new MessageTask("1", "msg", 10, 5, "Risky");

        MessageTask high = new MessageTask("2", "msg", 20, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(10, 0, 0);

        Optional<MessageTask> chosen = strategy.chooseMessage(List.of(low, high), reputation);

        assertThat(chosen).isPresent();
        assertThat(chosen.get().adId()).isEqualTo("2");
    }

    @Test
    void filtersForbiddenMessages() {
        MessageTask forbidden = new MessageTask("1", "this is forbidden", 10, 5, "Piece of cake");

        ReputationResponse reputation = new ReputationResponse(10, 0, 0);

        Optional<MessageTask> chosen = strategy.chooseMessage(List.of(forbidden), reputation);

        assertThat(chosen).isEmpty();
    }
}
