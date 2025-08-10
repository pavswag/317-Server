package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.PlatinumTokens;
import io.xeros.content.commands.Command;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

/**
 * Converts coins into platinum tokens.
 */
public class Tok extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int coinAmount = player.getItems().getInventoryCount(Items.COINS);

        int coinsToConvert = coinAmount;
        if (input != null && !input.isBlank()) {
            try {
                int requested = Integer.parseInt(input.trim());
                if (requested < coinsToConvert) {
                    coinsToConvert = requested;
                }
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid amount.");
                return;
            }
        }

        if (coinsToConvert < PlatinumTokens.TOKEN_VALUE) {
            player.sendMessage("You need at least 1,000 coins to convert to platinum tokens");
            return;
        }

        int tokensToAdd = coinsToConvert / PlatinumTokens.TOKEN_VALUE;
        Optional<GameItem> tokensAdded = player.getItems().addItemUntilFullReverse(
                new GameItem(Items.PLATINUM_TOKEN, tokensToAdd));
        if (tokensAdded.isEmpty()) {
            player.sendMessage("You don't have enough inventory space.");
            return;
        }

        player.getItems().deleteItem(Items.COINS,
                tokensAdded.get().getAmount() * PlatinumTokens.TOKEN_VALUE);
        player.sendMessage("Converted " + tokensAdded.get().getAmount() + " platinum tokens.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Converts your coins into platinum tokens at 1k:1");
    }
}

