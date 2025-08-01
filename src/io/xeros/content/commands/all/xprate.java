package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class xprate extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getMode().isOsrs()) {
            c.forcedChat("XP Rate : 1x");
        } else if (c.getMode().isIronmanType()) {
           c.forcedChat("XP Rate : 250x combat");
        } else if (c.getMode().is5x()) {
            c.forcedChat("XP Rate : 5x");
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Announces your xp rate above your head for all to see.");
    }

}