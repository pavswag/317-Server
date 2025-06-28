package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.impl.RaidsChestItems;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.xeros.content.minigames.raids.Raids.rollRegular;
import static io.xeros.content.minigames.raids.Raids.rollUnique;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 02/02/2024
 */
public class CoxReward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int uniqueBudget = Misc.random(25000,150000);
        int uniques = 0;
        for (int i = 0; i < 3; i++) { // up to 3 uniques
            if (uniqueBudget <= 0)
                break;
            int pointsToUse = Math.min(350000, uniqueBudget); // max of 350k points per unique attempt
            uniqueBudget -= pointsToUse;
            double chance = (double) pointsToUse / 3200 / 100.0; // 1% chance per 4200 points - OSRS = 8675
            if (ThreadLocalRandom.current().nextDouble() < chance) {
                uniques++;
                player.getRaidRewards().add(rollUnique());

/*                if (uniques == 1) {
                    getPlayers().forEach(p -> p.sendMessage("@pur@Special loot:"));
                }
                PlayerHandler.executeGlobalMessage("@pur@[RARE DROP] " + lucker.getDisplayName()+ " Has just received @red@" + item.getDef().getName() + " from Chambers of Xeric!");*/
            }
        }
        if (player.getRaidRewards().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
                GameItem gameItem = itemList.get(Misc.random(itemList.size() - 1));

                gameItem.setAmount(Misc.random(gameItem.getAmount()));

                player.getRaidRewards().add(gameItem);
            }
        }


        for (int z = 0; z < 6; z++) {
            player.getPA().itemOnInterface(-1, 1, 22729, z);
        }

        player.setLootCox(false);
        int i = 0;
        for (GameItem raidReward : player.getRaidRewards()) {
            player.getItems().addItemUnderAnyCircumstance(raidReward.getId(), raidReward.getAmount());
            player.getPA().itemOnInterface(raidReward.getId(), raidReward.getAmount(), 22729, i++);
        }

        if (!player.getRaidRewards().isEmpty()) {
            if (player.getItems().getInventoryCount(21046) > 0) {
                player.getItems().deleteItem2(21046, 1);
                player.sendErrorMessage("You roll a chance at a mini CoX Box! GL GL");
                if (Misc.isLucky(5)) {
                    player.getItems().addItemUnderAnyCircumstance(12585,1);
                    player.sendErrorMessage("You lucky mofo, you got a Mini CoX Box, Gl next time!!");
                }
            }
        }

        player.getRaidRewards().clear();
        player.getPA().showInterface(22725);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
