package io.xeros.content.commands.owner;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.discord.Discord;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class release extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.ServerLocked = !Server.ServerLocked;
        player.sendMessage("@red@The server is now " + (!Server.ServerLocked ? "unlocked" : "locked") + "!");

        if (!Server.ServerLocked) {
            Discord.writeServerSyncMessage("Server is now online. <@everyone>");
            Discord.writeOnlineNotification("Server is now online. <@everyone>");
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
