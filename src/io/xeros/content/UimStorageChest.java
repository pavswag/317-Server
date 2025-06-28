package io.xeros.content;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.skills.farming.Plants;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.world.ShopHandler;
import io.xeros.util.Misc;

import java.util.HashMap;

public class UimStorageChest {

    public static void main(String[] args) throws Exception {
        ItemDef.load();

        for (int i = 0; i < 34000; i++) {
            if (isStorageItem(null, i)) {
                System.out.println(ItemDef.forId(i).getName());
            }
        }
    }

    public static boolean isStorageItem(Player c, int itemId) {
        for (Plants value : Plants.values()) {
            if (value.seed == itemId || value.harvest == itemId) {
                return true;
            }
        }

        if (c.getAncientCasket().getLoot().get(LootRarity.RARE).contains(new GameItem(itemId)) ||
                c.getAncientCasket().getLoot().get(LootRarity.UNCOMMON).contains(new GameItem(itemId)) ||
                c.getAncientCasket().getLoot().get(LootRarity.COMMON).contains(new GameItem(itemId))) {
            return true;
        }

        for (PetHandler.Pets value : PetHandler.Pets.values()) {
            if (value.getItemId() == itemId) {
                return true;
            }
        }

        for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
            if (itemId == (ShopHandler.ShopItems[196][i] - 1)) {
                return true;
            }
        }

        return switch (itemId) {
            case 27473, 27475, 27477, 27479, 27481, 25898, 25900, 25904, 25906, 25912, 25910, 24364, 24365, 24366,
                 27497, 27499, 27501, 27503, 27505, 27507, 26258, 26256, 20439, 20436, 20442, 20433, 10558, 19942,
                 25748, 10591, 21262, 8132, 25750, 25749, 25752, 10533, 25350, 33208, 23495, 26348, 25348, 25592, 33073,
                 33074, 33090, 33091, 33102, 33103, 33104, 33105, 33106, 33107, 33108, 33117, 33118, 33119, 33121,
                 33079, 33080, 33087, 33088, 33089, 33093, 33094, 33095, 33096, 33097, 33098, 33099, 33100, 33101,
                 33122, 33109, 33110, 33111, 33112, 33113, 33077, 33078, 33072, 33075, 33076, 33081, 33082, 33083,
                 33084, 33085, 33086, 33092, 33114, 33115, 33116, 33120, 33123, 33124, 25594, 25596, 25598, 25549,
                 20366, 22249, 23444, 25551, 25553, 25555, 26858, 26860, 26862, 26899, 33159, 20370, 20374, 20372,
                 20368, 27275, 33205, 33058, 28919, 28922, 27235, 27238, 27241, 33141, 33142, 33143, 33202, 33204,
                 28688, 10559, 10556, 26914, 33160, 33161, 33162, 25739, 25736, 26708, 24664, 24666, 24668, 25918,
                 26482, 26484, 25734, 26486, 33184, 33203, 33206, 26269, 27428, 27430, 27432, 27434, 27436, 27438,
                 33149, 33189, 33190, 33191, 27253, 33183, 33186, 33187, 33188, 12899, 12900, 26235, 12892, 12893,
                 12894, 12895, 12896, Items.GRACEFUL_BOOTS, Items.GRACEFUL_CAPE, Items.GRACEFUL_GLOVES,
                 Items.GRACEFUL_HOOD, Items.GRACEFUL_LEGS, Items.GRACEFUL_TOP, Items.PRIMORDIAL_BOOTS,
                 Items.PEGASIAN_BOOTS, Items.ETERNAL_BOOTS, Items.AGILITY_CAPET, Items.CONSTRUCT_CAPET,
                 Items.COOKING_CAPET, Items.ATTACK_CAPET, Items.CRAFTING_CAPET, Items.DEFENCE_CAPET,
                 Items.FARMING_CAPET, Items.FIREMAKING_CAPET, Items.FISHING_CAPET, Items.FLETCHING_CAPET,
                 Items.HERBLORE_CAPET, Items.HITPOINTS_CAPET, Items.HUNTER_CAPET, Items.MAGIC_CAPET, Items.MINING_CAPET,
                 Items.MUSIC_CAPET, Items.PRAYER_CAPET, Items.RANGING_CAPET, Items.RUNECRAFT_CAPET, Items.SLAYER_CAPET,
                 Items.SMITHING_CAPET, Items.STRENGTH_CAPET, Items.WOODCUT_CAPET, Items.THIEVING_CAPET, 23859,
                 Items.COMPLETIONIST_CAPE, Items.MAX_CAPE, Items.MAX_HOOD, Items.ACCUMULATOR_MAX_CAPE,
                 Items.ACCUMULATOR_MAX_HOOD, Items.ARDOUGNE_MAX_CAPE, Items.ARDOUGNE_MAX_HOOD, Items.FIRE_MAX_CAPE,
                 Items.FIRE_MAX_HOOD, Items.INFERNAL_MAX_CAPE, Items.INFERNAL_MAX_HOOD, Items.GUTHIX_MAX_CAPE,
                 Items.GUTHIX_MAX_HOOD, Items.SARADOMIN_MAX_CAPE, Items.SARADOMIN_MAX_HOOD, Items.ZAMORAK_MAX_CAPE,
                 Items.ZAMORAK_MAX_HOOD, Items.IMBUED_GUTHIX_MAX_HOOD, Items.IMBUED_GUTHIX_MAX_CAPE,
                 Items.IMBUED_SARADOMIN_MAX_CAPE, Items.IMBUED_SARADOMIN_MAX_HOOD, Items.IMBUED_ZAMORAK_MAX_CAPE,
                 Items.IMBUED_ZAMORAK_MAX_HOOD, Items.ASSEMBLER_MAX_CAPE, Items.ASSEMBLER_MAX_HOOD, Items.SLAYER_HELMET,
                 Items.SLAYER_HELMET_I, Items.BLACK_SLAYER_HELMET_I, Items.BLACK_SLAYER_HELMET,
                 Items.GREEN_SLAYER_HELMET, Items.GREEN_SLAYER_HELMET_I, Items.RED_SLAYER_HELMET_I,
                 Items.RED_SLAYER_HELMET, Items.PURPLE_SLAYER_HELMET, Items.PURPLE_SLAYER_HELMET_I,
                 Items.TURQUOISE_SLAYER_HELMET, Items.TURQUOISE_SLAYER_HELMET_I, Items.HYDRA_SLAYER_HELMET,
                 Items.HYDRA_SLAYER_HELMET_I, Items.TWISTED_SLAYER_HELMET, Items.TWISTED_SLAYER_HELMET_I,
                 Items.MAGIC_SECATEURS, Items.MAGIC_SECATEURS_NZ, Items.CANNON_BASE, Items.CANNON_BARRELS,
                 Items.CANNON_FURNACE, Items.CANNON_STAND, 26520, 26522, 26524, 26526, Items.HERB_SACK,
                 Items.BARROWS_GLOVES, Items.RUNE_GLOVES, Items.MITHRIL_GLOVES, 33125, 33126, 33127, 33128, 33129,
                 33130, 33131, 33132, 33133, 33134, 33135, 33136, 33137, 33138, 33139, 33140, Items.RANGER_BOOTS, 12954,
                 26467, 26469, 26471, 26473, 26475, 26477, Items.AVAS_ACCUMULATOR, Items.AVAS_ATTRACTOR,
                 Items.IMBUED_SARADOMIN_CAPE, Items.IMBUED_GUTHIX_CAPE, Items.IMBUED_ZAMORAK_CAPE,
                 Items.AMULET_OF_FURY_OR, Items.FLIPPERS, 33033, 33034, 33035, 33036, 33037, 33038, 33039, 33040, 33041,
                 33042, 33043, 33044, 33045, 33046, 33047, 33048, 33049, 33050, 33051, 33052, 33053, 33054, 33055,
                 23911, 23913, 23915, 23917, 23919, 23921, 23923, 23925, 33059, 33060, 33061, 33062, 33063, 33064,
                 26498, 26496, 26492, 26494, 26490, 26488, Items.UNHOLY_BOOK, Items.HOLY_BOOK, Items.BOOK_OF_BALANCE,
                 Items.BOOK_OF_WAR, Items.BOOK_OF_DARKNESS, Items.BOOK_OF_LAW, Items.CRUCIFEROUS_CODEX,
                 Items.RING_OF_THIRD_AGE, Items.VOID_KNIGHT_GLOVES, Items.VOID_KNIGHT_MACE, Items.VOID_KNIGHT_ROBE,
                 Items.VOID_KNIGHT_TOP, Items.VOID_MAGE_HELM, Items.VOID_MELEE_HELM, Items.VOID_RANGER_HELM,
                 Items.ELITE_VOID_ROBE, Items.ELITE_VOID_TOP, 24182, 24183, 24184, 24185, 26465, 26463,
                 Items.THIRD_AGE_AXE, Items.THIRD_AGE_BOW, Items.THIRD_AGE_DRUIDIC_STAFF, Items.THIRD_AGE_LONGSWORD,
                 Items.THIRD_AGE_PICKAXE, Items.THIRD_AGE_AMULET, Items.THIRD_AGE_DRUIDIC_CLOAK,
                 Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS, Items.THIRD_AGE_DRUIDIC_ROBE_TOP, Items.THIRD_AGE_PLATESKIRT,
                 Items.THIRD_AGE_RANGE_TOP, Items.THIRD_AGE_RANGE_LEGS, Items.THIRD_AGE_RANGE_COIF,
                 Items.THIRD_AGE_VAMBRACES, Items.THIRD_AGE_ROBE_TOP, Items.THIRD_AGE_ROBE, Items.THIRD_AGE_MAGE_HAT,
                 Items.THIRD_AGE_PLATELEGS, Items.THIRD_AGE_PLATEBODY, Items.THIRD_AGE_FULL_HELMET,
                 Items.THIRD_AGE_KITESHIELD, Items.THIRD_AGE_WAND, Items.THIRD_AGE_CLOAK, 12006, 22109, 10476, 12247,
                 12253, 12255, 12257, 12259, 12261, 12263, 12265, 12267, 12269, 12271, 12273, 12275, 3827, 3828, 3829,
                 3830, 3831, 3832, 3833, 3834, 3835, 3836, 3837, 3838, 10368, 10370, 10372, 10374, 10376, 10378, 10380,
                 10382, 10384, 10386, 10388, 10390, 10400, 10402, 10416, 10418, 10420, 10422, 10436, 10438, 10440,
                 10442, 10444, 10446, 10448, 10450, 10452, 10454, 10456, 10470, 10472, 10474, 12441, 12443, 12514,
                 12516, 12596, 12598, 19687, 19689, 19691, 19693, 19695, 19697, 19921, 19924, 19927, 19930, 19933,
                 19936, 19941, 19958, 19961, 19964, 19967, 19973, 19976, 19979, 19982, 19988, 19991, 20005, 20017,
                 20020, 20023, 20026, 20029, 20032, 20035, 20038, 20041, 20044, 20047, 20059, 20110, 20143, 20164,
                 20199, 20202, 20205, 20208, 20211, 20214, 20217, 20220, 20223, 20226, 20229, 20232, 20235, 20240,
                 20243, 20246, 20249, 20266, 20269, 20272, 2579, 2581, 2583, 2585, 2587, 2589, 2591, 2593, 2595, 2597,
                 2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, 2631,
                 2633, 2635, 2637, 2639, 2641, 2643, 2645, 2647, 2649, 2651, 2653, 2655, 2657, 2659, 2661, 2663, 2665,
                 2667, 2669, 2671, 2673, 2675, 3481, 3483, 3485, 3486, 3488, 6889, 7319, 7321, 7323, 7325, 7327, 7332,
                 7334, 7338, 7340, 7342, 7346, 7348, 7350, 7352, 7356, 7358, 7362, 7364, 7366, 7368, 7370, 7372, 7374,
                 7376, 7378, 7380, 7382, 7384, 7386, 7388, 7390, 7392, 7394, 7396, 7398, 7399, 7400, 1617, 1618, 1619,
                 1620, 1621, 1622, 1623, 1624, 1625, 1626, 1627, 1628, 1629, 1630, 1631, 1632, 6571, 6572, 2481, 2482,
                 2485, 2486, 19496, 19497, 199, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214,
                 215, 216, 217, 218, 219, 220, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262,
                 263, 264, 265, 266, 267, 268, 269, 3000, 3001, 2998, 2999, 3049, 3050, 3051, 3052, Items.ROGUE_BOOTS,
                 Items.ROGUE_GLOVES, Items.ROGUE_MASK, Items.ROGUE_TOP, Items.ROGUE_TROUSERS, Items.PROSELYTE_CUISSE,
                 Items.PROSELYTE_HAUBERK, Items.PROSELYTE_TASSET, Items.PROSELYTE_SALLET, Items.MOURNER_BOOTS,
                 Items.MOURNER_CLOAK, Items.MOURNER_GLOVES, Items.MOURNER_TOP, Items.MOURNER_TROUSERS, Items.GAS_MASK,
                 Items.LUMBERJACK_BOOTS, Items.LUMBERJACK_HAT, Items.LUMBERJACK_LEGS, Items.LUMBERJACK_TOP,
                 Items.PROSPECTOR_BOOTS, Items.PROSPECTOR_HELMET, Items.PROSPECTOR_JACKET, Items.PROSPECTOR_LEGS,
                 Items.ANGLER_BOOTS, Items.ANGLER_HAT, Items.ANGLER_TOP, Items.ANGLER_WADERS, Items.SHAYZIEN_GLOVES_5,
                 Items.SHAYZIEN_BOOTS_5, Items.SHAYZIEN_GREAVES_5, Items.SHAYZIEN_HELM_5, Items.SHAYZIEN_PLATEBODY_5,
                 Items.FARMERS_BOOTS, Items.FARMERS_BORO_TROUSERS, Items.FARMERS_FORK, Items.FARMERS_JACKET,
                 Items.FARMERS_SHIRT, Items.FARMERS_STRAWHAT, Items.FARMERS_STRAWHAT_2, Items.FARMERS_BORO_TROUSERS_2,
                 Items.OBSIDIAN_PLATEBODY, Items.OBSIDIAN_PLATELEGS, Items.OBSIDIAN_HELMET, Items.FIGHTER_TORSO,
                 Items.JUSTICIAR_CHESTGUARD, Items.JUSTICIAR_FACEGUARD, Items.JUSTICIAR_LEGGUARDS,
                 Items.INQUISITORS_GREAT_HELM, Items.INQUISITORS_PLATESKIRT, Items.INQUISITORS_HAUBERK,
                 Items.PYROMANCER_BOOTS, Items.PYROMANCER_GARB, Items.PYROMANCER_HOOD, Items.PYROMANCER_ROBE,
                 Items.MYSTIC_BOOTS, Items.MYSTIC_BOOTS_DARK, Items.MYSTIC_BOOTS_DUSK, Items.MYSTIC_BOOTS_LIGHT,
                 Items.MYSTIC_GLOVES, Items.MYSTIC_GLOVES_DARK, Items.MYSTIC_GLOVES_DUSK, Items.MYSTIC_GLOVES_LIGHT,
                 Items.MYSTIC_ROBE_TOP, Items.MYSTIC_ROBE_TOP_DUSK, Items.MYSTIC_ROBE_TOP_DARK,
                 Items.MYSTIC_ROBE_TOP_LIGHT, Items.MYSTIC_HAT, Items.MYSTIC_HAT_DARK, Items.MYSTIC_HAT_DUSK,
                 Items.MYSTIC_HAT_LIGHT, Items.MYSTIC_ROBE_BOTTOM, Items.MYSTIC_ROBE_BOTTOM_DARK,
                 Items.MYSTIC_ROBE_BOTTOM_DUSK, Items.MYSTIC_ROBE_BOTTOM_LIGHT, Items.INFINITY_BOOTS,
                 Items.INFINITY_BOTTOMS, Items.INFINITY_GLOVES, Items.INFINITY_HAT, Items.INFINITY_TOP,
                 Items.DARK_INFINITY_BOTTOMS, Items.DARK_INFINITY_HAT, Items.DARK_INFINITY_TOP, Items.ANCESTRAL_HAT,
                 Items.ANCESTRAL_ROBE_TOP, Items.ANCESTRAL_ROBE_BOTTOM, 30010, 30012, 30011, 30013, 30014, 30015, 30016,
                 30017, 30018, 30019, 30020, 30021, 30022, 23939, 30110, 30112, 30111, 30113, 30114, 30115, 30116,
                 30117, 30118, 30119, 30120, 30121, 30122, 30123, 13320, 13321, 21187, 21188, 21189, 21192, 21193,
                 21194, 21196, 21197, 13322, 13323, 13324, 13325, 13326, 20659, 20661, 20663, 20665, 20667, 20669,
                 20671, 20673, 20675, 20677, 20679, 20681, 20683, 20685, 20687, 20689, 20691, 20693, 19557, 12650,
                 12649, 12651, 12652, 12644, 12645, 12643, 11995, 12653, 12655, 13178, 12646, 13179, 13180, 13177,
                 12648, 13225, 13247, 21273, 12921, 12939, 12940, 21992, 13181, 12816, 12654, 22318, 12647, 13262,
                 19730, 22376, 22378, 22380, 22382, 22384, 20851, 22473, 21291, 22319, 22746, 22748, 22750, 22752,
                 23760, 23757, 23759, 24491, 7629, 21046 -> true;
            default ->
//                c.sendMessage("@red@Your game mode cannot store: @blu@" + ItemAssistant.getItemName(itemId));
                    false;
        };
    }
}
