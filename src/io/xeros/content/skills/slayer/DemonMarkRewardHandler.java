package io.xeros.content.skills.slayer;

import io.xeros.model.entity.player.Player;

/**
 * Handles Demon Mark currency for Demon Hunter tasks.
 */
public class DemonMarkRewardHandler {

    public static void reward(Player player) {
        player.addDemonMarks(1);
        player.sendMessage("You receive a Demon Mark for your efforts.");
    }

    public static boolean spend(Player player, int amount) {
        if (player.getDemonMarks() >= amount) {
            player.removeDemonMarks(amount);
            return true;
        }
        return false;
    }
}
