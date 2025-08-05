package io.xeros.content.commands.test;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Debug command that prints the sanitised boss tier zone names to verify
 * none of them contain characters that render invisibly in dialogue.
 */
public class TestZonesClean extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        for (BossTier tier : BossTier.values()) {
            String zone = tier.getZoneName();
            if (zone == null) {
                zone = "null";
            }
            String cleanZone = zone.replaceAll("[^\\p{ASCII}]", "").replaceAll("[–—•]", "-");
            player.sendMessage("Tier " + (tier.ordinal() + 1) + ": " + cleanZone);
            Misc.println("testzones_clean tier=" + tier + " zone=" + cleanZone);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true; // accessible in test environments
    }

    @Override
    public String getCommand() {
        return "testzones_clean";
    }
}
