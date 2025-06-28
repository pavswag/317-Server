package io.xeros.content.commands.owner;

import io.xeros.Server;
import io.xeros.content.battlepass.Rewards;
import io.xeros.content.commands.Command;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.impl.ChristmasBox;
import io.xeros.content.seasons.Halloween;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.objects.ForceDoors;

import java.util.Arrays;

import static io.xeros.content.battlepass.Rewards.memberRewards;


public class Allnpcs extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        c.sendMessage("Mmmm nothing stored here now.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
