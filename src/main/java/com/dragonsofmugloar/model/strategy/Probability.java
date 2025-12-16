package com.dragonsofmugloar.model.strategy;

import java.util.Arrays;
import java.util.Optional;

public enum Probability {
    SURE_THING("Sure thing"),
    PIECE_OF_CAKE("Piece of cake"),
    WALK_IN_THE_PARK("Walk in the park"),
    QUITE_LIKELY("Quite likely"),
    HMMM("Hmmm...."),
    GAMBLE("Gamble"),
    RISKY("Risky"),
    RATHER_DETRIMENTAL("Rather detrimental"),
    PLAYING_WITH_FIRE("Playing with fire"),
    SUICIDE_MISSION("Suicide mission");

    private final String apiValue;

    Probability(String apiValue) {
        this.apiValue = apiValue;
    }

    public static Optional<Probability> fromApi(String value) {
        return Arrays.stream(values())
                .filter(probability -> probability.apiValue.equals(value))
                .findFirst();
    }
}