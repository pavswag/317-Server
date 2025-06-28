package io.xeros.model.entity.npc.drops;

import io.xeros.Server;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.perky.Perks;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import lombok.Getter;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

    @Getter
    private final List<Integer> npcIds;

    /**
     * Creates a new group of tables
     */
    public TableGroup(List<Integer> npcsIds) {
        this.npcIds = npcsIds;
    }

    /**
     * Accesses each {@link Table} in this {@link TableGroup} with hopes of retrieving a {@link List} of {@link GameItem} objects.
     *
     * @return
     */

    private static final Random generate = new Random();

    public List<GameItem> access(Player player, NPC npc, double modifier, int amountOfRolls, int npcId) {
        List<GameItem> items = new ArrayList<>();
        for (var table : this) {
            TablePolicy policy = table.getPolicy();

            if (npc instanceof Nightmare nightmare) {
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                } else if (!nightmare.getRareRollPlayers().contains(player) && (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE || policy == TablePolicy.EXTREMELY_RARE)) {
                    continue;
                }
            }
            if (policy.equals(TablePolicy.CONSTANT)) {
                for (Drop drop : table) {
                    int minimumAmount = drop.getMinimumAmount();

                    items.add(new GameItem(drop.getItemId(), minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount)));
                }
            } else {
                for (int i = 0; i < amountOfRolls; i++) {
                    int rate = table.getAccessibility();
                    double modified = rate * modifier;
                    modified = Math.max(modified, 1);
                    int roll = (int) Math.ceil(modified);
                    int chance = rate - roll;
                    chance = Math.max(chance, 1);
                    if (generate.nextInt(chance) == 0) {
                        var drop = table.fetchRandom();
                        var finalAmount = Server.random.inclusive(drop.getMinimumAmount(), drop.getMaximumAmount());
                        System.out.println("amount=" + finalAmount + " maxamount=" + drop.getMaximumAmount());
                        var rareTableBroadcast = policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE) || policy.equals(TablePolicy.EXTREMELY_RARE);
                        var item = new GameItem(drop.getItemId(), finalAmount);
                        var itemNameLowerCase = ItemDef.forId(item.getId()).getName().toLowerCase();

                        if ((player.getItems().playerHasItem(33159) && item.getId() == 10501)
                                || (player.hasFollower && (player.petSummonId == 33159) && item.getId() == 10501))
                            item.incrementAmount(50);

                        if (item.getId() == 33169 || item.getId() == 33163)
                            player.getCollectionLog().handleDrop(player, 10, item.getId(), item.getAmount());

                        // Rare drop announcements
                        for (int i1 = 0; i1 < Perks.values().length; i1++) {
                            if (item.getId() == Perks.values()[i1].itemID) {
                                NPCDeath.announce(player, item, npcId);
                                rareTableBroadcast = false;
                                break;
                            }
                        }
                        items.add(item);
                        // Any item names here will always announce when dropped
                        if (rareTableBroadcast) {
                            player.getCollectionLog().handleDrop(player, npcId, item.getId(), item.getAmount());
                            // Any item names here will never announce
                            if (!nameList.contains(itemNameLowerCase.toLowerCase())
                                    || item.getId() <= 23490 && item.getId() >= 23491
                                    || item.getId() <= 23083 && item.getId() >= 23084
                                    || item.getId() == 26358
                                    || item.getId() == 26360
                                    || item.getId() == 26362
                                    || item.getId() == 26364)
                                NPCDeath.announce(player, item, npcId);
                        }
                        break;
                    }
                }
            }
        }
        return items;
    }

    private final List<String> nameList = Arrays.asList(
            "archer ring",
            "vasa minirio",
            "hydra",
            "skeletal visage",
            "feather",
            "dharok",
            "logs",
            "guthan",
            "bronze",
            "karil",
            "ahrim",
            "verac",
            "torag",
            "arrow",
            "shield",
            "staff",
            "iron",
            "black",
            "steel",
            "rune warhammer",
            "rock-shell",
            "eye of newt",
            "silver ore",
            "spined",
            "wine of zamorak",
            "rune spear",
            "grimy",
            "skeletal",
            "jangerberries",
            "goat horn dust",
            "yew roots",
            "white berries",
            "bars",
            "blue dragonscales",
            "kebab",
            "potato",
            "shark",
            "red",
            "spined body",
            "prayer",
            "anchovy",
            "runite",
            "adamant",
            "magic roots",
            "earth battlestaff",
            "torstol",
            "dragon battle axe",
            "helm of neitiznot",
            "mithril",
            "sapphire",
            "rune",
            "toktz",
            "steal",
            "seed",
            "ancient",
            "monk",
            "splitbark",
            "pure",
            "zamorak robe",
            "null",
            "essence",
            "crushed",
            "snape",
            "unicorn",
            "mystic",
            "eye patch",
            "steel darts",
            "steel bar",
            "limp",
            "darts",
            "dragon longsword",
            "dust battlestaff",
            "granite",
            "coal",
            "crystalline key",
            "leaf-bladed sword",
            "dragon plateskirt",
            "dragon platelegs",
            "dragon scimitar",
            "abyssal head",
            "cockatrice head",
            "dragon chainbody",
            "dragon battleaxe",
            "dragon boots",
            "overload",
            "bones",
            "granite shield",
            "granite body",
            "granite helm",
            "greanite legs",
            "barrlchest anchor",
            "rune med helm",
            "dragon med helm",
            "red spiders' eggs",
            "rune battleaxe",
            "granite maul",
            "casket",
            "ballista limbs",
            "ballista spring",
            "light frame",
            "heavy frame",
            "monkey tail",
            "shield left half",
            "clue scroll (master)",
            "dragon axe",
            "the unbearable's key",
            "corrupted ork's key",
            "mystic steam staff",
            "dragon spear",
            "ancient staff",
            "mysterious emblem",
            "ancient emblem",
            "pkp ticket",
            "crystal body",
            "crystal helm",
            "crystal legs",
            "dharok's helm",
            "dharok's greataxe",
            "dharok's platebody",
            "dharok's platelegs",
            "verac's flail",
            "verac's helm",
            "verac's brassard",
            "verac's plateskirt",
            "guthan's warspear",
            "guthan's helm",
            "guthan's platebody",
            "guthan's chainskirt",
            "ahrim's hood",
            "ahrim's staff",
            "ahrim's robetop",
            "ahrim's robeskirt",
            "karil's coif",
            "karil's crossbow",
            "karil's leathertop",
            "karil's leatherskirt",
            "torag's hammers",
            "torag's helm",
            "torag's platebody",
            "torag's platelegs",
            "rune boots",
            "rune longsword",
            "rune platebody",
            "adamant platelegs",
            "dragon mace",
            "dragon dagger",
            "mystic robe top",
            "rune chainbody",
            "rune pickaxe",
            "grimy dwarf weed",
            "brine sabre",
            "godsword shard 1",
            "godsword shard 2",
            "godsword shard 3",
            "poison ivy seed",
            "cactus seed",
            "avantoe seed",
            "kwuarm seed",
            "snapdragon seed",
            "cadantine seed",
            "lantadyme seed",
            "dwarf weed seed",
            "coins",
            "pure essence",
            "dragon bones",
            "magic logs",
            "runite ore",
            "runite bar",
            "divine super combat potion(4)",
            "lava dragon bones",
            "saradomin brew(4)",
            "bloodier key",
            "mystery box",
            "10,000 nomad point certificate",
            "amulet of the damned"
    );

    /**
     * The non-playable character identification values that have access to this group of tables.
     *
     * @return the non-playable character id values
     */
    public List<Integer> getNpcIds() {
        return npcIds;
    }
}
