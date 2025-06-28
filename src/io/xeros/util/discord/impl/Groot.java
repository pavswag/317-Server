package io.xeros.util.discord.impl;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Groot extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        io.xeros.content.activityboss.Groot.spawnGroot();
        Discord.writeGiveLog("[Groot] " + user.getName() + " has spawned groot!");
    }

}