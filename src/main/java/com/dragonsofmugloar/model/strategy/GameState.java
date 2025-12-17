package com.dragonsofmugloar.model.strategy;

import lombok.Getter;

@Getter
public class GameState {

    private int lives;
    private int gold;
    private int score;
    private int turn;

    private int healingPotionsBought;

    public GameState(int lives, int gold, int score, int turn) {
        this.lives = lives;
        this.gold = gold;
        this.score = score;
        this.turn = turn;
        this.healingPotionsBought = 0;
    }

    public void applyNewState(int lives, int gold, int score, int turn) {
        this.lives = lives;
        this.gold = gold;
        this.score = score;
        this.turn = turn;
    }

    public void incrementHealingPotionsBought() {
        this.healingPotionsBought++;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}
