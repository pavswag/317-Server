package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.SoundType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class soundtest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        player.getPA().sendSound(Integer.parseInt(args[0]), SoundType.SOUND, null);

    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
