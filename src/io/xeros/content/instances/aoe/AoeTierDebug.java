package io.xeros.content.instances.aoe;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.util.Optional;

/**
 * Implements the ::aoe command set used for testing the
 * data driven AOE boss tiers and reward system.
 */
public class AoeTierDebug extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0) {
            player.sendMessage("Usage: ::aoe <tier|rewards> ...");
            return;
        }
        String category = parts[0].toLowerCase();
        if ("tier".equals(category)) {
            if (parts.length < 2) {
                player.sendMessage("Usage: ::aoe tier <open|status|start|set|simulate|reload>");
                return;
            }
            String sub = parts[1].toLowerCase();
            switch (sub) {
                case "open":
                    player.start(new io.xeros.content.dialogue.impl.BossInstanceDialogue(player));
                    break;
                case "status":
                    int size = AoeTierRepo.size();
                    String sample = AoeTierRepo.get().stream().limit(3)
                            .map(def -> def.zoneName)
                            .collect(Collectors.joining(", "));
                    String path = AoeBossTierLoader.defaultFile().toFile().getAbsolutePath();
                    player.sendMessage("Repo size=" + size + " sample=" + sample + " file=" + path);
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
                    Arrays.stream(Server.playerHandler.players)
                            .filter(p -> p != null && AoeTierController.getActiveTier(p) > 0)
                            .forEach(p -> AoeTierController.endTier(p, false));
                    AoeBossTierLoader.loadAllOrWarn("reload");
                    player.sendMessage("AOE tier definitions reloaded. Count=" + AoeTierRepo.size());
                    break;
                default:
                    player.sendMessage("Unknown subcommand: " + sub);
            }
        } else if ("rewards".equals(category)) {
            if (parts.length < 2) {
                player.sendMessage("Usage: ::aoe rewards <show|clear|bank|reload|simulate>");
                return;
            }
            String sub = parts[1].toLowerCase();
            switch (sub) {
                case "show":
                    AoeRewardTracker tracker = AoeTierController.getTracker(player);
                    if (tracker != null) {
                        AoeTreasureTrailsAdapter.openLootViewer(player, "AOE Tier Rewards", tracker.snapshot());
                    } else {
                        player.sendMessage("No active rewards.");
                    }
                    break;
                case "clear":
                    AoeRewardTracker t = AoeTierController.getTracker(player);
                    if (t != null) t.clear();
                    player.sendMessage("AOE reward tracker cleared.");
                    break;
                case "bank":
                    if (parts.length >= 3) {
                        boolean on = parts[2].equalsIgnoreCase("on");
                        AoeDropInterceptor.setBankOverride(player, on);
                        player.sendMessage("AOE auto bank " + (on ? "enabled" : "disabled"));
                    } else {
                        player.sendMessage("Usage: ::aoe rewards bank on|off");
                    }
                    break;
                case "reload":
                    if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                        player.sendMessage("Admin only command.");
                        return;
                    }
                    AoeTierRewardsLoader.reload();
                    player.sendMessage("AOE tier rewards reloaded.");
                    break;
                case "simulate":
                    if (!Right.ADMINISTRATOR.isOrInherits(player)) {
                        player.sendMessage("Admin only command.");
                        return;
                    }
                    if (parts.length >= 4) {
                        int id = Integer.parseInt(parts[2]);
                        int amt = Integer.parseInt(parts[3]);
                        AoeDropInterceptor.awardInsideAoe(player, new GameItem(id, amt));
                    } else {
                        player.sendMessage("Usage: ::aoe rewards simulate <itemId> <amount>");
                    }
                    break;
                default:
                    player.sendMessage("Unknown subcommand: " + sub);
            }
        } else {
            player.sendMessage("Usage: ::aoe <tier|rewards> ...");
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
