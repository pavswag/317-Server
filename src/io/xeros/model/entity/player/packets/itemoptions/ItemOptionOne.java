package io.xeros.model.entity.player.packets.itemoptions;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.DiceHandler;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bonus.BoostScrolls;
import io.xeros.content.bosses.Cerberus;
import io.xeros.content.bosses.Durial321;
import io.xeros.content.bosses.mimic.MimicCasket;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.magic.NonCombatSpellData;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue;
import io.xeros.content.dwarfmulticannon.Cannon;
import io.xeros.content.item.lootable.impl.*;
import io.xeros.content.items.CluescrollRateIncreaseScroll;
import io.xeros.content.items.Packs;
import io.xeros.content.items.pouch.RunePouch;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.miniquests.magearenaii.MageArenaII;
import io.xeros.content.perky.Perks;
import io.xeros.content.skills.DoubleExpScroll;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.SkillHandler;
import io.xeros.content.skills.SkillPetRateIncreaseScroll;
import io.xeros.content.skills.hunter.Hunter;
import io.xeros.content.skills.hunter.trap.impl.BirdSnare;
import io.xeros.content.skills.hunter.trap.impl.BoxTrap;
import io.xeros.content.skills.prayer.Bone;
import io.xeros.content.skills.prayer.Prayer;
import io.xeros.content.skills.runecrafting.Pouches;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.content.teleportation.TeleportTablets;
import io.xeros.content.trails.TreasureTrails;
import io.xeros.model.Graphic;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.thrall.ThrallSystem;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.items.ItemAction;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules.Rule;
import io.xeros.model.multiplayersession.flowerpoker.FlowerData;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Clicking an item, bury bone, eat food etc
 **/
public class ItemOptionOne implements PacketType {

    /**
     * The position the mithril seed was planted
     */
    private static final int[] position = new int[2];

    public void plantMithrilSeed(Player c) {
        position[0] = c.absX;
        position[1] = c.absY;
        GlobalObject object1 = new GlobalObject(FlowerData.getRandomFlower().objectId, position[0], position[1], c.getHeight(), 3, 10, 120, -1);
        Server.getGlobalObjects().add(object1);
        c.getPA().walkTo(1, 0);
        c.facePosition(position[0] -1, position[1]);
        c.sendMessage("You planted a flower!");
        c.getItems().deleteItem(299, c.getItems().getInventoryItemSlot(299), 1);
    }
    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        c.interruptActions();
        int interfaceId = c.getInStream().readUnsignedWord();
        int itemSlot = c.getInStream().readUnsignedWord();
        int itemId = c.getInStream().readUnsignedWord();

        if (c.debugMessage) {
            c.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 1, interfaceId, itemSlot));
        }

        GameItem gameItem = new GameItem(itemId);
        ItemDef itemDef = ItemDef.forId(itemId);
        ItemAction action = null;
        ItemAction[] actions = itemDef.inventoryActions;

        if(actions != null)
            action = actions[0];
        if(action != null) {
            action.handle(c, gameItem);
            return;
        }

        if (itemId == 27) {
            if (c.isMember()) {
                c.sendErrorMessage("You already have an active division pass!!");
                return;
            }
            Pass.grantMembership(c);
            c.getItems().deleteItem2(27, 1);
            return;
        }

        if (itemId == 20280) {
            c.sendErrorMessage("Contact a staff member to exchange this item!");
            return;
        }
        if (itemId == 19911) {
            c.sendErrorMessage("Contact a staff member to exchange this item!");
            return;
        }
        if (itemId == 19903) {
            c.sendErrorMessage("Contact a staff member to exchange this item!");
            return;
        }
        if (itemId == 20281) {
            c.sendErrorMessage("Contact a staff member to exchange this item!");
            return;
        }
        if (itemId == 20282) {
            c.sendErrorMessage("Contact a staff member to exchange this item!");
            return;
        }


        if (itemId == 25818) {
            ThrallSystem.spawnThrall(c, ThrallSystem.forThrall(10880));
            return;
        }

        if (BoostScrolls.handleItemClick(c, itemId)) {
            return;
        }

        if (itemId == 11681) {
            c.getShops().openShop(600);
                return;

        }
        if (itemId == 27563) {
            if (c.getItems().getInventoryCount(27563) < 0)
                return;

            if (c.getPosition().inWild())
                return;

            c.getItems().deleteItem2(27563, 1);
            c.eggNogTimer = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));
            c.sendMessage("@red@You drink your eggnog and become overwhelmed with knowledge! (2 hours duration 2x Achievement points)");
            return;
        }

        if (itemId == 5020) {
            if (c.getItems().getInventoryCount(5020) <= 0) {
                return;
            }
            if (Durial321.spawned || Durial321.alive) {
                c.sendMessage("@red@Durial is already active!");
                return;
            }
            c.getItems().deleteItem2(5020, 1);
            Durial321.init();
            PlayerHandler.executeGlobalMessage(c.getDisplayName() + " Has consumed there Durial ticket & spawned Durial!");
            return;
        }

        if (itemId == 33213) {
            if (c.getItems().getInventoryCount(33213) <= 0) {
                return;
            }
            c.getItems().deleteItem2(33213,1);

            c.getHealth().increase(c.getHealth().getMaximumHealth()-c.getHealth().getCurrentHealth());
            return;
        }

        if (itemId == 6644) {
            if (c.getItems().getInventoryCount(6644) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6644, 1);
            c.getItems().addItemUnderAnyCircumstance(27777, 1);
            c.getItems().addItemUnderAnyCircumstance(27769, 1);
            c.getItems().addItemUnderAnyCircumstance(27773, 1);
            return;
        }

        if (itemId == 6642) {
            if (c.getItems().getInventoryCount(6642) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6642, 1);
            c.getItems().addItemUnderAnyCircumstance(27753, 1);
            c.getItems().addItemUnderAnyCircumstance(27745, 1);
            c.getItems().addItemUnderAnyCircumstance(27749, 1);
            return;
        }

        if (itemId == 6641) {
            if (c.getItems().getInventoryCount(6641) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6641, 1);
            c.getItems().addItemUnderAnyCircumstance(27765, 1);
            c.getItems().addItemUnderAnyCircumstance(27757, 1);
            c.getItems().addItemUnderAnyCircumstance(27761, 1);
            return;
        }

        if (itemId == 6650) {
            if (c.getItems().getInventoryCount(6650) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6650, 1);
            c.getItems().addItemUnderAnyCircumstance(27729, 1);
            c.getItems().addItemUnderAnyCircumstance(27721, 1);
            c.getItems().addItemUnderAnyCircumstance(27725, 1);
            return;
        }

        if (itemId == 6645) {
            if (c.getItems().getInventoryCount(6645) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6645, 1);
            c.getItems().addItemUnderAnyCircumstance(27741, 1);
            c.getItems().addItemUnderAnyCircumstance(27733, 1);
            c.getItems().addItemUnderAnyCircumstance(27737, 1);
            return;
        }

        if (itemId == 6640) {
            if (c.getItems().getInventoryCount(6640) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6640, 1);
            c.getItems().addItemUnderAnyCircumstance(27705, 1);
            c.getItems().addItemUnderAnyCircumstance(27697, 1);
            c.getItems().addItemUnderAnyCircumstance(27701, 1);
            return;
        }

        if (itemId == 6646) {
            if (c.getItems().getInventoryCount(6646) <= 0) {
                c.sendMessage("You don't have any Crystals to open.");
                return;
            }
            c.getItems().deleteItem2(6646, 1);
            c.getItems().addItemUnderAnyCircumstance(27717, 1);
            c.getItems().addItemUnderAnyCircumstance(27709, 1);
            c.getItems().addItemUnderAnyCircumstance(27713, 1);
            return;
        }

        if (itemId == 6807) {
            if (c.getItems().getInventoryCount(6807) <= 0) {
                return;
            }
            if (c.hitStandardRateLimit(true))
                return;
            c.getItems().deleteItem2(6807, 1);
            c.prestigePoints += 1000;
            c.sendMessage("You consume the scroll and now have " + c.prestigePoints + " prestige points!");
            return;
        }
        if (itemId == 6891) {
            c.start(new DialogueBuilder(c).option("Would you like to reset your achievements?",
                    new DialogueOption("Yes", plr -> {
                        if (!plr.getItems().hasItemOnOrInventory(6891)) {
                            plr.sendMessage("@red@You do not have an achievement reset book.");
                            return;
                        }
                        plr.getAchievements().resetAll();
                        plr.getItems().deleteItem2(6891, 1);
                        plr.sendMessage("@red@You now need to relog, after using the reset achievement book!");
                        plr.getPA().closeAllWindows();
                    }),
                    new DialogueOption("No", plr -> plr.getPA().closeAllWindows())));
            return;
        }

        if (itemId == 6805) {
            if (c.hitStandardRateLimit(true))
                return;
            int amount = c.getItems().getInventoryCount(6805);
            if (amount <= 0) {
                c.sendMessage("[@red@Fortune@bla] @cya@You don't have any left to claim!");
                return;
            }
            c.FortuneSpins += amount;
            c.getItems().deleteItem2(6805, amount);
            c.sendMessage("[@red@Fortune@bla@] @cya@You have claimed "+amount+" fortune spin"+(amount > 1 ? "'s" : "")+"!");
            return;
        }

        if (itemId == 11157) {
            int count = c.getItems().getInventoryCount(11157);
            if (count <= 0) {
                return;
            }
            c.start(new DialogueBuilder(c).statement("You earned " + (500 * count) + " Demon Hunter Experience!"));
            c.getPA().addSkillXPMultiplied(500 * count, Skill.DEMON_HUNTER.getId(), true);
            c.getItems().deleteItem2(11157, count);
        }

        if (itemId == 20238) {
            c.getTaskMaster().taskMasterKillsList.clear();
            c.getTaskMaster().generateTasks(c, true);
            c.getItems().deleteItem2(20238, 1);
        }

        if (itemId == 24366) {
            if (Configuration.UNICOW_DISABLED) {
                c.sendMessage("This island is currently disabled.");
                return;
            }
            if (System.currentTimeMillis() - c.clickDelay <= 2200) {
                return;
            }
            if (!Boundary.isIn(c, Boundary.UNICOW_AREA)) {
                c.moveTo(new Position(2847, 5086, 0));
            }
            if ((c.IslandTimer/100) >= 55) {//o theres a max, but timer is working in-game at-least
                return;
            }
            c.clickDelay = System.currentTimeMillis();
            c.IslandTimer += (TimeUnit.MINUTES.toMillis(60) / 600);
            c.getPA().sendConfig(39, 60);
            c.getPA().sendGameTimer(ClientGameTimer.ISLAND_TIMER_60, TimeUnit.MINUTES, (int) (c.IslandTimer/100));
            if (c.IslandTimer == 0) {
                c.sendMessage("You now have 60 minutes time remaining at Unicows!");
            } else if (c.IslandTimer > 0) {
                c.sendMessage("You have added 60 more minutes to your Unicow timer!");
            }
            c.getItems().deleteItem2(24366, 1);
            return;
        }
        else if (itemId == 24365) {
            if (Configuration.UNICOW_DISABLED) {
                c.sendMessage("This island is currently disabled.");
                return;
            }
            if (System.currentTimeMillis() - c.clickDelay <= 2200) {
                return;
            }
            if (!Boundary.isIn(c, Boundary.UNICOW_AREA)) {
                c.moveTo(new Position(2847, 5086, 0));
            }
            if ((c.IslandTimer/100) >= 55) {
                return;
            }
            c.clickDelay = System.currentTimeMillis();
            c.IslandTimer += (TimeUnit.MINUTES.toMillis(30) / 600);
            c.getPA().sendGameTimer(ClientGameTimer.ISLAND_TIMER_30, TimeUnit.MINUTES, (int) (c.IslandTimer/100));
            c.getPA().sendConfig(39, 30);
            if (c.IslandTimer == 0) {
                c.sendMessage("You now have 30 minutes time remaining at Unicows!");
            } else if (c.IslandTimer > 0) {
                c.sendMessage("You have added 30 more minutes to your Unicow timer!");
            }
            c.getItems().deleteItem2(24365, 1);
            return;
        }
        else if (itemId == 24364) {
            if (Configuration.UNICOW_DISABLED) {
                c.sendMessage("This island is currently disabled.");
                return;
            }
            if (System.currentTimeMillis() - c.clickDelay <= 2200) {
                return;
            }
            if (!Boundary.isIn(c, Boundary.UNICOW_AREA)) {
                c.moveTo(new Position(2847, 5086, 0));
            }
            if ((c.IslandTimer/100) >= 55) {
                return;
            }
            c.clickDelay = System.currentTimeMillis();
            c.IslandTimer += (TimeUnit.MINUTES.toMillis(15) / 600);
            c.getPA().sendConfig(39, 15);
            c.getPA().sendGameTimer(ClientGameTimer.ISLAND_TIMER_15, TimeUnit.MINUTES, (int) (c.IslandTimer/100));
            if (c.IslandTimer == 0) {
                c.sendMessage("You now have 15 minutes time remaining at Unicows!");
            } else if (c.IslandTimer > 0) {
                c.sendMessage("You have added 15 more minutes to your Unicow timer!");
            }
            c.getItems().deleteItem2(24364, 1);
            return;
        }



        if (itemId == 27285) {
            c.start(new DialogueBuilder(c).option("Which weapon would you like to upgrade?", new DialogueOption("Tumeken's Shadow (500m Upgrade Points & 25x Corruptors)", p -> {
                if (p.getItems().getInventoryCount(27275) == 0) {
                    p.sendMessage("@red@You don't have any Tumeken's Shadow's to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(27285) < 25) {
                    p.sendMessage("@red@You need 25 Eye of the corruptor's to upgrade your Tumeken's Shadow!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 500_000_000) {
                    p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Tumeken's Shadow!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(27275, 1);
                p.getItems().deleteItem2(27285, 25);
                p.foundryPoints -= 500_000_000;
                p.getItems().addItemUnderAnyCircumstance(33205, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully achieved Demon X Staff!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();

            }), new DialogueOption("Keris Partisan of corruption (500m Upgrade Points & 25x Corruptors)", p -> {
                if (p.getItems().getInventoryCount(27287) == 0) {
                    p.sendMessage("@red@You don't have any Keris Partisan of corruption's to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(27285) < 25) {
                    p.sendMessage("@red@You need 25 Eye of the corruptor's to upgrade your Keris Partisan of corruption!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 500_000_000) {
                    p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Keris Partisan of corruption!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(27287, 1);
                p.getItems().deleteItem2(27285, 25);
                p.foundryPoints -= 500_000_000;
                p.getItems().addItemUnderAnyCircumstance(33204, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully achieved Demon X Spear!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();
            }), new DialogueOption("Osmumten's Fang (or) (250m Upgrade Points & 15x Corruptors)", p -> {
                if (p.getItems().getInventoryCount(27246) == 0) {
                    p.sendMessage("@red@You don't have any Osmumten's Fang (or)'s to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(27285) < 15) {
                    p.sendMessage("@red@You need 15 Eye of the corruptor's to upgrade your Osmumten's Fang (or)!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 250_000_000) {
                    p.sendMessage("@red@You require 250m Upgrade Points to upgrade your Osmumten's Fang (or)!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(27246, 1);
                p.getItems().deleteItem2(27285, 15);
                p.foundryPoints -= 250_000_000;
                p.getItems().addItemUnderAnyCircumstance(33202, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully achieved Demon X Sword!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();
            }), new DialogueOption("Blood scythe (500m Upgrade Points & 25x Corruptors)", p -> {
                if (p.getItems().getInventoryCount(33184) == 0) {
                    p.sendMessage("@red@You don't have any Blood scythe's to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(27285) < 25) {
                    p.sendMessage("@red@You need 25 Eye of the corruptor's to upgrade your Blood scythe!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 500_000_000) {
                    p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Blood scythe!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(33184, 1);
                p.getItems().deleteItem2(27285, 25);
                p.foundryPoints -= 500_000_000;
                p.getItems().addItemUnderAnyCircumstance(33203, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully achieved Demon X Scythe!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();

            }), new DialogueOption("Seren Godbow (500m Upgrade Points & 25x Corruptors)", p -> {
                if (p.getItems().getInventoryCount(33058) == 0) {
                    p.sendMessage("@red@You don't have any Seren Godbow's to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(27285) < 25) {
                    p.sendMessage("@red@You need 25 Eye of the corruptor's to upgrade your Seren Godbow!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 500_000_000) {
                    p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Seren Godbow!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(33058, 1);
                p.getItems().deleteItem2(27285, 25);
                p.foundryPoints -= 500_000_000;
                p.getItems().addItemUnderAnyCircumstance(33207, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully achieved Demon X Bow!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();
            })));
        }

        if (itemId == 28274) {
            c.start(new DialogueBuilder(c).option("what tool would you like to upgrade?", new DialogueOption("Infernal pickaxe (or). (500m Upgrade Points & 20 Smoked gems)", p -> {
                if (p.getItems().getInventoryCount(25063) == 0) {
                    p.sendMessage("@red@You don't have an Infernal pickaxe (or) to upgrade!");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.getItems().getInventoryCount(28274) < 20) {
                    p.sendMessage("@red@You need 20 Smoke gems to upgrade your pickaxe");
                    p.getPA().closeAllWindows();
                    return;
                }
                if (p.foundryPoints < 500_000_000) {
                    p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Pickaxe!");
                    p.getPA().closeAllWindows();
                    return;
                }
                p.getItems().deleteItem2(25063, 1);
                p.getItems().deleteItem2(28274, 20);
                p.foundryPoints -= 500_000_000;
                p.getItems().addItemUnderAnyCircumstance(25112, 1);
                String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                        + " Has successfully created a Trailblazer Pickaxe!";
                PlayerHandler.executeGlobalMessage(msg);
                p.getPA().closeAllWindows();
            }),

                new DialogueOption("Infernal Axe (or). (500m Upgrade Points & 20 Smoked gems", p -> {
                    if (p.getItems().getInventoryCount(25066) == 0) {
                        p.sendMessage("@red@You don't have an Infernal axe (or) to upgrade!");
                        p.getPA().closeAllWindows();
                        return;
                    }
                    if (p.getItems().getInventoryCount(28274) < 20) {
                        p.sendMessage("@red@You need 20 Smoke gems to upgrade your Axe");
                        p.getPA().closeAllWindows();
                        return;
                    }
                    if (p.foundryPoints < 500_000_000) {
                        p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Axe!");
                        p.getPA().closeAllWindows();
                        return;
                    }
                    p.getItems().deleteItem2(25066, 1);
                    p.getItems().deleteItem2(28274, 20);
                    p.foundryPoints -= 500_000_000;
                    p.getItems().addItemUnderAnyCircumstance(25110, 1);
                    String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                            + " Has successfully created a Trailblazer Axe!";
                    PlayerHandler.executeGlobalMessage(msg);
                    p.getPA().closeAllWindows();
                }),

                    new DialogueOption("Infernal Harpoon (or). (500m Upgrade Points & 20 Smoked gems", p -> {
                        if (p.getItems().getInventoryCount(25059) == 0) {
                            p.sendMessage("@red@You don't have an Infernal Harpoon (or) to upgrade!");
                            p.getPA().closeAllWindows();
                            return;
                        }
                        if (p.getItems().getInventoryCount(28274) < 20) {
                            p.sendMessage("@red@You need 20 Smoke gems to upgrade your Harpoon");
                            p.getPA().closeAllWindows();
                            return;
                        }
                        if (p.foundryPoints < 500_000_000) {
                            p.sendMessage("@red@You require 500m Upgrade Points to upgrade your Harpoon!");
                            p.getPA().closeAllWindows();
                            return;
                        }
                        p.getItems().deleteItem2(25059, 1);
                        p.getItems().deleteItem2(28274, 20);
                        p.foundryPoints -= 500_000_000;
                        p.getItems().addItemUnderAnyCircumstance(25114, 1);
                        String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + p.getDisplayName()
                                + " Has successfully created a Trailblazer Harpoon!";
                        PlayerHandler.executeGlobalMessage(msg);
                        p.getPA().closeAllWindows();
                    })));
        }

        if (itemId == 10025) {
            c.getItems().deleteItem2(10025, 1);
            int rng = Misc.random(100);
            int item = (rng > 95 ? 19835 : rng > 75 && rng < 95 ? 2722 : rng > 50 && rng < 75 ? 2801 : 2677);
            c.getItems().addItemUnderAnyCircumstance(item,1);
        }

        if (itemId == 25481) {
            c.getClaimedLog().clear();
            c.getCollectionLog().getCollections().clear();
            c.getItems().deleteItem2(25481, 1);
            c.sendMessage("All your log's and rewards have been reset!");
        }

        if (itemId == 25478) {
            if (c.wildLevel <= 0) {
                    if (c.SafetyTimer > 0) {
                        c.sendMessage("You still have Bounty Protection Active!");
                    } else {
                        c.getItems().deleteItem2(25478, 1);
                        c.sendMessage("@red@You now have 30 minutes of protection from the bounty hunter system!");
                        c.SafetyTimer = (TimeUnit.MINUTES.toMillis(30) / 600);
                        c.getPA().sendGameTimer(ClientGameTimer.SAFETY_BUFFER, TimeUnit.MINUTES, (int) (c.SafetyTimer/100));
                        if (c.getBH().outsideBoundsTicks > 0) {
                            c.getBH().outsideBoundsTicks = 0;
                        }
                    }
            } else {
                c.sendMessage("You can only use this in a safe area!");
            }
        }

        for (Perks value : Perks.values()) {
            if (value.itemID == itemId) {
                c.getPerkSytem().attunePerk(itemId);
                return;
            }
        }

        if (c.getPetManagement().isCashMoney(itemId)) {
            c.getPetManagement().openInterface();
            return;
        }

        if (c.getPerkSytem().obtainPerk(itemId)) {
            return;
        }

        if (itemId == 26545) {
            if (System.currentTimeMillis() - c.clickDelay <= 2200) {
                return;
            }
            c.clickDelay = System.currentTimeMillis();
            int[] perks = {26546, 26547, 26548};
            c.getItems().deleteItem2(26545, 1);
            c.getItems().addItem(perks[Misc.random((perks.length-1))], 1);
            return;
        }

        if (itemSlot >= c.playerItems.length || itemSlot < 0) {
            return;
        }
        if (itemId != c.playerItems[itemSlot] - 1) {
            return;
        }
        if (c.isDead || c.getHealth().getCurrentHealth() <= 0) {
            return;
        }

        if (c.getInterfaceEvent().isActive()) {
            c.sendMessage("Please finish what you're doing.");
            return;
        }
        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return;
        }
        c.lastClickedItem = itemId;

        if (c.isFping() && itemId != 299) {
            return;
        }

        if (itemId == 22999) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.sendMessage("You can't use this in the wild or duel arena!");
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                c.sendMessage("You are not allowed to drink potions in the outlast hut.");
                return;
            }
            if (c.isDead) {
                return;
            }
            if (c.teleTimer > 0) {
                return;
            }

            int health = c.getHealth().getCurrentHealth();
            if (health <= 50) {
                c.sendMessage("I should get some more lifepoints before using this!");
                return;
            }
            if (c.getPotionTimer().elapsed() >= 2) {
                c.getPotionTimer().reset();
                c.getFoodTimer().reset();
                c.getPA().sendSound(334);
                c.getPotions().doInfOverload();
            }
            return;
        }
        if (itemId == 26364 ||itemId == 26358 ||itemId == 26362 ||itemId == 26360 ) {
            if (c.getInventory().containsAll(new ImmutableItem(26364), new ImmutableItem(26360),
                    new ImmutableItem(26358), new ImmutableItem(26362))) {
                c.getItems().deleteItem2(26364,1);
                c.getItems().deleteItem2(26360,1);
                c.getItems().deleteItem2(26358,1);
                c.getItems().deleteItem2(26362,1);
                c.getInventory().addOrDrop(new ImmutableItem(26356));
                c.sendMessage("Some how you assemble all the pieces into a frozen key!");
            } else {
                c.sendMessage("It looks like you're missing the other pieces.");
            }
            return;
        }

        if (itemId == 11481) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.sendMessage("You can't use this in the wild or duel arena!");
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                c.sendMessage("You are not allowed to drink potions in the outlast hut.");
                return;
            }
            if (c.isDead) {
                return;
            }
            if (c.teleTimer > 0) {
                return;
            }
            if (c.getPotionTimer().elapsed() >= 2) {
                c.getPotionTimer().reset();
                c.getFoodTimer().reset();
                c.getPA().sendSound(334);
                c.getPotions().drinkInfPrayerPot();
            }

            return;
        }

        if (itemId == 11429) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.sendMessage("You can't use this in the wild or duel arena!");
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                c.sendMessage("You are not allowed to drink potions in the outlast hut.");
                return;
            }
            if (c.isDead) {
                return;
            }
            if (c.teleTimer > 0) {
                return;
            }
            if (c.getPotionTimer().elapsed() >= 2) {
                c.getPotionTimer().reset();
                c.getFoodTimer().reset();
                c.getPA().sendSound(334);
                c.getPotions().drinkInfAgroPot();
            }
            return;
        }

        if (itemId == 11433) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.sendMessage("You can't use this in the wild or duel arena!");
                return;
            }
            if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                c.sendMessage("You are not allowed to drink potions in the outlast hut.");
                return;
            }
            if (c.isDead) {
                return;
            }
            if (c.teleTimer > 0) {
                return;
            }
            if (c.getPotionTimer().elapsed() >= 2) {
                c.getPotionTimer().reset();
                c.getFoodTimer().reset();
                c.getPA().sendSound(334);
                c.getPotions().drinkRagePot();
            }
            return;
        }

        c.getHerblore().clean(itemId);
        if (c.getFood().isFood(itemId)) {
            c.getFood().eat(itemId, itemSlot);
            return;
        } else if (c.getPotions().isPotion(itemId)) {
            c.getPotions().handlePotion(itemId, itemSlot);
            return;
        }
        if (c.getQuesting().handleItemClick(itemId)) {
            return;
        }
        Optional<Bone> bone = Prayer.isOperableBone(itemId);
        if (bone.isPresent()) {
            c.getPrayer().bury(bone.get());
            return;
        }
        TeleportTablets.operate(c, itemId);
        Packs.openPack(c, itemId);
        if (LootingBag.isLootingBag(itemId)) {
            c.getLootingBag().toggleOpen();
            return;
        }
        if (RunePouch.isRunePouch(itemId)) {
            c.getRunePouch().openRunePouch();
            return;
        }
        if (Cannon.clickItem(c, itemId)) {
            return;
        }
        if (io.xeros.content.dwarfleaguecannon.Cannon.clickItem(c, itemId)) {
            return;
        }
        if (ClaimDonatorScrollDialogue.clickScroll(c, itemId)) {
            return;
        }
        if (SanguinestiStaff.clickItem(c, itemId, 1)) {
            return;
        }
        if (TreasureTrails.firstClickItem(c, itemId)) {
            return;
        }

        switch (itemId) {
            case 26500:
                c.start(new DialogueBuilder(c).option("Which donor boss would you like to reset?", new DialogueOption("Queen Latsyrc (1 scroll)", plr -> {
                    if (plr.amDonated < 10) {
                        plr.sendMessage("@red@You do not have a high enough donor rank to do this.");
                        return;
                    }
                    if (plr.getItems().getInventoryCount(itemId) < 1) {
                        plr.sendMessage("@red@You do not have enough Donor Boss Scroll's to do this.");
                        return;
                    }
                    plr.getItems().deleteItem2(itemId, 1);
                    plr.setDonorBossKC(0);
                    plr.sendMessage("@red@You've reset your Queen Latsyrc KC Limit!");
                }), new DialogueOption("Avatar Of Creation (3 scroll)", plr -> {
                    if (plr.amDonated < 250) {
                        plr.sendMessage("@red@You do not have a high enough donor rank to do this.");
                        return;
                    }
                    if (plr.getItems().getInventoryCount(itemId) < 3) {
                        plr.sendMessage("@red@You do not have enough Donor Boss Scroll's to do this.");
                        return;
                    }
                    plr.getItems().deleteItem2(itemId, 3);
                    plr.setDonorBossKCx(0);
                    plr.sendMessage("@red@You've reset your Avatar Of Creation KC Limit!");
                }), new DialogueOption("Avatar Of Destruction (6 scroll)", plr -> {
                    if (plr.amDonated < 750) {
                        plr.sendMessage("@red@You do not have a high enough donor rank to do this.");
                        return;
                    }
                    if (plr.getItems().getInventoryCount(itemId) < 6) {
                        plr.sendMessage("@red@You do not have enough Donor Boss Scroll's to do this.");
                        return;
                    }
                    plr.getItems().deleteItem2(itemId, 6);
                    plr.setDonorBossKCy(0);
                    plr.sendMessage("@red@You've reset your Avatar Of Destruction KC Limit!");
                })));
                break;
            case MageArenaII.SYMBOL_ID:
                MageArenaII.handleEnchantedSymbol(c);
                return;
            case Items.MIMIC:
                MimicCasket.open(c);
                break;
            case 26886:
                c.sendMessage("This will give you a 50% extra chance at upgrading any item! One time use!");
                break;
            case 21730:
                c.sendMessage("Fallen from the centre of a Grotesque Guardian. This could be attached");
                c.sendMessage("to a pair of Bandos boots...");
                break;
            case 23517:
                if (c.getItems().playerHasItem(946)) {
                    c.getItems().deleteItem(23517, 1);
                    c.getItems().addItem(224, 100);
                } else {
                    c.sendMessage("You need a knife to open this.");
                }
                break;
            case 28696:
                if (c.getItems().playerHasItem(28696)) {
                    c.getItems().deleteItem(28696, 1);
                    c.SoulSplit = true;
                }
                break;
            case 28693:
                if (c.getItems().playerHasItem(28693)) {
                    c.getItems().deleteItem(28693, 1);
                    c.Turmoil = true;
                }
                break;
        case ResourceBoxSmall.BOX_ITEM:
            new ResourceBoxSmall().roll(c);
            break;
        case ResourceBoxMedium.BOX_ITEM:
            new ResourceBoxMedium().roll(c);
            break;
        case ResourceBoxLarge.BOX_ITEM:
            new ResourceBoxLarge().roll(c);
            break;
            case 23071:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(23071)) {
                    c.inDonatorBox = true;
                    c.getAncientCasket().quickOpen();
                    return;
                }
                break;
        case 21034:
        	c.getDH().sendDialogues(345, 9120);
        	break;
        case 21079:
        	c.getDH().sendDialogues(347, 9120);
        	break;
        case 22477:
			c.sendMessage("Attach it onto a dragon defender to make avernic defender.");
        	break;

        case 23185:
            if (!c.getPA().morphPermissions()) {
                return;
            }
            for (int i = 0; i <= 12; i++) {
                c.setSidebarInterface(i, 6014);
            }
            c.npcId2 = 9415;
            c.isNpc = true;
            c.playerStandIndex = -1;
            c.setUpdateRequired(true);
            c.morphed = true;
            c.setAppearanceUpdateRequired(true);
            break;
            case 19564:
        	if (c.wildLevel > 30 && !(c.getRights().isOrInherits(Right.STAFF_MANAGER))) {
				c.sendMessage("You can't teleport above level 30 in the wilderness.");
				return;
				}
                if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
                    c.getPA().startTeleport(3135, 3628, 0, "pod", false);
                } else {
                    c.getPA().startTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, "pod", false);
                }
    		break;
        case 2841:
        	if (!c.getItems().playerHasItem(2841)) {
		          		c.sendMessage("You need an Bonus XP Scroll to do this!");
        	   	return;
        	}
        	if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
        	    c.sendMessage("@red@Bonus XP Weekend is @gre@active@red@, no need to use that now!");
        	    return;
        	}
        	else if (!c.xpScroll && c.getItems().playerHasItem(2841)) {
        	    c.getItems().deleteItem(2841, 1);
        	    DoubleExpScroll.openScroll(c);
        	    c.sendMessage("@red@You have activated 1 hour of bonus experience.");
                c.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, 60);
        	    c.getQuestTab().updateInformationTab();
        	} else if (c.xpScroll) {
        	    c.sendMessage("@red@You already used this up.");
        	}
        	break;
            case 7968:
                if (!c.getItems().playerHasItem(7968)) {
                    c.sendMessage("You need an Bonus Pet Scroll to do this!");
                    return;
                }
                if (!c.skillingPetRateScroll && c.getItems().playerHasItem(7968)) {
                    c.getItems().deleteItem(7968, 1);
                    SkillPetRateIncreaseScroll.openScroll(c);
                    c.sendMessage("@red@You have activated 30 minutes of bonus skilling pet rate increase.");
                    c.getPA().sendGameTimer(ClientGameTimer.BONUS_SKILLING_PET_RATE, TimeUnit.MINUTES, 30);
                } else if (c.skillingPetRateScroll) {
                    c.sendMessage("@red@You already have a pet bonus going.");
                }
                break;
            case 24460:
                if (!c.getItems().playerHasItem(24460)) {
                    c.sendMessage("You need a Faster clues scroll to do this!");
                    return;
                }
                if (!c.fasterCluesScroll && c.getItems().playerHasItem(24460)) {
                    c.getItems().deleteItem(24460, 1);
                    CluescrollRateIncreaseScroll.openScroll(c);
                    c.sendMessage("@red@You have activated 30 minutes of bonus clues for pvm and skilling.");
                    c.getPA().sendGameTimer(ClientGameTimer.BONUS_CLUES, TimeUnit.MINUTES, 30);
                } else if (c.fasterCluesScroll) {
                    c.sendMessage("@red@You already have a faster clue rate going.");
                }
                break;

            case 12885:
            case 13277:
            case 19701:
            case 13245:
            case 12007:
            case 22106:
            case 12936:
            case 24495:
                c.getDH().sendDialogues(361, Npcs.BOSS_POINT_SHOP);
                break;

        case 6830:   
            c.getItems().deleteItem(6830, 1);
            c.gfx100(263);
//            c.getItems().addItemUnderAnyCircumstance(26714, 1);
//            c.getItems().addItemUnderAnyCircumstance(26715, 1);
//            c.getItems().addItemUnderAnyCircumstance(26716, 1);
//            c.getItems().addItemUnderAnyCircumstance(13237, 1);
//            c.getItems().addItemUnderAnyCircumstance(19547, 1);
//            c.getItems().addItemUnderAnyCircumstance(22109, 1);
//            c.getItems().addItemUnderAnyCircumstance(25916, 1);
//            c.getItems().addItemUnderAnyCircumstance(21000, 1);
//            c.getItems().addItemUnderAnyCircumstance(21905, 500);
//            c.getItems().addItemUnderAnyCircumstance(26225, 1);
//            c.getItems().addItemUnderAnyCircumstance(26221, 1);
//            c.getItems().addItemUnderAnyCircumstance(26223, 1);
//            c.getItems().addItemUnderAnyCircumstance(26229, 1);
//            c.getItems().addItemUnderAnyCircumstance(26227, 1);
//            c.getItems().addItemUnderAnyCircumstance(21793, 1);
//            c.getItems().addItemUnderAnyCircumstance(12002, 1);
//            c.getItems().addItemUnderAnyCircumstance(13681, 1);
//            c.getItems().addItemUnderAnyCircumstance(22323, 1);
//            c.getItems().addItemUnderAnyCircumstance(26382, 1);
//            c.getItems().addItemUnderAnyCircumstance(26384, 1);
//            c.getItems().addItemUnderAnyCircumstance(26386, 1);
//            c.getItems().addItemUnderAnyCircumstance(13239, 1);
//            c.getItems().addItemUnderAnyCircumstance(27112, 1);
//            c.getItems().addItemUnderAnyCircumstance(6570, 1);
//            c.getItems().addItemUnderAnyCircumstance(22325, 1);
//            c.getItems().addItemUnderAnyCircumstance(20788, 1);
//            c.getItems().addItemUnderAnyCircumstance(30018, 1);
//            c.getItems().addItemUnderAnyCircumstance(2528, 1000);
//            c.getItems().addItemUnderAnyCircumstance(6805, 3);
//            c.getItems().addItemUnderAnyCircumstance(2396, 1);
                break;
            case 6832://yt stream giveaway box tier 1
                c.getItems().deleteItem(6832, 1);
                c.gfx100(263);
                c.setStoreDonated(c.getStoreDonated() + 500);
                c.getItems().addItemUnderAnyCircumstance(27235, 1);
                c.getItems().addItemUnderAnyCircumstance(27238, 1);
                c.getItems().addItemUnderAnyCircumstance(27241, 1);
                c.getItems().addItemUnderAnyCircumstance(26235, 1);
                c.getItems().addItemUnderAnyCircumstance(22249, 1);
                c.getItems().addItemUnderAnyCircumstance(33207, 1);
                c.getItems().addItemUnderAnyCircumstance(25413, 1);
                c.getItems().addItemUnderAnyCircumstance(25404, 1);
                c.getItems().addItemUnderAnyCircumstance(25416, 1);
                c.getItems().addItemUnderAnyCircumstance(25410, 1);
                c.getItems().addItemUnderAnyCircumstance(25407, 1);
                c.getItems().addItemUnderAnyCircumstance(26551, 1);
                c.getItems().addItemUnderAnyCircumstance(19720, 1);
                c.getItems().addItemUnderAnyCircumstance(33205, 1);
                c.getItems().addItemUnderAnyCircumstance(28254, 1);
                c.getItems().addItemUnderAnyCircumstance(28256, 1);
                c.getItems().addItemUnderAnyCircumstance(28258, 1);
                c.getItems().addItemUnderAnyCircumstance(22954, 1);
                c.getItems().addItemUnderAnyCircumstance(13372, 1);
                c.getItems().addItemUnderAnyCircumstance(20366, 1);
                c.getItems().addItemUnderAnyCircumstance(27552, 1);
                c.getItems().addItemUnderAnyCircumstance(33203, 1);
                c.getItems().addItemUnderAnyCircumstance(608, 1);
                c.getItems().addItemUnderAnyCircumstance(25975, 1);
                c.getItems().addItemUnderAnyCircumstance(23859, 1);
                c.getItems().addItemUnderAnyCircumstance(33204, 1);
                c.getItems().addItemUnderAnyCircumstance(11433, 1);
                c.getItems().addItemUnderAnyCircumstance(30022, 1);
                break;

            case 6829://Premium Battle Pass Box
            if  (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                    !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                    || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                    || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                    || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                    || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                    || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                    || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                    || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                    || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                    || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6829)) {
                    c.inDonatorBox = true;
                    c.getP2pDivisionBox().openInterface();
                    return;
                }
                break;

            case 6831://Free2play battle pass box
            if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                    !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                    || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                    || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                    || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                    || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                    || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                    || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                    || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                    || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                    || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6831)) {
                    c.inDonatorBox = true;
                    c.getF2pDivisionBox().openInterface();
                    return;
                }
                break;


            case 6833://yt stream giveaway box tier2
                if (c.getItems().freeSlots() > 20) {
                    c.getItems().deleteItem(6833, 1);
                    c.gfx100(263);
                    c.getItems().addItem(6199, 2);//normal m box
                    c.getItems().addItem(6828, 2);//super m box
                    c.getItems().addItem(13346, 2);//ultra m box
                    c.getItems().addItem(6769, 5);//5 scroll
                    c.getItems().addItem(2396, 2);//25 scroll
                    c.getDH().sendStatement("Box has been logged for staff, please give away responsibly!");
                    Discord.writeServerSyncMessage("[YOUTUBE STREAM BOX T2] Opened by " + c.getDisplayName() + ".");
                    PlayerHandler.executeGlobalStaffMessage("[@red@Staff Message@bla@] <col=255>" + c.getDisplayName() + " has opened a stream box t2!");
                } else {
                    c.sendMessage("Please clear your inventory before opening.");
                }
                break;
            case 2697:
                if (c.getItems().playerHasItem(2697, 1)) {
                    c.getItems().deleteItem(2697, 1);
                    c.gfx100(263);
                    c.donatorPoints += 10;
                    c.amDonated += 10;
                    c.sendMessage("Thank you for donating! 10$ has been added to your total credit.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 2698:
                if (c.getItems().playerHasItem(2698, 1)) {
                    c.getItems().deleteItem(2698, 1);
                    c.gfx100(1800);
                    c.donatorPoints += 50;
                    c.amDonated += 50;
                    c.sendMessage("Thank you for donating! +50$ Donator rank + 50 points!.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 2699:
                if (c.getItems().playerHasItem(2699, 1)) {
                    c.getItems().deleteItem(2699, 1);
                    c.gfx100(1800);
                    c.donatorPoints += 100;
                    c.amDonated += 100;
                    c.sendMessage("Thank you for donating! +100$ Donator rank + 100 points!.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 2700:
                if (c.getItems().playerHasItem(2700, 1)) {
                    c.getItems().deleteItem(2700, 1);
                    c.gfx100(1800);
                    c.donatorPoints += 250;
                    c.amDonated += 250;
                    c.sendMessage("Thank you for donating! +250$ Donator rank + 250 points!.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 2702:
                if (c.getItems().playerHasItem(2702, 1)) {
                    c.getItems().deleteItem(2702, 1);
                    c.gfx100(1800);
                    c.donatorPoints += 500;
                    c.amDonated += 500;
                    c.sendMessage("Thank you for donating! +500$ Donator rank + 500 points!.");
                    c.updateRank();
                    c.getPA().closeAllWindows();
                    return;
                }
                break;
            case 1004:
                if (c.getItems().playerHasItem(1004)) {
                    c.getItems().deleteItem(1004, 1);
                    c.getItems().addItem(995, 20000000);
                    c.sendMessage("You claim the coin stack and it turns into 20m gp.");
                }
                break;
            case 8899:
                if (c.getItems().playerHasItem(8899)) {
                    c.getItems().deleteItem(8899, 1);
                    c.getItems().addItem(995, 50000000);
                    c.sendMessage("You claim the coin stack and it turns into 50m gp.");
                }
                break;
            case 6121:
            	if (!c.barbarian && c.getItems().playerHasItem(6121)) {
            		c.getItems().deleteItem(6121, 1);
            		c.barbarian = true;
                    c.breakVials = true;
            		c.getDH().sendStatement("You may now use ::vials to turn off and on vial smashing!", "It is now set to on.");
            		
            	}else if (c.barbarian) {
            		c.sendMessage("You already learned how to do this");
            	}
            	break;
            case 299:
                if (Boundary.isIn(c, Boundary.FLOWER_POKER_AREA)) {
                    if (c.isFping())
                        c.getFlowerPoker().plantSeed(c, true, false);
                    return;
                }
            	if (c.getRegionProvider().getClipping(c.absX, c.absY, c.heightLevel) != 0
				|| Server.getGlobalObjects().anyExists(c.absX, c.absY, c.heightLevel)) {
            		c.sendMessage("You cannot plant a flower here.");
				return;
            	}
                /*if (!c.getItems().playerHasItem(15098, 1)) {
            		c.sendMessage("You need to purchase a dice bag to use mithril seeds.");
                	return;
                }
                if(!Boundary.isIn(c, DICING_AREA)) {
                    c.sendMessage("You must be in the dice area to plant mithril seeds.");
                    return;
                }*/
                if(System.currentTimeMillis() - c.lastPlant < 250) {
                    return;
                }
                c.lastPlant = System.currentTimeMillis();
                plantMithrilSeed(c);
                break;
            case 13438: //slayer m chest
                if (c.getItems().playerHasItem(13438)) {
                    new SlayerMysteryBox(c).quickOpen();
                }
                break;
            case 21027: //dark relic
            	  c.inLamp = true;
            	  c.usingLamp = true;
                  c.normalLamp = true;
                  c.antiqueLamp = false;
                  c.sendMessage("You rub the lamp...");
                  c.getPA().showInterface(2808);
            	break;
            case 11489:
                if (Boundary.isIn(c, Boundary.DUEL_ARENA) || (Boundary.isIn(c, Boundary.WILDERNESS) && c.wildLevel > 0)) {
                    c.sendMessage("You can't use this in the wild or duel arena!");
                    return;
                }
                if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                    c.sendMessage("You are not allowed to drink potions in the outlast hut.");
                    return;
                }
                if (c.isDead) {
                    return;
                }
                if (c.teleTimer > 0) {
                    return;
                }

                if (c.getPotionTimer().elapsed() >= 2) {
                    c.getPotionTimer().reset();
                    c.getFoodTimer().reset();
                    c.getPA().sendSound(334);
                    c.startAnimation(4939);
                    c.forcedChat("I HOLD THE TRUE POWER!");
                    c.startGraphic(new Graphic(1920));
                    c.startGraphic(new Graphic(1800));
                    c.startGraphic(new Graphic(1850));
                    c.startGraphic(new Graphic(1921));
                    c.getPotions().doAIOPotion();
                }
                    break;
            case 13148:
                c.start(new DialogueBuilder(c).statement("This lamp will reset a skill of your choice.").exit(plr -> {
                    c.inLamp = true;
                    c.usingLamp = true;
                    c.normalLamp = true;
                    c.antiqueLamp = false;
                    c.sendMessage("You have rubbed the skill reset lamp.");
                    c.getPA().showInterface(2808);
                }));
          	break;
            case 12579:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12579)) {
                    c.inDonatorBox = true;
                    c.getArboBox().openInterface();
                    return;
                }
                break;
            case 12582:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12582)) {
                    c.inDonatorBox = true;
                    c.getCoxBox().openInterface();
                    return;
                }
                break;
            case 12588:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12588)) {
                    c.inDonatorBox = true;
                    c.getDonoBox().openInterface();
                    return;
                }
                break;
            case 19891:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(19891)) {
                    c.inDonatorBox = true;
                    c.getTobBox().openInterface();
                    return;
                }
                break;
            case 19897:
                if  (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(19897)) {
                    c.inDonatorBox = true;
                    c.getCosmeticBox().openInterface();
                    return;
                }
                break;
            case 6680:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6680)) {
                    c.inDonatorBox = true;
                    c.getMiniArboBox().openInterface();
                    return;
                }
                break;
            case 12585:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12585)) {
                    c.inDonatorBox = true;
                    c.getMiniCoxBox().openInterface();
                    return;
                }
                break;
            case 19887:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(19887)) {
                    c.inDonatorBox = true;
                    c.getMiniDonoBox().openInterface();
                    return;
                }
                break;
            case 6679:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6679)) {
                    c.inDonatorBox = true;
                    c.getMiniNormalMysteryBox().openInterface();
                    return;
                }
                break;
            case 6677:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6677)) {
                    c.inDonatorBox = true;
                    c.getMiniSmb().openInterface();
                    return;
                }
                break;
            case 19895:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(19895)) {
                    c.inDonatorBox = true;
                    c.getMiniTobBox().openInterface();
                    return;
                }
                break;
            case 6678:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(6678)) {
                    c.inDonatorBox = true;
                    c.getMiniUltraBox().openInterface();
                    return;
                }
                break;
            case 12789:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12789)) {
                    c.inDonatorBox = true;
                    c.getYoutubeMysteryBox().openInterface();
                    return;
                }
                break;
            case 12161:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(12161)) {
                    c.inDonatorBox = true;
                    c.getChristmasBox().openInterface();
                    return;
                }
                break;
            case 13346:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
    			else if (c.getItems().playerHasItem(13346)) {
                	c.inDonatorBox = true;
                    c.getUltraMysteryBox().openInterface();
                    return;
                }
                break;
            case 6199:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
    			else if (c.getItems().playerHasItem(6199)) {
                    c.getNormalMysteryBox().openInterface();
                	c.inDonatorBox = true;
    				c.stopMovement();
                    return;
                }
                break;
            case 6828:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
    			else if (c.getItems().playerHasItem(6828)) {
            		c.getSuperMysteryBox().openInterface();
                	c.inDonatorBox = true;
    				c.stopMovement();
                    return;
                }
                break;
            case 8167:
                if (!(c.getSuperMysteryBox().canMysteryBox) || !(c.getNormalMysteryBox().canMysteryBox) ||
                        !(c.getUltraMysteryBox().canMysteryBox) || !(c.getFoeMysteryBox().canMysteryBox)
                        || !(c.getYoutubeMysteryBox().canMysteryBox) || !(c.getChristmasBox().canMysteryBox)
                        || !(c.getF2pDivisionBox().canMysteryBox) || !(c.getP2pDivisionBox().canMysteryBox)
                        || !(c.getAncientCasket().canMysteryBox)|| !(c.getArboBox().canMysteryBox)
                        || !(c.getCoxBox().canMysteryBox)|| !(c.getTobBox().canMysteryBox)
                        || !(c.getDonoBox().canMysteryBox)|| !(c.getCosmeticBox().canMysteryBox)
                        || !(c.getMiniArboBox().canMysteryBox)|| !(c.getMiniCoxBox().canMysteryBox)
                        || !(c.getMiniDonoBox().canMysteryBox)|| !(c.getMiniNormalMysteryBox().canMysteryBox)
                        || !(c.getMiniSmb().canMysteryBox)|| !(c.getMiniTobBox().canMysteryBox)
                        || !(c.getMiniUltraBox().canMysteryBox)) {
                    c.getPA().showInterface(47000);
                    c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
                    return;
                }
                else if (c.getItems().playerHasItem(8167)) {
                    c.getFoeMysteryBox().openInterface();
                    c.inDonatorBox = true;
                    c.stopMovement();
                    return;
                }
                break;
            case 21347:
                c.boltTips = true;
                c.arrowTips = false;
                c.javelinHeads = false;
                c.sendMessage("Your Amethyst method is now Bolt Tips!");
                break;
            case 20724:
                DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
                if (Objects.nonNull(session)) {
                    if (session.getRules().contains(Rule.NO_DRINKS)) {
                        c.sendMessage("Using the imbued heart with 'No Drinks' option is forbidden.");
                        return;
                    }
                }
                if (System.currentTimeMillis() - c.lastHeart < 420000) {
                    c.sendMessage("You must wait 7 minutes between each use.");
                } else {
                    c.getPA().imbuedHeart();
                    c.lastHeart = System.currentTimeMillis();
                }
                break;
            case PvmCasket.PVM_CASKET: //Pvm Casket
            	if (System.currentTimeMillis() - c.openCasketTimer > 350) {
                	if (c.getItems().playerHasItem(405)) {
                        c.getPvmCasket().roll(c);
                        c.openCasketTimer =  System.currentTimeMillis();
                    }
                }
            	break;
            case Items.DWARF_CANNON_SET:
                if (c.getItems().freeSlots() < 4) {
                    c.sendMessage("You need at least 4 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(Items.DWARF_CANNON_SET, 1)) {
                    c.getItems().deleteItem(Items.DWARF_CANNON_SET, 1);
                    c.getItems().addItem(Items.CANNON_BASE, 1);
                    c.getItems().addItem(Items.CANNON_STAND, 1);
                    c.getItems().addItem(Items.CANNON_BARRELS, 1);
                    c.getItems().addItem(Items.CANNON_FURNACE, 1);
                }
                break;
            case 11666://full void token
                if (c.getItems().freeSlots() < 6) {
                    c.sendMessage("You need at least 6 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(11666)) {
                    c.getItems().deleteItem(11666, 1);
                    c.getItems().addItem(Items.ELITE_VOID_ROBE, 1);
                    c.getItems().addItem(Items.ELITE_VOID_TOP, 1);
                    c.getItems().addItem(Items.VOID_KNIGHT_GLOVES, 1);
                    c.getItems().addItem(Items.VOID_RANGER_HELM, 1);
                    c.getItems().addItem(Items.VOID_MAGE_HELM, 1);
                    c.getItems().addItem(Items.VOID_MELEE_HELM, 1);
                }
                break;
            case 7774:
                if (c.getItems().playerHasItem(7774)) {
                    c.getItems().deleteItem2(7774, 1);
                    c.donatorPoints += 10;
                    c.sendMessage("Your donation credit has increased by 10.");
                }
                break;
            case 7775:
                if (c.getItems().playerHasItem(7775)) {
                    c.getItems().deleteItem2(7775, 1);
                    c.donatorPoints += 50;
                    c.sendMessage("Your donation credit has increased by 50.");
                }
                break;
            case 7776:
                if (c.getItems().playerHasItem(7776)) {
                    c.getItems().deleteItem2(7776, 1);
                    c.donatorPoints += 100;
                    c.sendMessage("Your donation credit has increased by 100.");
                }
                break;
            case 12873://guthan
                if (c.getItems().freeSlots() < 4) {
                    c.sendMessage("You need at least 4 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(12873, 1)) {
                    c.getItems().addItem(4724, 1);
                    c.getItems().addItem(4726, 1);
                    c.getItems().addItem(4728, 1);
                    c.getItems().addItem(4730, 1);
                }
                c.getItems().deleteItem(12873, 1);
                break;

            case 13024://rune leg
                if (c.getItems().freeSlots() < 4) {
                    c.sendMessage("You need at least 4 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(13024, 1)) {
                    c.getItems().addItem(1163, 1);
                    c.getItems().addItem(1127, 1);
                    c.getItems().addItem(1079, 1);
                    c.getItems().addItem(1201, 1);
                }
                c.getItems().deleteItem(13024, 1);
                break;
            case 13026://rune skirt
                if (c.getItems().freeSlots() < 4) {
                    c.sendMessage("You need at least 4 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(13026, 1)) {
                    c.getItems().addItem(1163, 1);
                    c.getItems().addItem(1127, 1);
                    c.getItems().addItem(1093, 1);
                    c.getItems().addItem(1201, 1);
                }
                c.getItems().deleteItem(13026, 1);
                break;
            case 23113://Mystic blue
                if (c.getItems().freeSlots() < 5) {
                    c.sendMessage("You need at least 5 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(23113, 1)) {
                    c.getItems().addItem(4089, 1);
                    c.getItems().addItem(4091, 1);
                    c.getItems().addItem(4093, 1);
                    c.getItems().addItem(4097, 1);
                    c.getItems().addItem(4095, 1);
                }
                c.getItems().deleteItem(23113, 1);
                break;
            case 23116://Mystic Red
                if (c.getItems().freeSlots() < 5) {
                    c.sendMessage("You need at least 5 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(23116, 1)) {
                    c.getItems().addItem(4099, 1);
                    c.getItems().addItem(4101, 1);
                    c.getItems().addItem(4103, 1);
                    c.getItems().addItem(4105, 1);
                    c.getItems().addItem(4107, 1);
                }
                c.getItems().deleteItem(23116, 1);
                break;
            case 23110://Mystic yellow
                if (c.getItems().freeSlots() < 5) {
                    c.sendMessage("You need at least 5 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(23110, 1)) {
                    c.getItems().addItem(4109, 1);
                    c.getItems().addItem(4111, 1);
                    c.getItems().addItem(4113, 1);
                    c.getItems().addItem(4115, 1);
                    c.getItems().addItem(4117, 1);
                }
                c.getItems().deleteItem(23110, 1);
                break;
            case 12871://black dhide
                if (c.getItems().freeSlots() < 3) {
                    c.sendMessage("You need at least 3 free slots to open this.");
                    return;
                }
                if (c.getItems().playerHasItem(12871, 1)) {
                    c.getItems().addItem(2503, 1);
                    c.getItems().addItem(2497, 1);
                    c.getItems().addItem(2491, 1);
                }
                c.getItems().deleteItem(12871, 1);
                break;
    		case 12875://verac
    			if (c.getItems().freeSlots() < 4) {
    				c.sendMessage("You need at least 4 free slots to open this.");
    				return;
    			}
    			if (c.getItems().playerHasItem(12875, 1)) {
    				c.getItems().addItem(4753, 1);
    				c.getItems().addItem(4755, 1);
    				c.getItems().addItem(4757, 1);
    				c.getItems().addItem(4759, 1);
    			}
    	            c.getItems().deleteItem(12875, 1);
    			break;
    		case 12877://dharok
    			if (c.getItems().freeSlots() < 4) {
    				c.sendMessage("You need at least 4 free slots to open this.");
    				return;
    			}
    			if (c.getItems().playerHasItem(12877, 1)) {
    				c.getItems().addItem(4716, 1);
    				c.getItems().addItem(4718, 1);
    				c.getItems().addItem(4720, 1);
    				c.getItems().addItem(4722, 1);
    			}
    	            c.getItems().deleteItem(12877, 1);
    			break;
    		case 12879://torags
    			if (c.getItems().freeSlots() < 4) {
    				c.sendMessage("You need at least 4 free slots to open this.");
    				return;
    			}
    			if (c.getItems().playerHasItem(12879, 1)) {
    				c.getItems().addItem(4745, 1);
    				c.getItems().addItem(4747, 1);
    				c.getItems().addItem(4749, 1);
    				c.getItems().addItem(4751, 1);
    			}
    	            c.getItems().deleteItem(12879, 1);
    			break;
    		case 12881://ahrims
    			if (c.getItems().freeSlots() < 4) {
    				c.sendMessage("You need at least 4 free slots to open this.");
    				return;
    			}
    			if (c.getItems().playerHasItem(12881, 1)) {
    				c.getItems().addItem(4708, 1);
    				c.getItems().addItem(4710, 1);
    				c.getItems().addItem(4712, 1);
    				c.getItems().addItem(4714, 1);
    			}
    	            c.getItems().deleteItem(12881, 1);
    			break;
    		case 12883://karils
    			if (c.getItems().freeSlots() < 4) {
    				c.sendMessage("You need at least 4 free slots to open this.");
    				return;
    			}
    			if (c.getItems().playerHasItem(12883, 1)) {
    				c.getItems().addItem(4732, 1);
    				c.getItems().addItem(4734, 1);
    				c.getItems().addItem(4736, 1);
    				c.getItems().addItem(4738, 1);
    			}
    	            c.getItems().deleteItem(12883, 1);
    			break;
            case 10006:
                Hunter.lay(c, new BirdSnare(c));
                break;
            case 10008:
                Hunter.lay(c, new BoxTrap(c));
                break;
            case 13249:
                if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
                    c.sendMessage("Please leave the outlast hut area to teleport.");
                    return ;
                }
                if (Boundary.isIn(c, Boundary.OUTLAST)) {
                    c.sendMessage("Please leave the outlast hut area to teleport.");
                    return ;
                }
                if (Boundary.isIn(c, Boundary.WILDERNESS_PARAMETERS)) {
                    c.sendMessage("Please leave the wild.");
                    return ;
                }
/*                if (!Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
                    c.sendMessage("Please use this in edgeville.");
                    return ;
                }*/
                c.isWc = false;
                if (c.isOverloading) {
                    c.sendMessage("You cannot teleport while taking overload damage.");
                    return;
                }
                if (c.isFping()) {
                    /**
                     * Cannot do action while fping
                     */
                    c.sendMessage("You cannot teleport while fping.");

                    return;
                }

                if (c.stopPlayerSkill) {
                    SkillHandler.resetPlayerSkillVariables(c);
                    c.stopPlayerSkill = false;
                }
                if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && (!c.getSlayer().getTask().get().getPrimaryName().equals("hellhound") && !c.getSlayer().getTask().get().getPrimaryName().equals("cerberus")))) {
                    c.sendMessage("You must have an active cerberus or hellhound task to use this.");
                    return;
                }
            	c.getItems().deleteItem(13249, 1);

                if (!c.getSlayer().isCerberusRoute()) {
                    c.sendMessage("You have bought Route into cerberus cave. please wait till you will be teleported.");
                    Cerberus.init(c);
                    c.cerbDelay = System.currentTimeMillis();
                    return;
                }

				if (Server.getEventHandler().isRunning(c, "cerb")) {
					c.sendMessage("You're about to fight start the fight, please wait.");
					return;
				}
                Cerberus.init(c);
                break;

            case 13226:
                c.getHerbSack().fillSack();
                break;

            case 12020:
                c.getGemBag().fillBag();
                break;

            case 5509:
                Pouches.fill(c, Pouches.Pouch.forId(itemId), 0);
                break;
            case 5510:
                Pouches.fill(c, Pouches.Pouch.forId(itemId), 1);
                break;
            case 5512:
                Pouches.fill(c, Pouches.Pouch.forId(itemId), 2);
                break;
            case 5514:
                Pouches.fill(c, Pouches.Pouch.forId(itemId), 3);
                break;
            case 5515:
                Pouches.fill(c, Pouches.Pouch.forId(itemId), 4);
                break;

        }

        if (itemId == 2678) {
            c.getDH().sendDialogues(657, -1);
            return;
        }
        if (itemId == 8015 || itemId == 8014) {
            NonCombatSpellData.attemptDate(c, itemId);
        }
        if (itemId == 9553) {
            c.getPotions().eatChoc(itemSlot);
        }
        if (itemId == 12846) {
            c.getDH().sendDialogues(578, -1);
        }
        if (itemId == 12938) {
            if (c.amDonated >= 1000) {
                c.getPA().movePlayer(2213, 3056,0);
                return;
            }
            c.getItems().deleteItem(12938, 1);
            c.getPA().startTeleport(2205, 3056, 0, "modern", false);
            return;
        }
        if (itemId == 4155) {
            if (!c.getSlayer().getTask().isPresent()) {
                c.sendMessage("You do not have a task, please talk with a slayer master!");
                return;
            }
            c.sendMessage("I currently have " + c.getSlayer().getTaskAmount() + " " + c.getSlayer().getTask().get().getPrimaryName() + "'s to kill.");
            c.getPA().closeAllWindows();
        }
        if (itemId == 2839) {
            if (c.getSlayer().getUnlocks().contains(SlayerUnlock.MALEVOLENT_MASQUERADE)) {
                c.sendMessage("You have already learned this recipe. You have no more use for this scroll.");
                return;
            }
            if (c.getItems().playerHasItem(2839)) {
                c.getSlayer().getUnlocks().add(SlayerUnlock.MALEVOLENT_MASQUERADE);
                c.sendMessage("You have learned the slayer helmet recipe. You can now assemble it");
                c.sendMessage("using a Black Mask, Facemask, Nose peg, Spiny helmet and Earmuffs.");
                c.getItems().deleteItem(2839, 1);
            }
        }
        if (itemId == 15098) {
            DiceHandler.rollDice(c);
        }
        if (itemId == 2701) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
                return;
            }
            if (c.getItems().playerHasItem(2701, 1)) {
                c.getDH().sendDialogues(4004, -1);
            }
        }

        if (itemId == 7509) {
            if (c.getPosition().inDuelArena() || Boundary.isIn(c, Boundary.DUEL_ARENA)) {
                c.sendMessage("You cannot do this here.");
                return;
            }
            if (c.getHealth().getStatus().isPoisoned() || c.getHealth().getStatus().isVenomed()) {
                c.sendMessage("You are effected by venom or poison, you should cure this first.");
                return;
            }
            if (c.getHealth().getCurrentHealth() <= 1) {
                c.sendMessage("I better not do that.");
                return;
            }
            c.forcedChat("Ow! I nearly broke a tooth!");
            c.startAnimation(829);
            // c.getHealth().reduce(1);
            c.appendDamage(1, Hitmark.HIT);
            c.getPA().sendSound(334);
            return;
        }
        if (itemId == 10269) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10269, 1)) {
                c.getItems().addItem(995, 30000);
                c.getItems().deleteItem(10269, 1);
            }
        }
        if (itemId == 10271) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10271, 1)) {
                c.getItems().addItem(995, 10000);
                c.getItems().deleteItem(10271, 1);
            }
        }
        if (itemId == 10273) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10273, 1)) {
                c.getItems().addItem(995, 14000);
                c.getItems().deleteItem(10273, 1);
            }
        }
        if (itemId == 10275) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10275, 1)) {
                c.getItems().addItem(995, 18000);
                c.getItems().deleteItem(10275, 1);
            }
        }
        if (itemId == 10277) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10277, 1)) {
                c.getItems().addItem(995, 22000);
                c.getItems().deleteItem(10277, 1);
            }
        }
        if (itemId == 10279) {
            if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
                return;
            }
            if (c.getItems().playerHasItem(10279, 1)) {
                c.getItems().addItem(995, 26000);
                c.getItems().deleteItem(10279, 1);
            }
        }

        /*Coin Bags */
        if (itemId == 10832)
            if (c.getItems().playerHasItem(10832)) {
                c.getCoinBagSmall().open();
                return;
            }
        if (itemId == 10833)
            if (c.getItems().playerHasItem(10833)) {
                c.getCoinBagMedium().open();
                return;
            }
        if (itemId == 10834)
            if (c.getItems().playerHasItem(10834)) {
                c.getCoinBagLarge().open();
                return;
            }
        if (itemId == 10835)
            if (c.getItems().playerHasItem(10835)) {
                c.getCoinBagBuldging().open();
                return;
            }
        if (itemId == 11739)
            if (c.getItems().playerHasItem(11739)) {
                c.getVoteMysteryBox().roll(c);
                return;
            }
        if (itemId == 22993)
            if (c.getItems().playerHasItem(22993)) {
                c.getSeedBox().roll(c);
                return;
            }
        if (itemId == 11738)
            if (c.getItems().playerHasItem(11738)) {
                c.getHerbBox().roll(c);
                return;
            }

        if (itemId == 20703) //Daily Gear Box
            if (c.getItems().playerHasItem(20703)) {
                c.getDailyGearBox().open();
                return;
            }
        if (itemId == 20791) //Daily Skilling Box
            if (c.getItems().playerHasItem(20791)) {
                c.getDailySkillBox().open();
                return;
            }
		/*if (itemId == 7310) //Skill Casket
			if (c.getItems().playerHasItem(7310)) {
				c.getSkillCasket().open();
				return;
			}*/

        if (itemId == 2528) {
        	c.inLamp = true;
            c.usingLamp = true;
            c.normalLamp = true;
            c.antiqueLamp = false;
            c.sendMessage("You rub the lamp...");
            c.sendMessage("@red@Caution this will use all lamps in your inventory!");
            c.getPA().showInterface(2808);
            return;
        }

   }
}