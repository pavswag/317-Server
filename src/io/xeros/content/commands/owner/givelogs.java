package io.xeros.content.commands.owner;

import io.xeros.Server;
import io.xeros.content.collection_log.CollectionRewards;
import io.xeros.content.commands.Command;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.content.items.aoeweapons.AoeWeapons;
import io.xeros.content.trails.TreasureTrailsRewardItem;
import io.xeros.content.trails.TreasureTrailsRewards;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;

import java.util.List;

public class givelogs extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (CollectionRewards valuex : CollectionRewards.values()) {
            List<GameItem> drops = Server.getDropManager().getNPCdrops(valuex.NpcID);
            if (valuex.NpcID == 5126) {
                if (!player.getCollectionLog().getCollections().get(11246 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(11246));
                }
            }
            if (valuex.NpcID == 7554) {
                drops = RaidsChestRare.getRareDrops();
            } else if (valuex.NpcID >= 1 && valuex.NpcID <= 4) {
                drops.clear();
                drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
            } else if (valuex.NpcID == 5) {
                drops.clear();
                drops = PetHandler.getPetIds(true);
            } else if (valuex.NpcID == 6) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 7) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 8) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 9) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 10) {
                drops.clear();
                for (AoeWeapons value : AoeWeapons.values()) {
                    drops.add(new GameItem(value.ID));
                }
            } else if (valuex.NpcID == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                drops = TheatreOfBloodChest.getRareDrops();
            }
            for (GameItem drop : drops) {
                player.getCollectionLog().handleDrop(player, valuex.NpcID, drop.getId(), drop.getAmount());
                player.sendMessage("Give collection Log - "+ valuex.NpcID + " / " + drop.getId() + " / " + drop.getAmount());
            }
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

}
