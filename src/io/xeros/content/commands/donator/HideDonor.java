package io.xeros.content.commands.donator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

public class HideDonor extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.amDonated < 50) {
            player.sendMessage("@red@You need to be Goten Donor or higher!");
            return;
        }
        if (player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
            player.sendMessage("Wildyman cannot hide there ranks due to being locked with a skull!");
            return;
        }
        player.hideDonor = !player.hideDonor;
//        player.sendMessage("Hide donor status = " + player.hideDonor);
        player.setUpdateRequired(true);
        player.appearanceUpdateRequired = true;
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return player.amDonated >= 20;
    }
}
