package io.xeros.content.commands.all;

import io.xeros.content.teleportv2.inter.TeleportInterface;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class Teleport extends Commands{

    @Override
    public void execute(Player player, String commandName, String input) {
        TeleportInterface.open(player);
        player.getQuesting().handleHelpTabActionButton(668);
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens Teleport Interface.");
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
