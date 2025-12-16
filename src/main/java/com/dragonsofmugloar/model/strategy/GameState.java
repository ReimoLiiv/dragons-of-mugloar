package com.dragonsofmugloar.model.strategy;

import lombok.Getter;

@Getter
public class GameState {

    private int lives;
    private int gold;
    private int score;
    private int turn;

    public GameState(int lives, int gold, int score, int turn) {
        this.lives = lives;
        this.gold = gold;
        this.score = score;
        this.turn = turn;
    }

    public void applySolveResult(
            int lives,
            int gold,
            int score,
            int turn
    ) {
        this.lives = lives;
        this.gold = gold;
        this.score = score;
        this.turn = turn;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}
