package io.xeros.content.instances.aoe;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import java.util.Optional;

/**
 * Minimal debugging commands for AOE tiers.
 */
public class AoeDebug extends Command {

    @Override
    public String getCommand() {
        return "aoe";
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0 || parts[0].isEmpty()) {
            player.sendMessage("Usage: ::aoe test <tier> | ::aoe reload");
            return;
        }
        String sub = parts[0].toLowerCase();
        switch (sub) {
            case "test":
                if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                    player.sendMessage("Admin only.");
                    return;
                }
                if (parts.length < 2) {
                    player.sendMessage("Usage: ::aoe test <tier>");
                    return;
                }
                int t = Integer.parseInt(parts[1]);
                AoeBossTierDef def = AoeBossTierLoader.getTier(t);
                if (def == null) {
                    player.sendMessage("Unknown tier " + t);
                    return;
                }
                int height = AoeInstanceService.allocateHeight("dbg-" + t);
                if (!AoeInstanceService.buildDynamicRegion(def, height)) {
                    player.sendMessage("Map build failed");
                    return;
                }
                int[] pos = AoeInstanceService.computeSpawn(def, height);
                AoeInstanceService.teleportIntoInstance(player, pos[0], pos[1], height);
                break;
            case "reload":
                if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                    player.sendMessage("Admin only.");
                    return;
                }
                AoeBossTierLoader.loadAllOrWarn("debug");
                player.sendMessage("AOE tiers reloaded: " + AoeTierRepo.size());
                break;
            default:
                player.sendMessage("Usage: ::aoe test <tier> | ::aoe reload");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("AOE debug utilities");
    }
}
