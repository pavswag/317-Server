package io.xeros.content.trails;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import static io.xeros.content.trails.RewardLevel.MASTER;
import io.xeros.content.trails.MasterClue;

import java.util.*;

public class ClueCasketHandler {

    public static boolean openAll(Player player, int itemId) {
        RewardLevel level = null;
        boolean isCasket = false;
        for (RewardLevel rl : RewardLevel.values()) {
            if (rl.getCasketId() == itemId) {
                level = rl;
                isCasket = true;
                break;
            }
            if (rl.getClueScrollId() == itemId) {
                level = rl;
                break;
            }
        }
        if (level == null) {
            return false;
        }

        int amount = player.getItems().getItemAmount(itemId);
        if (amount <= 0) {
            return false;
        }

        if (!isCasket) {
            openScrolls(player, level, amount);
            return true;
        }

        openCaskets(player, level, itemId, amount);
        return true;
    }

    private static void openScrolls(Player player, RewardLevel level, int amount) {
        if (level == MASTER && !MasterClue.checkRequirementOnOpen(player)) {
            return;
        }
        int mimic = 0;
        int caskets = 0;
        for (int i = 0; i < amount; i++) {
            Achievements.increase(player, AchievementType.CLUES, 1);
            player.getItems().deleteItem(level.getClueScrollId(), 1);
            if (TreasureTrails.rollMimicCasket(level)) {
                mimic++;
            } else {
                caskets++;
            }
        }
        if (mimic > 0) {
            player.getItems().addItem(Items.MIMIC, mimic);
            player.sendMessage("You've received " + mimic + " Mimic casket" + (mimic > 1 ? "s" : "") + "!");
        }
        if (caskets > 0) {
            player.getItems().addItem(level.getCasketId(), caskets);
            player.sendMessage("You've received " + caskets + " " + level.getFormattedName() + " clue scroll casket" + (caskets > 1 ? "s" : "") + ".");
        }
    }

    private static void openCaskets(Player player, RewardLevel level, int itemId, int amount) {
        Map<Integer, Integer> rewards = new HashMap<>();
        Set<Integer> announced = new HashSet<>();
        int opened = 0;
        int extraCaskets = 0;

        for (int i = 0; i < amount; i++) {
            if (player.getItems().freeSlots() < 3) {
                player.sendMessage("You need at least 3 free slots to open this.");
                break;
            }
            opened++;
            player.getItems().deleteItem(itemId, 1);
            List<GameItem> list = player.getTrails().generateRewardList(level, 1 + Misc.random(2), false);
            for (GameItem gi : list) {
                rewards.merge(gi.getId(), gi.getAmount(), Integer::sum);
                if (isRare(gi)) {
                    announced.add(gi.getId());
                }
            }
            if (player.getPerkSytem().gameItems.stream().anyMatch(it -> it.getId() == 33114) && Misc.isLucky(5)) {
                extraCaskets++;
            }
        }

        if (opened == 0) {
            return;
        }

        List<GameItem> rewardList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : rewards.entrySet()) {
            rewardList.add(new GameItem(entry.getKey(), entry.getValue()));
            if (player.getItems().playerHasItem(19730) || player.hasFollower && player.petSummonId == 19730) {
                player.getItems().addItemToBankOrDrop(entry.getKey(), entry.getValue());
            } else {
                player.getItems().addItemUnderAnyCircumstance(entry.getKey(), entry.getValue());
            }
        }
        player.getTrails().displayRewards(rewardList);
        for (int id : announced) {
            TreasureTrails.announceRare(player, new GameItem(id, rewards.get(id)), level);
        }
        if (extraCaskets > 0) {
            player.getItems().addItemUnderAnyCircumstance(itemId, extraCaskets);
            player.sendMessage("@red@You manage to get another clue casket x" + extraCaskets + "!!");
        }

        switch (level) {
            case EASY:
                player.setEasyClueCounter(player.getEasyClueCounter() + opened);
                player.sendMessage("<col=2d256d>You have completed " + player.getEasyClueCounter() + " Easy Treasure Trails.");
                break;
            case MEDIUM:
                player.setMediumClueCounter(player.getMediumClueCounter() + opened);
                player.sendMessage("<col=2d256d>You have completed " + player.getMediumClueCounter() + " medium Treasure Trails.");
                break;
            case HARD:
                player.setHardClueCounter(player.getHardClueCounter() + opened);
                player.sendMessage("<col=2d256d>You have completed " + player.getHardClueCounter() + " hard Treasure Trails.");
                break;
            case MASTER:
                player.setMasterClueCounter(player.getMasterClueCounter() + opened);
                player.sendMessage("<col=2d256d>You have completed " + player.getMasterClueCounter() + " master Treasure Trails.");
                for (int i = 0; i < opened; i++) {
                    PetHandler.roll(player, PetHandler.Pets.BLOODHOUND);
                }
                break;
        }
        player.sendMessage("You open x" + opened + " " + level.getFormattedName() + " caskets...");
    }

    private static boolean isRare(GameItem item) {
        String name = ItemDef.forId(item.getId()).getName();
        return name.contains("3rd") || item.getId() == 2577 || name.contains("mage's");
    }
}

