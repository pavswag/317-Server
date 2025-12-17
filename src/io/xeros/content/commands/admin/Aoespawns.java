package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.aoe.AoeInstance;
import io.xeros.content.instances.aoe.AoeNpcSpawner;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.List;
import java.util.Optional;

public class Aoespawns extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!hasPrivilege(player)) {
            player.sendMessage("This command is for admins only.");
            return;
        }
        Optional<AoeInstance> instOpt = AoeTierRepo.instanceForPlayer(player);
        if (!instOpt.isPresent()) {
            player.sendMessage("You are not inside an AOE instance.");
            return;
        }
        List<String> lines = AoeNpcSpawner.debugCounts(instOpt.get());
        lines.forEach(player::sendMessage);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("lists active aoe npc groups in your instance");
    }
}
