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

public class TpDelete extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
/*        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (params == null || params.length != 1) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1];

        for (Sale sale : Listing.getSales(name)) {
            int quantity = sale.getQuantity() - sale.getTotalSold(), saleItem = sale.getId();
            sale.setHasSold(true);
            sale.setLastCollectedSold(0);
            Listing.save(sale);
            ItemCollection.add(name, new GameItem(saleItem, quantity));
        }
        Discord.writeGiveLog("[TP-Delete] " + user.getName() + " deleted " + Misc.capitalizeJustFirst(name) + " Trading post items!");*/

    }

}