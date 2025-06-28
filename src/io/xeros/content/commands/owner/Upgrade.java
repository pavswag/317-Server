package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Upgrade extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
/*        for (int i = 0; i < 78; i++) {
            player.getPA().itemOnInterface(6199, 1, 35150, i);
        }*/
        player.getUpgradeInterface().openInterface(UpgradeMaterials.UpgradeType.WEAPON);
        player.getPA().showInterface(35000);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
