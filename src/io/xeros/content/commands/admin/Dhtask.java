package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.slayer.DemonSlayerMaster;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Arrays;

/**
 * Debug command for demon hunter tasks. Allows admins to assign
 * a specific demon hunter task or a random one if no arguments
 * are supplied.
 */
public class Dhtask extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        DemonSlayerMaster master = new DemonSlayerMaster();
        if (input == null || input.isEmpty()) {
            master.assign(player);
            player.sendMessage("Random demon hunter task assigned.");
            return;
        }
        String[] parts = input.split(" ");
        String name = parts[0].replace('_', ' ');
        int amount = 10;
        if (parts.length > 1) {
            try {
                amount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        DemonSlayerMaster.BossTier boss = Arrays.stream(DemonSlayerMaster.BossTier.values())
                .filter(b -> b.getNpcName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (boss == null) {
            player.sendMessage("Unknown boss: " + name);
            return;
        }
        DemonSlayerMaster.DemonSlayerTask task = new DemonSlayerMaster.DemonSlayerTask(boss, amount);
        player.setDemonHunterTask(task);
        player.setDemonHunterTaskProgress(amount);
        player.sendMessage("Task set: " + boss.getNpcName() + " x" + amount);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
