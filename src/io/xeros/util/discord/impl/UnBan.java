package io.xeros.util.discord.impl;

import io.xeros.Server;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerAddresses;
import io.xeros.model.entity.player.save.PlayerSaveOffline;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static io.xeros.punishments.PunishmentType.*;

public class UnBan extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (string == null || string.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = string[1];

        Server.getIoExecutorService().submit(() -> {
            try {
                File file = PlayerSaveOffline.getCharacterFile(name);
                if (file == null) {
                    Discord.writeGiveLog("[UnBan-log] No character file with name " + name);
                    return;
                }

                if (!Server.getPunishments().remove(BAN, name)) {
                    Discord.writeGiveLog("[UnBan-log] "+name+" isn't banned, what the fuck are you doing?.");
                } else {
                    Discord.writeGiveLog("[UnBan-log] " + user.getName() + " has unbanned " + name);
                }

                PlayerAddresses addresses = PlayerSaveOffline.getAddresses(file);
                PlayerHandler.addQueuedAction(() -> {
                    Server.getPunishments().remove(NET_BAN, addresses.getIp());
                    Server.getPunishments().remove(MAC_BAN, addresses.getMac());
                    Server.getPunishments().remove(MAC_BAN, addresses.getUUID());
                });
            } catch (Exception ez) {
                ez.printStackTrace();

            }
        });

    }

}
