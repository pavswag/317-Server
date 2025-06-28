package io.xeros.content.commands.moderator;

import java.util.Optional;

import io.xeros.content.combat.pvp.Killstreak;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

/**
 * Shows the killstreaks of a given player.
 *
 * @author Emiel
 */
public class Ks extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
        optionalPlayer.ifPresent(player -> {
            c.sendMessage("Hunter killstreak of " + player.getDisplayName() + " : " + player.getKillstreak().getAmount(Killstreak.Type.HUNTER));
            c.sendMessage("Rogue killstreak of " + player.getDisplayName() + " : " + player.getKillstreak().getAmount(Killstreak.Type.ROGUE));
        });
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return Right.MODERATOR.isOrInherits(player);
    }
}
