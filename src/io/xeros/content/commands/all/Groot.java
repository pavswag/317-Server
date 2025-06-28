package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Groot extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }
        if (c.getPosition().inWild()) {
            return;
        }
        c.getPA().spellTeleport(2523, 3167, 0, false);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

}
