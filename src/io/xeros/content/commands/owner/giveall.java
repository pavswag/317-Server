package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 04/03/2024
 */
public class giveall extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split(" ");
            if (args.length != 2) {
                throw new IllegalArgumentException();
            }
            int itemID = Integer.parseInt(args[0]);
            int amount = Misc.stringToInt(args[1]);

/*            Set<String> uniqueIPs = new HashSet<>();
            Set<String> uniqueMACs = new HashSet<>();*/
            Set<String> uniqueUUIDs = new HashSet<>();
            ArrayList<Player> filteredPlayers = new ArrayList<>();

            for (Player player1 : PlayerHandler.getPlayers()) {
/*                String ipAddress = player1.getIpAddress();
                String macAddress = player1.getMacAddress();*/
                String UUIDAddress = player1.getUUID();
                if (/*!uniqueIPs.contains(ipAddress) && !uniqueMACs.contains(macAddress) &&*/ !uniqueUUIDs.contains(UUIDAddress)) {
                    filteredPlayers.add(player1);
/*                    uniqueIPs.add(ipAddress);
                    uniqueMACs.add(macAddress);*/
                    uniqueUUIDs.add(UUIDAddress);
                }
            }

            if (player.debugMessage) {
                player.sendMessage("You have given " + uniqueUUIDs.size() + " / " + PlayerHandler.getPlayers().size() + " player's " + amount + " x " + ItemDef.forId(itemID).getName());
            }

            for (Player filteredPlayer : filteredPlayers) {
                filteredPlayer.getItems().addItemUnderAnyCircumstance(itemID, amount);
            }

            PlayerHandler.executeGlobalMessage("[@red@LOOOT@bla@] @pur@Everyone has been given " + ItemDef.forId(itemID).getName() + " x " + amount + "!!");
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::giveall itemid amount");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
