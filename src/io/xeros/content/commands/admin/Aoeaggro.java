package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.aoe.AoeNpcSpawner;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Aoeaggro extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!hasPrivilege(player)) {
            player.sendMessage("This command is for admins only.");
            return;
        }
        var instOpt = AoeTierRepo.instanceForPlayer(player);
        if (instOpt.isEmpty()) {
            player.sendMessage("You are not inside an AOE instance.");
            return;
        }
        boolean enabled = AoeNpcSpawner.toggleForceAggro(instOpt.get());
        player.sendMessage("AOE aggro debug " + (enabled ? "enabled" : "disabled") + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("toggles forced aggression for aoe instance npcs");
    }
}
