package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.playerinformation.CharacterInformationPanel;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Player;

public class Droprate extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.forcedChat("My drop rate bonus is : " + CharacterInformationPanel.formatString(DropManager.getDropRateModifier(player)) + "%.");
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Shows drop rate bonus");
	}
}


