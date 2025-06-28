package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

public class Foundry extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
            return;
        }
        c.getPA().startTeleport(3115, 3503, 0, "foundry", false);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
