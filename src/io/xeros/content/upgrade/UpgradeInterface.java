package io.xeros.content.upgrade;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.content.skills.Skill;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeInterface {

    private Player player;
    private UpgradeMaterials selectedUpgrade;
    public ArrayList<UpgradeMaterials> upgradeMaterialsArrayList;

    private int TOKEN_ID = 995;

    public UpgradeInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {
        switch (buttonId) {
            case 35020:
                handleUpgrade(false);
                return true;
            case 35005:
                openInterface(UpgradeMaterials.UpgradeType.WEAPON);
                showUpgrade(UpgradeMaterials.DRAGON_SCIMITAR);
                return true;
            case 35006:
                openInterface(UpgradeMaterials.UpgradeType.ARMOUR);
                showUpgrade(UpgradeMaterials.VOID_MAGE_HELM);
                return true;
            case 35007:
                openInterface(UpgradeMaterials.UpgradeType.ACCESSORY);
                showUpgrade(UpgradeMaterials.AMULET_OF_FURY);
                return true;
            case 35008:
                openInterface(UpgradeMaterials.UpgradeType.MISC);
                showUpgrade(UpgradeMaterials.UNHOLY_BOOK);
                return true;
        }

        return false;
    }

    public void handleItemAction(int slot) {
        if (upgradeMaterialsArrayList != null && upgradeMaterialsArrayList.get(slot) != null) {
            showUpgrade(upgradeMaterialsArrayList.get(slot));
        }
    }


    public void showUpgrade(UpgradeMaterials upgrade) {

        selectedUpgrade = upgrade;

        player.getPA().itemOnInterface(new GameItem(upgrade.getReward().getId(), upgrade.getReward().getAmount()), 35017,0);
        player.getPA().sendString(35018, "Upgrade Points req: @whi@" + Misc.formatPriceKMB((int) upgrade.getCost()));
        int fortuneXp = (int) (upgrade.getCost() / 5000L);
        player.getPA().sendString(35022, "Fortune XP: @whi@" + Misc.formatNumber(fortuneXp));

        if (upgrade.getReward().getId() == 3648 ||
                upgrade.getReward().getId() == 25432 ||
                        upgrade.getReward().getId() == 28254 ||
                        upgrade.getReward().getId() == 28256 ||
                        upgrade.getReward().getId() == 28258 ||
                        upgrade.getReward().getId() == 26551 ||
                        upgrade.getReward().getId() == 27585 ||
                        upgrade.getReward().getId() == 27584 ||
                        upgrade.getReward().getId() == 27582 ||
                        upgrade.getReward().getId() == 27583 ||
                        upgrade.getReward().getId() == 27586) {
            player.getPA().sendString(35019, "Success rate: @whi@ " + upgrade.getSuccessRate() + "%");

        } else {
            player.getPA().sendString(35019, "Success rate: @whi@" + getBoost(upgrade.getSuccessRate()) + "%");
        }
        if (player.playerLevel[Skill.FORTUNE.getId()] < upgrade.getLevelRequired()) {
            player.getPA().sendString(35021, "Lvl : " + upgrade.getLevelRequired());
        } else {
            player.getPA().sendString(35021, "Upgrade");
        }
    }


    public void openInterface(UpgradeMaterials.UpgradeType type) {
        player.getPA().sendConfig(5334, type.ordinal());

        selectedUpgrade = null;

        player.getPA().itemOnInterface(new GameItem(-1, 1), 35017, 0);
        player.getPA().sendString(35018, "Nomad req: @whi@---");
        player.getPA().sendString(35019, "Success rate: @whi@---");
        player.getPA().sendString(35022, "Fortune XP: ---");
        player.sendMessage("@bla@[@red@NOMAD@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
        upgradeMaterialsArrayList = UpgradeMaterials.getForType(type);
        for (int i = 0; i < 72; i++) {
            if (upgradeMaterialsArrayList.size() > i) {
                player.getPA().itemOnInterface(
                        upgradeMaterialsArrayList.get(i).getRequired().getId(), upgradeMaterialsArrayList.get(i).getRequired().getAmount(), 35150, i);
            } else {
                player.getPA().itemOnInterface(-1, 1, 35150, i);
            }
        }

        player.getPA().showInterface(35000);
    }


    public void handleUpgrade(boolean all) {
        if (System.currentTimeMillis() - player.clickDelay <= 2200) {
            player.sendMessage("You must wait before trying to upgrade again!");
            return;
        }
        player.clickDelay = System.currentTimeMillis();

        if (selectedUpgrade == null) {
            player.sendMessage("Choose an item to upgrade.");
            return;
        }

        Arrays.stream(UpgradeMaterials.values()).forEach(val -> {
            if (val.getRequired().getId() == selectedUpgrade.getRequired().getId()) {
                if (player.getLevelForXP(player.playerXP[Skill.FORTUNE.getId()]) < val.getLevelRequired()) {
                    player.sendMessage("You don't have the required Fortune level to upgrade this item.");
                    return;
                }

                if (getRestrictions(val, all)) {
                        if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072) && Misc.random(0, 100) >= 90)
                        {
                            player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount());
                        } else {
                            player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount());
                        }

                    if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072) && Misc.random(0, 100) >= 90)
                    {
                        player.sendMessage("@red@Your Fusion Master Perk Save's the Cost of your upgrade!");
                    } else {
                        player.foundryPoints = (player.foundryPoints - val.getCost());
                    }


                    TimerTask task = new TimerTask() {
                        int tick = 0;

                        @Override
                        public void run() {
                            if (tick == 0) {
                                player.sendMessage("You try to upgrade....");
                            } else if (tick == 2) {
                                double a = Misc.random(0, 99);
                                double b = val.getSuccessRate();
                                if (val.getReward().getId() != 3468 &&
                                        val.getReward().getId() != 25432 &&
                                        val.getReward().getId() != 28254 &&
                                        val.getReward().getId() != 28256 &&
                                        val.getReward().getId() != 28258 &&
                                        val.getReward().getId() != 26551 &&
                                        val.getReward().getId() != 27585 &&
                                        val.getReward().getId() != 27584 &&
                                        val.getReward().getId() != 27582 &&
                                        val.getReward().getId() != 27583 &&
                                        val.getReward().getId() != 27586) {
                                    b = getBoost(val.getSuccessRate());
                                    if (player.getItems().getInventoryCount(26886) >= 1) {
                                        player.getItems().deleteItem2(26886, 1);
                                    }
                                }
                                boolean success =  a <= b;
                                if (success) {
                                    player.sendMessage("You successfully upgraded your item!");
                                    Achievements.increase(player, AchievementType.UPGRADE, 1);
                                    player.getInventory().addToInventory(new ImmutableItem(val.getReward()));
                                    if (val.isRare()) {
                                            String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + player.getDisplayName()
                                                    + " Has successfully achieved "
                                                    + val.getReward().getDef().getName();

                                            PlayerHandler.executeGlobalMessage(msg);

                                        if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                                            player.getCollectionLog().handleDrop(player, 6, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                                            player.getCollectionLog().handleDrop(player, 7, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                                            player.getCollectionLog().handleDrop(player, 8, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                                            player.getCollectionLog().handleDrop(player, 9, val.getReward().getId(), 1);
                                        }

                                    }
                                    int fortuneXp = (int) (val.getCost() / 5000L);
                                    if (fortuneXp > 0) {
                                        player.getPA().addSkillXPMultiplied(fortuneXp, Skill.FORTUNE.getId(), true);
                                        player.sendMessage("You gain " + Misc.formatNumber(fortuneXp) + " Fortune XP.");
                                    }
                                } else {
                                    boolean ReturnItem = (Math.random() * 100) <= getDonator();
                                    if (ReturnItem && player.amDonated >= 100 && val.getRequired().getId() != 33189 && val.getRequired().getId() != 33190 && val.getRequired().getId() != 33191) {
                                        player.sendMessage("Your donator rank saves your item!");
                                        player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
                                    }
                                    if (val.getRequired().getId() == 33189 || val.getRequired().getId() == 33190 || val.getRequired().getId() == 33191) {
                                        player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
                                    }
                                    player.sendMessage("You failed to upgrade!");
                                }

                                player.sendMessage("@bla@[@red@FOUNDRY@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
                                cancel();
                            }
                            tick++;
                        }

                    };

                    Timer timer = new Timer();
                    timer.schedule(task, 500, 500);
                }
            }
        });
    }

    public int getDonator() {

        int multiplier = 0;

        if (player.getRights().isOrInherits(Right.ALMIGHTY_DONATOR)) {
            multiplier += 65;
        } else if (player.getRights().isOrInherits(Right.APEX_DONATOR)) {
            multiplier += 50;
        } else if (player.getRights().isOrInherits(Right.PLATINUM_DONATOR)) {
            multiplier += 45;
        } else if (player.getRights().isOrInherits(Right.GILDED_DONATOR)) {
            multiplier += 40;
        } else if (player.getRights().isOrInherits(Right.SUPREME_DONATOR)) {
            multiplier += 35;
        } else if (player.getRights().isOrInherits(Right.MAJOR_DONATOR)) {
            multiplier += 30;
        } else if (player.getRights().isOrInherits(Right.EXTREME_DONATOR)) {
            multiplier += 25;
        } else if (player.getRights().isOrInherits(Right.GREAT_DONATOR)) {
            multiplier += 20;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            multiplier += 10;
        }

        return multiplier;
    }

    public double getBoost(double chance) {
        double percentBoost = 0D;

        if (player.amDonated >= 25 && player.amDonated < 50) {
            percentBoost += 1;
        } else if (player.amDonated >= 50 && player.amDonated < 100) {
            percentBoost += 3;
        } else if (player.amDonated >= 100 && player.amDonated < 250) {
            percentBoost += 5;
        } else if (player.amDonated >= 250 && player.amDonated < 500) {
            percentBoost += 7;
        }else if (player.amDonated >= 500 && player.amDonated < 1250) {
            percentBoost += 9;
        }else if (player.amDonated >= 1250 && player.amDonated < 2500) {
            percentBoost += 11;
        }else if (player.amDonated >= 2500 && player.amDonated < 4000) {
            percentBoost += 13;
        }else if (player.amDonated >= 4000 && player.amDonated < 6500) {
            percentBoost += 15;
        }else if (player.amDonated >= 6500 && player.amDonated < 15000) {
            percentBoost += 20;
        }else if (player.amDonated >= 15000) {
            percentBoost += 30;
        }

        if (player.centurion > 0) {
            percentBoost += 20;
        }

        if (player.getItems().hasItemOnOrInventory(26886)) {
            percentBoost += 50;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            percentBoost += 10;
        }

        double multiplier = 1 + (percentBoost / 100D);
//        System.out.println("Multiplier: " + multiplier);
        chance += percentBoost;
//        System.out.println("Chance: " + chance);
        return chance;
    }

    private boolean getRestrictions(UpgradeMaterials data, boolean all) {
        ItemDef definition = ItemDef.forId(data.getRequired().getId() + 1);
        boolean noted = false;
        //if (definition.isNoted() && definition.isStackable()) {
        //    String name = definition.getName();
        //    definition = ItemDef.forId(data.getRequired().getId());
        //    String originalName = definition.getName();
        //    noted = name.equals(originalName);
        //}
        if (noted && player.getItems().getInventoryCount(data.getRequired().getId() + 1) > 1) {
            int amount = all ? player.getItems().getInventoryCount(data.getRequired().getId() + 1) : 1;
            if (player.getItems().getInventoryCount(data.getRequired().getId() + 1) < amount) {
                player.sendMessage("You do not have the required items!");
                return false;
            }
            if (player.foundryPoints < (data.getCost())) {
                player.sendMessage("You don't have enough Foundry points to upgrade this item.");
                return false;
            }
        } else {
            if (player.getItems().getInventoryCount(data.getRequired().getId()) < data.getRequired().getAmount()) {
                player.sendMessage("You do not have the required items!");
                return false;
            }
            if (player.foundryPoints < (data.getCost())) {
                player.sendMessage("You don't have enough Foundry points to upgrade this item.");
                return false;
            }
        }
        return true;
    }


}
