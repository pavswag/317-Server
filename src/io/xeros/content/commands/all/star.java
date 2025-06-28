package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.events.monsterhunt.ShootingStars;
import io.xeros.content.wildwarning.WildWarning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

public class star extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.wildLevel > 0) {
            return;
        }
        if (!ShootingStars.progress) {
            player.sendMessage("There is no star active.");
            return;
        }

        if (!ShootingStars.ACTIVE.starSpawn.inWild() && player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || !ShootingStars.ACTIVE.starSpawn.inWild() && player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
            player.sendMessage("You cannot teleport to a star outside the wilderness.");
            return;
        }

        if (player.jailEnd > 1) {
            player.forcedChat("I'm trying to teleport away!");
            player.sendMessage("You are still jailed!");
            return;
        }

        if (ShootingStars.ACTIVE.starSpawn.inWild()) {
           WildWarning.sendWildWarning(player, p -> {
               p.getPA().movePlayer(ShootingStars.ACTIVE.starSpawn.getX()-2,ShootingStars.ACTIVE.starSpawn.getY(),0);
           });
        } else {
            player.getPA().movePlayer(ShootingStars.ACTIVE.starSpawn.getX()-2,ShootingStars.ACTIVE.starSpawn.getY(),0);
        }

    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleport's to you the Shooting Star.");
    }
}
