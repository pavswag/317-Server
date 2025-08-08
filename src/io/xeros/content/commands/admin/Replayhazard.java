package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.content.instances.hazard.EnvironmentalHazardType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Replays the last triggered hazard of a given type. */
public class Replayhazard extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            c.sendMessage("Usage: ::replayhazard <zoneId> <hazardType>");
            return;
        }
        int zoneId;
        try { zoneId = Integer.parseInt(parts[0]); } catch (NumberFormatException e) { zoneId = -1; }
        EnvironmentalHazardType type;
        try { type = EnvironmentalHazardType.valueOf(parts[1].toUpperCase()); } catch (Exception e) { c.sendMessage("Unknown type."); return; }
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
        if (area.getHazardScheduler().replay(type)) {
            c.sendMessage("Replayed hazard " + type + ".");
        } else {
            c.sendMessage("No hazard recorded for " + type + ".");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("replay last hazard");
    }
}
