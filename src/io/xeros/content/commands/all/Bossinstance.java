package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.impl.BossInstanceDialogue;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Opens the boss instance dialogue so players can manage their personal boss tiers.
 */
public class Bossinstance extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.start(new BossInstanceDialogue(player));
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the boss instance menu.");
    }
}
