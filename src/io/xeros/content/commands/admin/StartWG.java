package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.worldevent.WorldEventContainer;
import io.xeros.content.worldevent.impl.WGWorldEvent;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class StartWG extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new WGWorldEvent());
        player.sendMessage("WeaponGames will start soon.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
