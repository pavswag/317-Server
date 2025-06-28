package io.xeros.util.discord.impl;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerAddresses;
import io.xeros.punishments.PunishmentType;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnMute extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (string == null || string.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = string[1];

        Player p = PlayerHandler.getPlayerByDisplayName(name);
        if (p != null) {
            p.setHelpCcMuted(false);
            p.muteEnd = 0;
            PlayerAddresses addresses = p.getValidAddresses();
            Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getIp());
            if (addresses.getMac() != null)
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getMac());
            if (addresses.getUUID() != null && p.getUUID().length() > 0)
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getUUID());
            Discord.writeGiveLog("[Mute-log] " + p.getDisplayName() + " has been unmuted by " + user.getName());
            p.sendMessage("You have been unmuted by " + user.getName());
        } else {
            Discord.writeGiveLog("[Mute-log] Well it's come to my attention that either they don't exist or you have a serious spelling issue, you fucktard.");
        }
    }

}

