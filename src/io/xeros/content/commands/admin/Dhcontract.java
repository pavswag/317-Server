package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.slayer.DemonSlayerContract;
import io.xeros.content.skills.slayer.DemonSlayerMaster;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Arrays;

/**
 * Debug command to set or view demon slayer contracts.
 */
public class Dhcontract extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (input == null || input.isEmpty()) {
            player.getDemonContract().ifPresentOrElse(c ->
                    player.sendMessage("Contract: " + c.getTarget().getNpcName() + " x" + c.getAmount()),
                    () -> player.sendMessage("No active contract."));
            return;
        }
        String[] parts = input.split(" ");
        String name = parts[0].replace('_', ' ');
        int amount = 1;
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
        player.setDemonContract(new DemonSlayerContract(boss, amount));
        player.sendMessage("Contract set: defeat " + amount + " " + boss.getNpcName());
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
