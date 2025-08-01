package io.xeros.content.achievement_diary;

import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.combat.range.RangeData;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class AchievementDiaryKills {

	public static void kills(Player player, int npcId) {
		switch (npcId) {
		/**
		 * Achievement diary tasks
		 */
		case 5779:
			/*if (player.getSlayer().getTask().isPresent()
					&& player.getSlayer().getTask().get().getPrimaryName().equals("giant mole")) {
			}*/
			player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.KILL_GIANT_MOLE);
			break;
		case 274:
			/*if (player.getSlayer().getTask().isPresent()
					&& player.getSlayer().getTask().get().getPrimaryName().equals("steel dragon")) {
			}*/
			if (Boundary.isIn(player, Boundary.BRIMHAVEN_DUNGEON_BOUNDARY)) {
				player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.STEEL_DRAGON);
			}
			break;
		case 2840:
			if (player.getPosition().inWild())
				player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KILL_EARTH_WARRIOR);
			break;
		case 264:
			if (player.getPosition().inWild())
				player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KILL_GREEN_DRAGON);
			break;
		case 3138:
			if (player.getPosition().inWild()) {
				player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KILL_BLOODVELD);
			}
			break;
		case 3166:
		case 2243:
		case 3159:
			if (player.getPosition().inWild()) {
				player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.SPIRITUAL_WARRIOR);
			}
			break;
		case 2054:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.CHAOS_ELEMENTAL);
			break;
		case 6618:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.CRAZY_ARCHAEOLOGIST);
			break;
		case 6619:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.CHAOS_FANATIC);
			break;
		case 3168:
		case 2244:
		case 3161:
		case 2212:
			if (player.getPosition().inWild()) {
				player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.SPIRITUAL_MAGE);
			} else {
				player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_SPIRITUAL_MAGE);
			}
			break;
		case 6503:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.CALLISTO);
			break;
		case 6610:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.VENENATIS);
			break;
		case 6611:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.VETION);
			break;
		case 891:
			break;
		case 2167:
			player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.KILL_TZHAAR_XIL);
			break;
		case 1798:
		case 1799:
		case 1800:
			player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.KILL_WHITE_KNIGHT);
			break;
		case 2593:
			player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_WEREWOLF);
			break;
		case 414:
			player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_BANSHEE);
			break;
		case 415:
			/*if (player.getSlayer().getTask().isPresent()
					&& player.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon")) {
			}*/
			player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_ABYSSAL_DEMON);
			break;
		case 3209:
			player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_CAVE_HORROR);
			break;
		case 11:
			player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_NECHRYAEL);
			break;
		case 2919:
			/*if (player.getSlayer().getTask().isPresent() && player.getSlayer().getTask().get().getPrimaryName().equals("mithril dragon")) {
			}*/
			player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.KILL_MITHRIL_DRAGON_KAN);
			break;
		case 100:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_ROCK_CRAB);
			break;
		case 2265:
		case 2266:
		case 2267:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.DAGANNOTH_KINGS);
			break;
		case 2215:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_BANDOS);
			break;
		case 3129:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_ZAMORAK);
			break;
		case 3162:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_ARMADYL);
			break;
		case 2205:
			player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_SARADOMIN);
			break;
		case 3428:
		case 3429:
			if (player.getItems().isWearingAnyItem(RangeData.CRYSTAL_BOWS)) {
				player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.KILL_ELVES);
			}
			break;
		case 499:
			player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.KILL_THERMO);
			break;
		case 2042:
		case 2043:
		case 2044:
			/*if (player.getSlayer().getTask().isPresent()
					&& player.getSlayer().getTask().get().getPrimaryName().equals("zulrah")) {
			}*/
			player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.KILL_ZULRAH);
			break;
		case 2064:
		case 2067:
			break;
		case 3544:
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.KILL_SNAKES_DESERT);
			break;
		case 458:
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.KILL_LIZARDS_DESERT);
			break;
		case 690:
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.KILL_BANDIT);
			break;
		case 1267:
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.KILL_VULTURE);
			break;
		case 423:
			if (player.getItems().isWearingAnyItem(player.SLAYER_HELMETS)) {
				player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.KILL_DUST_DEVIL);
			}
			break;
		case 6615:
			player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.SCORPIA);
			break;
		}
	}

}
