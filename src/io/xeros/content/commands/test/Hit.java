package io.xeros.content.commands.test;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Hit extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length == 0) {
            player.sendMessage("Usage: ::hit 98");
            return;
        }

        int damage = Integer.parseInt(args[0]);
        player.appendDamage(null, damage, Hitmark.HIT);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Damage your player");
    }
}
