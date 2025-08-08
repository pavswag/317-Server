package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.content.instances.hazard.EnvironmentalHazardPattern;
import io.xeros.content.instances.hazard.EnvironmentalHazardPattern.PatternType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Spawns a hazard pattern immediately for testing. */
public class Spawnhazard extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        String[] parts = input.split(" ");
        if (parts.length < 3) {
            c.sendMessage("Usage: ::spawnhazard <zoneId> <hazardType> <tier>");
            return;
        }
        int zoneId;
        try {
            zoneId = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            zoneId = -1;
        }
        int dummyTier;
        try {
            dummyTier = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            dummyTier = 1;
        }
        PatternType type;
        try {
            type = PatternType.valueOf(parts[1].toUpperCase());
        } catch (Exception e) {
            c.sendMessage("Unknown hazard type.");
            return;
        }
        BossTier[] tiers = BossTier.values();
        if (zoneId < 1 || zoneId > tiers.length) {
            c.sendMessage("Invalid zone id.");
            return;
        }
        BossInstanceArea area = BossInstanceManager.getAny(tiers[zoneId - 1]);
        if (area == null) {
            c.sendMessage("No active instance for that tier.");
            return;
        }
        EnvironmentalHazardPattern pattern = new EnvironmentalHazardPattern();
        pattern.setType(type);
        pattern.activate(area);
        c.sendMessage("Spawned hazard " + type + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("spawn hazard pattern");
    }
}
