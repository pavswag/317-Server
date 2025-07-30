package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.Items;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.bot.BotBehaviour;
import io.xeros.model.EquipmentSetup;
import io.xeros.util.Captcha;
import io.xeros.util.Misc;
import org.jetbrains.annotations.NotNull;
import io.xeros.content.tournaments.TourneyManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bots extends Command {

    private static int botCounter = 0;

    private static final String[] PREFIXES = {
            "Iron", "Rune", "Dark", "Sir", "Lady", "Lord", "Zez", "Woox", "B0aty", "Soup",
            "Torv", "Skill", "Fram", "Solo", "Guth", "Zammy", "Sara", "Max", "Obby",
            "Barro", "Void", "Arma", "Band", "Vork", "Hydr", "Krak", "TzKal", "Jad",
            "Corp", "Nex", "Nylo", "Sire", "Zul", "Nigh", "Tham", "Anc", "Blood", "Soul",
            "Earth", "Air", "Fire", "Watr", "Death", "Nat", "Law", "Cosm", "Trim",
            "Fury", "Shad", "Divi", "Spec", "Just", "Virt", "Clue", "Frem", "Luna",
            "Curs", "Prim", "Asc", "Wild", "Demo", "Grim", "Hex", "Bara", "Freez",
            "Blast", "Tox", "Smok", "Gnom", "Shade", "Gob", "Urn", "Zeny", "Vial",
            "Nom", "Rock", "Bass", "Pot", "Seed", "Bag", "Maple", "Flet", "Skul",
            "Shay", "Glari", "Pouch", "Card", "Bun", "Pres", "Pick", "Myst", "Bear",
            "RuneL", "Scape", "Gz", "Alt", "Main", "Farm", "Loot", "Rush", "Tbow",
            "Dclaw", "Fang", "Cape", "Voidr", "Torm", "Whip", "Tent", "Slay", "Sang",
            "Kodai", "Angler", "Pros", "Guard", "Darkz", "Ghost", "Fiend", "Slug",
            "Worm", "Dust", "Spark", "Dart", "Edge", "Var", "Yan", "Lum", "Seer",
            "Canif", "Menaph", "RuneG", "Ardy", "Port", "Gran", "Blastf", "Voidw",
            "Tarn", "Grave", "Lock", "RuneX", "PKX", "LootX", "MystX", "Xeno", "Nite"
    };

    private static final String[] SUFFIXES = {
            "Mage", "Knight", "Pker", "Scape", "Slay", "Tank", "Fury", "Boss", "Hex",
            "Skul", "Farm", "Fish", "Run", "Cook", "Mine", "WC", "Thief", "Spec", "Cape",
            "Champ", "Main", "Alt", "HC", "UIM", "Iron", "Max", "Gamb", "Loot", "Zerk",
            "Whip", "Mage", "Bow", "Claws", "Fletch", "Grind", "AFK", "Sweat", "Dead",
            "Def", "Atk", "Str", "Wis", "Dps", "Troll", "Trap", "Tank", "Rush", "God",
            "Noob", "Vet", "Nerd", "Skill", "Cape", "Box", "Key", "Bag", "Drop", "Junk",
            "Raid", "Smack", "Hit", "Splash", "Zap", "Pop", "Crit", "Dash", "Spam",
            "Grief", "Mule", "Bot", "Lure", "Lag", "Gf", "Ez", "Hard", "Soft", "Blast",
            "Arc", "Myst", "Wand", "Lootr", "Grim", "Fix", "Fang", "Rushr", "Split",
            "Die", "Farmr", "Herb", "Mix", "Stab", "Bonk", "Bop", "Bash", "Swing",
            "Juke", "Pet", "RNG", "Cube", "Shank", "Peep", "Quick", "Bank", "Click",
            "Skillz", "Scope", "Zapz", "Shiv", "Wreck", "Scout", "Feed", "Stack", "Tame",
            "Luck", "Flee", "Loop", "Bagz", "Fox", "Seal", "Stash", "Chip", "Rock",
            "Drip", "View", "Cast", "Flick", "Chop", "Splitz", "Boop", "Crash", "Rev"
    };


    private static String randomBotName() {
        String prefix = PREFIXES[Misc.random(PREFIXES.length - 1)];
        String suffix = SUFFIXES[Misc.random(SUFFIXES.length - 1)];
        int num = Misc.random(99);
        return prefix + suffix + String.format("%02d", num);
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
            player.sendMessage("Only owners can use this command.");
            return;
        }

        String[] args = input.split(" ");
        switch (args[0]) {
            case "spawn":
                spawnBots(player, Integer.parseInt(args[1]), null);
                break;
            case "fight":
                spawnBots(player, Integer.parseInt(args[1]), BotBehaviour.Type.FIGHT_NEAREST_NPC);
                break;
            case "miner":
                spawnBots(player, Integer.parseInt(args[1]), BotBehaviour.Type.MINE_NEAREST_ROCK);
                break;
            case "fish":
                spawnBots(player, Integer.parseInt(args[1]), BotBehaviour.Type.FISH_NEAREST_SPOT);
                break;
            case "spawnpker":
                spawnBots(player, Integer.parseInt(args[1]), BotBehaviour.Type.PK_NEAREST_PLAYER, "Pure");
                break;
            case "joinoutlast":
                joinOutlastBots();
                break;
            case "wc":
                spawnBots(player, Integer.parseInt(args[1]), BotBehaviour.Type.CHOP_NEAREST_TREE);
                int amount = Integer.parseInt(args[1]);
                player.sendMessage("Adding " + amount + " bots.");
                for (int i = 0; i < amount; i++) {
                    int x = 3085 + Misc.random(0, 25);
                    int y = 3530 + Misc.random(0, 25);
                    Player.createBot(randomBotName(), Right.PLAYER, new Position(x, y));
                }
                break;
            case "talk":
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        getBots().forEach(it -> it.forcedChat(Captcha.generateCaptchaString()));
                    }
                }, 1);
                break;
            default:
                player.sendMessage(String.format("No actionable command with '%s'", args[0]));
        }
    }
    private void spawnBots(Player player, int amount, BotBehaviour.Type type) {
        spawnBots(player, amount, type, null);
    }
    private void spawnBots(Player player, int amount, BotBehaviour.Type type, String setupName) {
        player.sendMessage("Spawning " + amount + " bots.");
        for (int i = 0; i < amount; i++) {
            int x = player.getX() + Misc.random(-2, 2);
            int y = player.getY() + Misc.random(-2, 2);
            Player bot = Player.createBot(randomBotName(), Right.PLAYER, new Position(x, y));
            bot.addQueuedLoginAction(Bots::randomizeStats);
            if (setupName != null) {
                String name = setupName;
                bot.addQueuedLoginAction(plr -> equipSetup(plr, name));
            } else {
                bot.addQueuedLoginAction(Bots::equipRandomSetup);
            }
            if (type != null) {
                BotBehaviour.Type finalType = type;
                bot.addQueuedLoginAction(plr -> {
                    plr.addTickable(new BotBehaviour(finalType));
                    plr.getAttributes().setInt("bot_behavior", finalType.ordinal());
                });
            }
        }
    }
    private static void equipSetup(Player bot, String name) {
        try {
            EquipmentSetup.equip(bot, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void joinOutlastBots() {
        TourneyManager tourney = TourneyManager.getSingleton();
        if (!tourney.isLobbyOpen())
            return;
        PlayerHandler.nonNullStream().filter(Player::isBot).forEach(bot -> {
            if (bot.getAttributes().getInt("bot_behavior", -1) == BotBehaviour.Type.PK_NEAREST_PLAYER.ordinal()) {
                tourney.join(bot);
            }
        });
    }

    private static void randomizeStats(Player bot) {
        for (int i = 0; i < bot.playerLevel.length; i++) {
            int level = Misc.random(60, 91);
            bot.playerLevel[i] = level;
            bot.playerXP[i] = bot.getPA().getXPForLevel(level) + 1;
            bot.getPA().setSkillLevel(i, bot.playerLevel[i], bot.playerXP[i]);
        }
        bot.getPA().refreshSkills();
    }

    private static void equipRandomSetup(Player bot) {
        List<String> setups = EquipmentSetup.listSetups().stream()
                .map(s -> s.split(" \\(")[0])
                .collect(Collectors.toList());
        if (setups.isEmpty()) {
            return;
        }
        String setup = setups.get(Misc.random(setups.size() - 1));
        try {
            EquipmentSetup.equip(bot, setup);
            bot.getPA().removeAllItems();
            bot.playerLevel[Player.playerPrayer] = 2_000_000_000;
            bot.getHealth().setMaximumHealth(bot.getLevelForXP(bot.playerXP[Player.playerHitpoints]));
            bot.getHealth().reset();
// Tab 2 items
            bot.getItems().addItemUnderAnyCircumstance(Items.WRATH_RUNE, 100);
// Tab 3 items
            int[][] tab3Items = {
                    {Items.HAMMER, 1},
                    {Items.TINDERBOX, 1},
                    {Items.KNIFE, 1},
                    {Items.CHEFS_HAT, 1},
                    {Items.CHISEL, 1},
                    {Items.IRON_PICKAXE, 1},
                    {Items.SMALL_FISHING_NET, 1},
                    {Items.FISHING_BAIT, 100},
                    {Items.FISHING_ROD, 1},
                    {Items.FLY_FISHING_ROD, 1},
                    {Items.FEATHER, 1000},
                    {Items.IRON_AXE, 1},
                    {Items.VIAL_OF_WATER, 1000},
                    {Items.PESTLE_AND_MORTAR, 1},
                    {Items.EYE_OF_NEWT, 100},
                    {Items.HAMMER, 1},
                    {Items.SPADE, 1}
            };

            for (int[] item : tab3Items) {
                bot.getItems().addItemUnderAnyCircumstance(item[0], item[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @NotNull
    private List<Player> getBots() {
        return PlayerHandler.nonNullStream().filter(Player::isBot).collect(Collectors.toList());
    }

    public Optional<String> getDescription() {
        return Optional.of("functions for bot players, ::bots spawn 10 to spawn.");
    }
}
