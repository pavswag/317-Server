package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Adjust or view Demon Mark currency for debugging.
 */
public class Dhmarks extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (input == null || input.isEmpty()) {
            player.sendMessage("Demon Marks: " + player.getDemonMarks());
            return;
        }
        try {
            int amount = Integer.parseInt(input.trim());
            if (amount >= 0) {
                player.addDemonMarks(amount);
            } else {
                player.removeDemonMarks(-amount);
            }
            player.sendMessage("Demon Marks now: " + player.getDemonMarks());
        } catch (NumberFormatException e) {
            player.sendMessage("Use ::dhmarks <amount>");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
