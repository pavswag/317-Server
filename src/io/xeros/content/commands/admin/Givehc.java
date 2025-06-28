package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

public class Givehc extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        String playerName = args[0];
        Player recipient = PlayerHandler.getPlayerByDisplayName(playerName);

        if (recipient == null) {
            player.sendMessage("Not sure what you've done but we can't find that user.");
            return;
        }

        if (!recipient.getMode().equals(Mode.forType(ModeType.ROGUE_IRONMAN)) && !recipient.getMode().equals(Mode.forType(ModeType.IRON_MAN))) {
            player.sendMessage("You cannot do this to a mode that doesn't have a HC link attached!");
            return;
        }

        if (recipient.getMode().equals(Mode.forType(ModeType.ROGUE_IRONMAN))) {
            recipient.setMode(Mode.forType(ModeType.ROGUE_HARDCORE_IRONMAN));
            recipient.getRights().setPrimary(Right.ROGUE_HARDCORE_IRONMAN);
            recipient.getRights().remove(Right.ROGUE_IRONMAN);
        } else if (recipient.getMode().equals(Mode.forType(ModeType.IRON_MAN))) {
            recipient.setMode(Mode.forType(ModeType.HC_IRON_MAN));
            recipient.getRights().setPrimary(Right.HC_IRONMAN);
            recipient.getRights().remove(Right.IRONMAN);
        }

        player.sendMessage("You have set " + recipient.getDisplayName() +" back to HC Mode");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
