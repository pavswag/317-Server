package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.content.instances.hazard.HazardDebugLogger;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Displays recent hazard reaction callbacks from NPCs. */
public class Debugnpcreactions extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        String[] parts = input.split(" ");
        if (parts.length < 1 || parts[0].isEmpty()) {
            c.sendMessage("Usage: ::debugnpcreactions <zoneId>");
            return;
        }
        int zoneId;
        try {
            zoneId = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            zoneId = -1;
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
        for (String log : HazardDebugLogger.getNpcLogs(area)) {
            c.sendMessage(log);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("debug npc hazard reactions");
    }
}
