package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.offlinestorage.ItemCollection;

import java.util.Optional;

public class Givereward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 3) {
                throw new IllegalArgumentException();
            }
            String playerName = args[0];
            int itemID = Integer.parseInt(args[1]);
            int amount = Misc.stringToInt(args[2]);

            ItemCollection.add(playerName, new GameItem(itemID, amount));
            player.sendMessage("You have given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");
            Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + player.getDisplayName() + " has given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");

        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::givereward-player-itemid-amount");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
