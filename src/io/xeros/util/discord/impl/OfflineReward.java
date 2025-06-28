package io.xeros.util.discord.impl;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.offlinestorage.ItemCollection;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OfflineReward extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (params == null || params.length != 4) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1].toLowerCase();
        int id = Integer.parseInt(params[2]);
        int amount = Integer.parseInt(params[3]);

        ItemCollection.add(name, new GameItem(id, amount));

        Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + user.getName() + " gave " + Misc.capitalizeJustFirst(name) + " Item: " + ItemDef.forId(id).getName() + " x " + amount + " (" + id +")");
    }

}
