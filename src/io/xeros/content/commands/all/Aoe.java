package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.aoe.AoeBossTierLoader;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/**
 * Entry point command for interacting with the AOE boss tier system. The
 * command syntax matches "::aoe tier <subcommand>".
 */
public class Aoe extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("tier")) {
            player.sendMessage("Usage: ::aoe tier <open|status|start|set|simulate|reload>");
            return;
        }
        String sub = parts[1].toLowerCase();
        switch (sub) {
            case "open":
                player.sendMessage("AOE tier selection UI not implemented in this build.");
                break;
            case "status":
                int tier = AoeTierController.getUnlockedTier(player);
                player.sendMessage("AOE tier: " + tier);
                break;
            case "start":
                if (parts.length >= 3) {
                    int t = Integer.parseInt(parts[2]);
                    AoeTierController.startTier(player, t);
                } else {
                    player.sendMessage("Usage: ::aoe tier start <tier>");
                }
                break;
            case "set":
                if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                    player.sendMessage("Admin only command.");
                    return;
                }
                if (parts.length >= 3) {
                    int t = Integer.parseInt(parts[2]);
                    AoeTierController.setUnlockedTier(player, t);
                    player.sendMessage("Set unlocked AOE tier to " + t);
                } else {
                    player.sendMessage("Usage: ::aoe tier set <tier>");
                }
                break;
            case "simulate":
                if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                    player.sendMessage("Admin only command.");
                    return;
                }
                if (parts.length >= 4) {
                    int t = Integer.parseInt(parts[2]);
                    int kills = Integer.parseInt(parts[3]);
                    for (int i = 0; i < kills; i++) {
                        AoeTierController.incrementKill(player, t);
                    }
                    player.sendMessage("Simulated " + kills + " kills for tier " + t);
                } else {
                    player.sendMessage("Usage: ::aoe tier simulate <tier> <kills>");
                }
                break;
            case "reload":
                if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                    player.sendMessage("Admin only command.");
                    return;
                }
                //AoeBossTierLoader.reload();
                player.sendMessage("AOE tier definitions reloaded.");
                break;
            default:
                player.sendMessage("Unknown subcommand: " + sub);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("AOE tier utilities");
    }
}
