package io.xeros.content.commands.owner;

import io.xeros.content.activityboss.GlobalBossActivityManager;
import io.xeros.content.activityboss.GlobalBossType;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Setbosscooldown extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            player.sendMessage("Usage: ::setbosscooldown bossName seconds");
            return;
        }
        try {
            GlobalBossType type = GlobalBossType.valueOf(parts[0].toUpperCase());
            long seconds = Long.parseLong(parts[1]);
            GlobalBossActivityManager.setCooldown(type, seconds * 1000);
            player.sendMessage("Cooldown for " + type.getName() + " set to " + seconds + "s");
        } catch (Exception e) {
            player.sendMessage("Invalid input.");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Set global boss cooldown in seconds");
    }
}
