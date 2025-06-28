package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.playerinformation.CharacterInformationPanel;
import io.xeros.model.entity.player.Player;

public class PlayerInfo extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        CharacterInformationPanel.Open(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
