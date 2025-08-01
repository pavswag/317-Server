package io.xeros.content.combat.magic;

import com.google.common.collect.Lists;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;

import java.util.List;

public class CombatSpellData {

	public static final int AUTOCAST_CONFIG = 1369;
	public static final int AUTOCAST_DEFENCE_CONFIG = 1370;

	public static final int TELEBLOCK = 12445;

	public static final int[] ANCIENT_AUTOCAST_STAFFS = {
			4675, 6914, 21006, 4710, 11907, 12904, 27624,
			22296, 24422, 24424, 24425, 8841, 33149, 21198, 33433,33205, 27679, 22335
	};

	public static final int[] DISABLE_AUTOCAST_STAFFS = {
			Items.SANGUINESTI_STAFF, 25731, 24144
	};

	public static boolean isNatureSpell(int spell) {
		List<Integer> runes = Lists.newArrayList();
		for (int i = 8; i < 13; i+= 2)
			runes.add(MAGIC_SPELLS[spell][i]);
		return runes.stream().anyMatch(x -> x.intValue() == 561);
	}

	public static boolean isBoltSpell(int spell) {
		switch (spell) {
			case 1160:
			case 1163:
			case 1166:
			case 1169:
				return true;
			default:
				return false;
		}
	}
	
	public static final int[] MULTI_SPELLS = {
			12963, // Smoke burst
			13011, // Shadow burst
			12919, // Blood burst
			12881, // Ice burst
			12975, // Smoke barrage
			13023, // Shadow barrage
			12929, // Blood barrage
			12891, // Ice barrage
	};

	/**
	 * Contains data of every magic spell and requirements
	 * 
	 * Spell id - Level req - Animation - Start GFX - Projectile GFX 
	 * End GFX - Max hit - Experience - rune - amount
	 * rune - amount - rune - amount - rune - amount
	 */
	public final static int[][] MAGIC_SPELLS = {

		//0 = id
		//1 = level
		//2 = animation
		//3 = start gfx
		//4 = projectile
		//5 = end gfx
		//6 = base damage
		//7 = experience gained
		//8 = rune1
		//9 = rune1 amount
		//10 = rune2
		//11 = rune2 amount
		//12 = rune3
		//13 = rune3 amount

		// Modern Spells
		{ 1152, 1, 711, 90, 91, 92, 2, 5, 556, 1, 558, 1, 0, 0, 0, 0 }, // wind strike
		{ 1154, 5, 711, 93, 94, 95, 4, 7, 555, 1, 556, 1, 558, 1, 0, 0 }, // water strike
		{ 1156, 9, 711, 96, 97, 98, 6, 9, 557, 2, 556, 1, 558, 1, 0, 0 }, // earth strike
		{ 1158, 13, 711, 99, 100, 101, 8, 11, 554, 3, 556, 2, 558, 1, 0, 0 }, // fire strike
		{ 1160, 17, 711, 117, 118, 119, 9, 13, 556, 2, 562, 1, 0, 0, 0, 0 }, // wind bolt
		{ 1163, 23, 711, 120, 121, 122, 10, 16, 556, 2, 555, 2, 562, 1, 0, 0 }, // water bolt
		{ 1166, 29, 711, 123, 124, 125, 11, 20, 556, 2, 557, 3, 562, 1, 0, 0 }, // earth bolt
		{ 1169, 35, 711, 126, 127, 128, 12, 22, 556, 3, 554, 4, 562, 1, 0, 0 }, // fire bolt
		{ 1172, 41, 711, 132, 133, 134, 13, 25, 556, 3, 560, 1, 0, 0, 0, 0 }, // wind blast
		{ 1175, 47, 711, 135, 136, 137, 14, 28, 556, 3, 555, 3, 560, 1, 0, 0 }, // water blast 
		{ 1177, 53, 711, 138, 139, 140, 15, 31, 556, 3, 557, 4, 560, 1, 0, 0 }, // earth blast 10
		{ 1181, 59, 711, 129, 130, 131, 16, 35, 556, 4, 554, 5, 560, 1, 0, 0 }, // fire blast
		{ 1183, 62, 711, 158, 159, 160, 17, 36, 556, 5, 565, 1, 0, 0, 0, 0 }, // wind wave
		{ 1185, 65, 711, 161, 162, 163, 18, 37, 556, 5, 555, 7, 565, 1, 0, 0 }, // water wave
		{ 1188, 70, 711, 164, 165, 166, 19, 40, 556, 5, 557, 7, 565, 1, 0, 0 }, // earth wave
		{ 1189, 75, 711, 155, 156, 157, 20, 42, 556, 5, 554, 7, 565, 1, 0, 0 }, // fire wave
		{ 1153, 3, 716, 102, 103, 104, 0, 13, 555, 3, 557, 2, 559, 1, 0, 0 }, // confuse
		{ 1157, 11, 716, 105, 106, 107, 0, 20, 555, 3, 557, 2, 559, 1, 0, 0 }, // weaken
		{ 1161, 19, 716, 108, 109, 110, 0, 29, 555, 2, 557, 3, 559, 1, 0, 0 }, // curse
		{ 1542, 66, 729, 167, 168, 169, 0, 76, 557, 5, 555, 5, 566, 1, 0, 0 }, // vulnerability 
		{ 1543, 73, 729, 170, 171, 172, 0, 83, 557, 8, 555, 8, 566, 1, 0, 0 }, // enfeeble 20
		{ 1562, 80, 729, 173, 174, 107, 0, 90, 557, 12, 555, 12, 556, 1, 0, 0 }, // stun
		{ 1572, 20, 711, 177, 178, 181, 1, 30, 557, 3, 555, 3, 561, 2, 0, 0 }, // bind
		{ 1582, 50, 711, 177, 178, 180, 2, 60, 557, 4, 555, 4, 561, 3, 0, 0 }, // snare
		{ 1592, 79, 711, 177, 178, 179, 2, 90, 557, 5, 555, 5, 561, 4, 0, 0 }, // entangle
		{ 1171, 39, 724, 145, 146, 147, 15, 25, 556, 2, 557, 2, 562, 1, 0, 0 }, // crumble undead
		{ 1539, 50, 708, 87, 88, 89, 25, 42, 554, 5, 560, 1, 0, 0, 0, 0 }, // iban blast
		{ 12037, 50, 1576, 327, 328, 329, 19, 30, 560, 1, 558, 4, 0, 0, 0, 0 }, // magic dart
		{ 1190, 60, 811, 0, 0, 76, 20, 60, 554, 2, 565, 2, 556, 4, 0, 0 }, // sara strike
		{ 1191, 60, 811, 0, 0, 77, 20, 60, 554, 1, 565, 2, 556, 4, 0, 0 }, // claws of guthix 
		{ 1192, 60, 811, 0, 0, 78, 20, 60, 554, 4, 565, 2, 556, 1, 0, 0 }, // flames of zamorak 30
		{ TELEBLOCK, 85, 1819, 0, 1299, 345, 0, 65, 563, 1, 562, 1, 560, 1, 0, 0 }, // teleblock
		
		//{ 22644 + 975, 81, 7855, 1455, 1456, 1457, 30, 130, 556, 7, 21880, 1, 0, 0, 0, 0 }, // wind surge
		//{ 22658 + 975, 85, 7855, 1458, 1459, 1460, 33, 130, 556, 7, 21880, 1, 555, 10, 0, 0 }, // water surge
		//{ 22628 + 975, 90, 7855, 1461, 1462, 1463, 35, 130, 556, 7, 21880, 1, 557, 10, 0, 0 }, // earth surge
		//{ 22608 + 975, 95, 7855, 1464, 1465, 1466, 38, 130, 556, 7, 21880, 1, 554, 10, 0, 0 }, // fire surge

		// Ancient Spells
		{ 12939, 50, 1978, 0, 384, 385, 13, 30, 560, 2, 562, 2, 554, 1, 556, 1 }, // smoke rush
		{ 12987, 52, 1978, 0, 378, 379, 14, 31, 560, 2, 562, 2, 566, 1, 556, 1 }, // shadow rush
		{ 12901, 56, 1978, 0, 0, 373, 15, 33, 560, 2, 562, 2, 565, 1, 0, 0 }, // blood rush
		{ 12861, 58, 1978, 0, 360, 361, 16, 34, 560, 2, 562, 2, 555, 2, 0, 0 }, // ice rush
		{ 12963, 62, 1979, 0, 0, 389, 19, 36, 560, 2, 562, 4, 556, 2, 554, 2 }, // smoke burst
		{ 13011, 64, 1979, 0, 0, 382, 20, 37, 560, 2, 562, 4, 556, 2, 566, 2 }, // shadow burst
		{ 12919, 68, 1979, 0, 0, 376, 21, 39, 560, 2, 562, 4, 565, 2, 0, 0 }, // blood burst
		{ 12881, 70, 1979, 0, 0, 363, 22, 40, 560, 2, 562, 4, 555, 4, 0, 0 }, // ice burst 
		{ 12951, 74, 1978, 0, 386, 387, 23, 42, 560, 2, 554, 2, 565, 2, 556, 2 }, // smoke blitz 40
		{ 12999, 76, 1978, 0, 380, 381, 24, 43, 560, 2, 565, 2, 556, 2, 566, 2 }, // shadow blitz
		{ 12911, 80, 1978, 0, 374, 375, 25, 45, 560, 2, 565, 4, 0, 0, 0, 0 }, // blood blitz
		{ 12871, 82, 1978, 366, 0, 367, 26, 46, 560, 2, 565, 2, 555, 3, 0, 0 }, // ice blitz
		{ 12975, 86, 1979, 0, 0, 391, 27, 48, 560, 4, 565, 2, 556, 4, 554, 4 }, // smoke barrage
		{ 13023, 88, 1979, 0, 0, 383, 28, 49, 560, 4, 565, 2, 556, 4, 566, 3 }, // shadow barrage
		{ 12929, 92, 1979, 0, 0, 377, 29, 51, 560, 4, 565, 4, 566, 1, 0, 0 }, // blood barrage
		{ 12891, 94, 1979, 0, 0, 369, 30, 52, 560, 4, 565, 2, 555, 6, 0, 0 }, // ice barrage
		
		//On items
		{ -1, 80, 811, 301, 0, 0, 0, 0, 554, 3, 565, 3, 556, 3, 0, 0 }, // charge
		{ -1, 21, 712, 112, 0, 0, 0, 31, 554, 3, 561, 1, 0, 0, 0, 0 }, // low alch
		{-1, 55, 713, 113, 0, 0, 0, 65, 554, 5, 561, 1, 0, 0, 0, 0 }, // high alch 50
		{ -1, 33, 728, 142, 143, 144, 0, 35, 556, 1, 563, 1, 0, 0, 0, 0 }, // telegrab

		//Other
		{ -1, 75, 1167, 1251, 1252, 1253, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, // trident of the seas
		{ -1, 75, 1167, 665, 1040, 1042, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, // trident of the swamp
		
		{ 1173, 43, 725, 148, 0, 0, 0, 0, 554, 4, 561, 1, 0, 0, 0, 0 }, // superheat item

		{ 55, 94, 0, 0, 0, 0, 0, 10000, 9075, 4, 557, 10, 560, 2, 0, 0 }, // Vengeance 55
		
		{ 1155, 7, 0, 0, 0, 0, 0, 0, 555, 1, 564, 1, 0, 0, 0, 0 }, // Lvl-1 Enchant - Sapphire
		{ 1165, 27, 0, 0, 0, 0, 0, 0, 556, 3, 564, 1, 0, 0, 0, 0 }, // Lvl-2 Enchant - Emerals
		{ 1176, 49, 0, 0, 0, 0, 0, 0, 554, 5, 564, 1, 0, 0, 0, 0 }, // Lvl-3 Enchant - Ruby
		{ 1180, 57, 0, 0, 0, 0, 0, 0, 557, 10, 564, 1, 0, 0, 0, 0 }, // Lvl-4 Enchant - Diamond
		{ 1187, 68, 0, 0, 0, 0, 0, 0, 555, 15, 564, 1, 557, 15, 0, 0 }, // Lvl-5 Enchant - Dragonstone
		{ 6003, 87, 0, 0, 0, 0, 0, 0, 554, 20, 564, 1, 557, 20, 0, 0 }, // Lvl-6 Enchant - Onyx - 61

		//City teleports
		{ -1, 25, 0, 0, 0, 0, 0, 0, 554, 0, 556, 3, 563, 1, 0, 0 }, //Varrock - 62
		{ -1, 31, 0, 0, 0, 0, 0, 0, 557, 1, 556, 3, 563, 1, 0, 0 }, //Lumbridge - 63
		{ -1, 37, 0, 0, 0, 0, 0, 0, 555, 1, 556, 3, 563, 1, 0, 0 }, //Falador - 64
		{ -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, //Camelot - 65
		{ -1, 51, 0, 0, 0, 0, 0, 0, 555, 2, 563, 2, 0, 0, 0, 0 }, //Ardougne - 66
		{ -1, 58, 0, 0, 0, 0, 0, 0, 557, 2, 563, 2, 0, 0, 0, 0 }, //Watchtower - 67
		{ -1, 61, 0, 0, 0, 0, 0, 0, 554, 2, 563, 2, 0, 0, 0, 0 }, //Trollheim - 68
		
		{ -1, 54, 0, 0, 0, 0, 0, 0, 563, 2, 554, 1, 556, 1, 0, 0 }, //Paddewwa - 69
		{ -1, 60, 0, 0, 0, 0, 0, 0, 563, 2, 566, 1, 0, 0, 0, 0 }, //Senntisten - 70
		{ -1, 66, 0, 0, 0, 0, 0, 0, 563, 2, 565, 1, 0, 0, 0, 0 }, //Kharyrll - 71
		{ -1, 72, 0, 0, 0, 0, 0, 0, 563, 2, 555, 4, 0, 0, 0, 0 }, //Lassar - 72
		{ -1, 78, 0, 0, 0, 0, 0, 0, 563, 2, 554, 3, 556, 2, 0, 0 }, //Dareeyak - 73
		{ -1, 84, 0, 0, 0, 0, 0, 0, 563, 2, 566, 2, 0, 0, 0, 0 }, //Carrallangar - 74
		{ -1, 90, 0, 0, 0, 0, 0, 0, 563, 2, 565, 2, 0, 0, 0, 0 }, //Annakarl - 75
		{ -1, 96, 0, 0, 0, 0, 0, 0, 563, 2, 555, 8, 0, 0, 0, 0 }, //Ghorrok - 76
		
		{ -1, 69, 0, 0, 0, 0, 0, 0, 9075, 2, 563, 1, 557, 2, 0, 0 }, //Moonclan - 77
		{ -1, 71, 0, 0, 0, 0, 0, 0, 9075, 2, 563, 1, 557, 6, 0, 0 }, //Ourania - 78
		{ -1, 72, 0, 0, 0, 0, 0, 0, 9075, 2, 563, 1, 555, 1, 0, 0 }, //Waterbirth - 79
		{ -1, 75, 0, 0, 0, 0, 0, 0, 9075, 2, 563, 2, 554, 3, 0, 0 }, //Barbarian - 80
		{ -1, 78, 0, 0, 0, 0, 0, 0, 9075, 2, 563, 2, 555, 4, 0, 0 }, //Khazard - 81
		{ -1, 85, 0, 0, 0, 0, 0, 0, 9075, 3, 563, 3, 555, 10, 0, 0 }, //Fishing guild - 82
		{ -1, 87, 0, 0, 0, 0, 0, 0, 9075, 3, 563, 3, 555, 10, 0, 0 }, //Catherby - 83
		{ -1, 89, 0, 0, 0, 0, 0, 0, 9075, 3, 563, 3, 555, 8, 0, 0 }, //Ice plateu - 84
		
		//Lunar spells
		{ 30048, 68, 0, 0, 0, 0, 0, 0, 9075, 1, 563, 1, 557, 10, 0, 0 }, //Cure Other - 85
		{ 30130, 75, 0, 0, 0, 0, 0, 0, 9075, 2, 564, 2, 559, 5, 0, 0 }, //Stat spy - 86
		{ 30282, 91, 0, 0, 0, 0, 0, 0, 9075, 3, 563, 2, 561, 1, 0, 0 }, //Energy Transfer - 87
		{ 30290, 92, 0, 0, 0, 0, 0, 0, 9075, 3, 563, 3, 565, 1, 0, 0 }, //Heal Other - 88
		{ 30298, 93, 0, 0, 0, 0, 0, 0, 9075, 3, 560, 2, 557, 10, 0, 0 }, //Vengeance other - 89
		
		//Orb charging
		{ -1, 56, 0, 0, 0, 0, 0, 0, 555, 30, 564, 3, 0, 0, 0, 0 }, //Water 90
		{ -1, 60, 0, 0, 0, 0, 0, 0, 557, 30, 564, 3, 0, 0, 0, 0 }, //Earth 91
		{ -1, 63, 0, 0, 0, 0, 0, 0, 554, 30, 564, 3, 0, 0, 0, 0 }, //Fire 92
		{ -1, 66, 0, 0, 0, 0, 0, 0, 556, 30, 564, 3, 0, 0, 0, 0 }, //Air 93

		{ 22644 + 975, 81, 7855, 1455, 1456, 1457, 21, 130, 556, 7, 21880, 1, 0, 0, 0, 0 }, // wind surge 23619 94
		{ 22658 + 975, 85, 7855, 1458, 1459, 1460, 22, 130, 556, 7, 21880, 1, 555, 10, 0, 0 }, // water surge 23633 95
		{ 22628 + 975, 90, 7855, 1461, 1462, 1463, 23, 130, 556, 7, 21880, 1, 557, 10, 0, 0 }, // earth surge 23603 96
		{ 22608 + 975, 95, 7855, 1464, 1465, 1466, 24, 130, 556, 7, 21880, 1, 554, 10, 0, 0 }, // fire surge 23583 97

		{ -1, 75, 1167, 1540, 1539, 1541, 35, 2, 0, 0, 0, 0, 0, 0, 0, 0 }, // Sanguinesti staff spell 98 (other ids are off 1)

			{ 23649, 93, 0, 0, 0, 0, 0, 0, 565, 20, 566, 20, 564, 1, 0, 0 }, // Lvl-7 Enchant - Zenyte - 99

			{ -1, 75, 9493, 2125, 2126, 2127, 30, 2, 0, 0, 0, 0, 0, 0, 0, 0 }, // Tuemeken's Shadow - 100

			{ -1, 75, 9493, 2207, 2181, 2200, 30, 2, 0, 0, 0, 0, 0, 0, 0, 0 }, // Demonx Staff - 101


			{ -1, 1, 9493, 2207, 2181, 2200, 20, 2, 0, 0, 0, 0, 0, 0, 0, 0 }, // Demonx Staff - 102
		//12435, 12455, 12425, 30298, 30290, 30282
	};
	//0 = id
	//1 = level
	//2 = animation
	//3 = start gfx
	//4 = projectile
	//5 = end gfx
	//6 = base damage
	//7 = experience gained
	//8 = rune1
	//9 = rune1 amount
	//10 = rune2
	//11 = rune2 amount
	//12 = rune3
	//13 = rune3 amount
	/**
	 * Contains data of every magic spell and requirements
	 *
	 * Spell id - Level req - Animation - Start GFX - Projectile GFX
	 * End GFX - Max hit - Experience - rune - amount
	 * rune - amount - rune - amount - rune - amount
	 */

	public static final int[] AUTOCAST_IDS = { 51133, 32, 51185, 33, 51091, 34, 24018, 35, 51159, 36, 51211, 37, 51111, 38, 51069, 39, 51146, 40, 51198, 41, 51102, 42, 51058, 43, 51172, 44,
			51224, 45, 51122, 46, 51080, 47, 7038, 0, 7039, 1, 7040, 2, 7041, 3, 7042, 4, 7043, 5, 7044, 6, 7045, 7, 7046, 8, 7047, 9, 7048, 10, 7049, 11, 7050, 12, 7051, 13,
			7052, 14, 7053, 15, 47019, 27, 47020, 25, 47021, 12, 47022, 13, 47023, 14, 47024, 15
	};
    public static final int[] REDUCE_SPELL_TIME = { 250000, 250000, 250000, 500000, 500000, 500000 };
    public static final int[] REDUCE_SPELLS = { 1153, 1157, 1161, 1542, 1543, 1562 };

    public static int getSpellId(int index) {
		if (index < 0)
			return -1;
		return MAGIC_SPELLS[index][0];
	}

	public static int[] getSpellData(int spellId) {
		for (int[] spellData: MAGIC_SPELLS) {
			int spellDataId = spellData[0];
			if (spellId == spellDataId)
				return spellData;
		}
		return null;
	}

	public static int getBaseDamage(int[] spellData) {
		if (spellData == MAGIC_SPELLS[98]) {
			return 35;
		}
		if (spellData == MAGIC_SPELLS[52]) {
			return 35;
		}
		if (spellData == MAGIC_SPELLS[53]) {
			return 35;
		}
		return spellData[6];
	}

	public static int getStartGfxHeight(Player c) {
		switch (getSpellId(c.getSpellId())) {
		case 12871:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public static int getEndGfxHeight(Player c) {
		if (c.oldSpellId <= -1) {
			return 100;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 12987:
		case 12901:
		case 12861:
		case 12445:
		case 1192:
		case 13011:
		case 12919:
		case 12881:
		case 12999:
		case 12911:
		case 12871:
		case 13023:
		case 12929:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public static boolean waterSpells(Player c) {
		if (c.oldSpellId <= -1) {
			return false;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1154:
		case 1163:
		case 1175:
		case 1185:
		case 23633:
			return true;

		default:
			return false;
		}
	}

	public static boolean airSpells(Player c) {
		if (c.oldSpellId <= -1) {
			return false;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1152:
		case 1160:
		case 1172:
		case 1183:
		case 23619:
			return true;

		default:
			return false;
		}
	}

	public static boolean fireSpells(Player c) {
		if (c.oldSpellId <= -1) {
			return false;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1158:
		case 1169:
		case 1181:
		case 1189:
		case 23583:
			return true;

		default:
			return false;
		}
	}

	public static boolean earthSpells(Player c) {
		if (c.oldSpellId <= -1) {
			return false;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1156:
		case 1166:
		case 1177:
		case 1188:
		case 23603:
			return true;

		default:
			return false;
		}
	}

	public static boolean shadowSpells(Player c) {
		switch (getSpellId(c.getSpellId())) {
			case 32:
			case 36:
			case 40:
			case 44:
				return true;

			default:
				return false;
		}
	}

	public static boolean godSpells(Player c) {
		if (c.getSpellId() < 0)
			return false;
		switch (getSpellId(c.getSpellId())) {
		case 1190:
			return true;

		case 1191:
			return true;

		case 1192:
			return true;

		default:
			return false;
		}
	}

	public static int getStaffNeeded(Player c, int spellId) {
		switch (getSpellId(spellId)) {
		case 1539:
			return 1409;

		case 12037:
			//return 4170;
			return !c.getItems().isWearingAnyItem(21255) ? 4170 : 21255;

		case 1190:
			return 2415;

		case 1191:
			return !c.getItems().isWearingItem(24144) ? 2416 : 24144;

		case 1192:
			// return 2417;
			return !c.getItems().isWearingAnyItem(11791, 12904) ? 2417 : 11791 - 12904;

		default:
			return 0;
		}
	}

	public static int getStartDelay(Player c) {
		switch (getSpellId(c.getSpellId())) {
		case 1539:
			return 60;

		default:
			return 53;
		}
	}

	public static int getEndHeight(Player c) {
		if (c.getSpellId() == 52) {
			return 25;
		}
		switch (getSpellId(c.getSpellId())) {
		case 1562: // stun
			return 10;

		case 12939: // smoke rush
			return 20;

		case 12987: // shadow rush
			return 28;

		case 12861: // ice rush
			return 10;

		case 12951: // smoke blitz
			return 28;

		case 12999: // shadow blitz
			return 15;

		case 12911: // blood blitz
			return 10;

		default:
			return 31;
		}
	}

	public static int getStartHeight(Player c) {
		switch (getSpellId(c.getSpellId())) {
		case 1562: // stun
			return 25;

		case 12939:// smoke rush
			return 35;

		case 12987: // shadow rush
			return 38;

		case 12861: // ice rush
			return 15;

		case 12951: // smoke blitz
			return 38;

		case 12999: // shadow blitz
			return 25;

		case 12911: // blood blitz
			return 25;

		default:
			return 43;
		}
	}

	public static int getFreezeTime(Player c) {
		if (c.oldSpellId <= -1) {
			return 0;
		}
		switch (MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1572:
		case 12861: // ice rush
			return  10;

		case 1582:
		case 12881: // ice burst
			return c.getItems().isWearingItem(27624) ? 17 : 16;

		case 1592: // Entangle
		case 12871: // ice blitz
			return c.getItems().isWearingItem(27624) ? 26 : 24;

		case 12891: // ice barrage
			return c.getItems().isWearingItem(27624) ? 35 : 32;

		default:
			return 0;
		}
	}
}
