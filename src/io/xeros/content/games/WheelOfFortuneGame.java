package io.xeros.content.games;

import io.xeros.model.items.GameItem;

import java.security.SecureRandom;

public class WheelOfFortuneGame {

    private final int[] items;
    private final int winningIndex;

    public WheelOfFortuneGame(int[] items) {
        this.items = items;
        this.winningIndex = new SecureRandom().nextInt(items.length);
    }

    public int[] getItems() {
        return items;
    }

    public int getWinningIndex() {
        return winningIndex;
    }

    public GameItem getReward() {
        return new GameItem(items[winningIndex]);
    }
}
