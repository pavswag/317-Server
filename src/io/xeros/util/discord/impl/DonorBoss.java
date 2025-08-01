package io.xeros.util.discord.impl;

import io.xeros.content.commands.admin.dboss;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DonorBoss extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        dboss.spawnBoss();
        Discord.writeGiveLog("[Donor Boss] " + user.getName() + " has spawned Donor Boss!");
    }

}
