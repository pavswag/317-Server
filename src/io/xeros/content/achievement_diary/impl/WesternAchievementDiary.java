package io.xeros.content.achievement_diary.impl;

import java.util.EnumSet;
import java.util.Set;

import io.xeros.content.achievement_diary.StatefulAchievementDiary;
import io.xeros.model.entity.player.Player;

import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.COOK_MONK;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.CRYSTAL;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.HERB_PACK;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.SEED_PACK;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.FLETCH_OAK_SHORT_WEST;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.FULL_VOID;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.GNOME_AGILITY;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.KILL_ELVES;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.KILL_THERMO;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.KILL_ZULRAH;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.PEST_CONTROL;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.PEST_CONTROL_TELEPORT;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.PICKPOCKET_GNOME;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.SPIRIT_TREE_WEST;
import static io.xeros.content.achievement_diary.impl.WesternDiaryEntry.UPGRADE_VOID;

public final class WesternAchievementDiary extends StatefulAchievementDiary<WesternDiaryEntry> {
	
	public static final Set<WesternDiaryEntry> EASY_TASKS = EnumSet.of(PEST_CONTROL, GNOME_AGILITY, PEST_CONTROL_TELEPORT, FLETCH_OAK_SHORT_WEST);
	
	public static final Set<WesternDiaryEntry> MEDIUM_TASKS = EnumSet.of(SPIRIT_TREE_WEST, CRYSTAL, HERB_PACK, SEED_PACK);
	
	public static final Set<WesternDiaryEntry> HARD_TASKS = EnumSet.of(KILL_ELVES, COOK_MONK, KILL_ZULRAH, PICKPOCKET_GNOME);
	
	public static final Set<WesternDiaryEntry> ELITE_TASKS = EnumSet.of(KILL_THERMO, FULL_VOID, UPGRADE_VOID);
	
	public static final String NAME = "Western area";

	public WesternAchievementDiary(Player player) {
		super(NAME, player);
	}
	
	public boolean hasCompleted(String difficulty) {
		switch (difficulty) {
		case "EASY":
			return achievements.containsAll(EASY_TASKS);
			
		case "MEDIUM":
			return achievements.containsAll(MEDIUM_TASKS);
			
		case "HARD":
			return achievements.containsAll(HARD_TASKS);
			
		case "ELITE":
			return achievements.containsAll(ELITE_TASKS);
		}
		return achievements.containsAll(EASY_TASKS);
	}
	
	int REWARD =  13141;
	public void claimReward() {
		//EASY
		if (!hasDone(EntryDifficulty.EASY)) {
			npcDialogue("Come back when you've completed the easy tasks of this area.");
			return;
		} else {
			if (!hasClaimed(EntryDifficulty.EASY)) {
				npcDialogue("Nice job, here have the tier 1 reward.");
				addReward(REWARD);
				claim(EntryDifficulty.EASY);
				return;
			} else {
				if (getCount(REWARD) == 0 && !hasClaimed(EntryDifficulty.MEDIUM)) {
					npcDialogue("Oh, you lost your reward? Don't worry, here you go.");
					addReward(REWARD);
					return;
				}
			}
		}
		
		//MEDIUM
		if (hasDone(EntryDifficulty.EASY) && hasDone(EntryDifficulty.MEDIUM) && hasClaimed(EntryDifficulty.EASY)) {
			if (hasClaimed(EntryDifficulty.MEDIUM)) {
				if (getCount(REWARD + 1) == 0) {
					if (!hasClaimed(EntryDifficulty.HARD)) {
						npcDialogue("Oh, you lost your reward? Don't worry, here you go.");
						addReward(REWARD + 1);
						return;
					}
				}
			} else {
				if (player.getItems().playerHasItem(REWARD)) {
					npcDialogue("Nice one, I will upgrade that for you..");
					upgradeReward(REWARD, REWARD + 1);
					claim(EntryDifficulty.MEDIUM);
					return;
				} else {
					npcDialogue("Bring me the previous tier reward and I will upgrade it for you!");
					return;
				}
			}
		}
		
		//HARD
		if (hasDone(EntryDifficulty.EASY) && hasDone(EntryDifficulty.MEDIUM) && hasDone(EntryDifficulty.HARD) && hasClaimed(EntryDifficulty.MEDIUM)) {
			if (hasClaimed(EntryDifficulty.HARD)) {
				if (getCount(REWARD + 2) == 0) {
					if (!hasClaimed(EntryDifficulty.ELITE)) {
						npcDialogue("Oh, you lost your reward? Don't worry, here you go.");
						addReward(REWARD + 2);
						return;
					}
				}
			} else {
				if (player.getItems().playerHasItem(REWARD + 1)) {
					npcDialogue("Nice one, I will upgrade that for you..");
					upgradeReward(REWARD + 1, REWARD + 2);
					claim(EntryDifficulty.HARD);
					return;
				} else {
					npcDialogue("Bring me the previous tier reward and I will upgrade it for you!");
					return;
				}
			}
		}
		
		//ELITE
		if (hasDone(EntryDifficulty.EASY) && hasDone(EntryDifficulty.MEDIUM) && hasDone(EntryDifficulty.HARD) && hasDone(EntryDifficulty.ELITE) && hasClaimed(EntryDifficulty.HARD)) {
			if (hasClaimed(EntryDifficulty.ELITE)) {
				if (getCount(REWARD + 3) == 0) {
					npcDialogue("Oh, you lost your reward? Don't worry, here you go.");
					addReward(REWARD + 3);
					return;
				}
			} else {
				if (player.getItems().playerHasItem(REWARD + 2)) {
					npcDialogue("Nice one, I will upgrade that for you..");
					upgradeReward(REWARD + 2, REWARD + 3);
					claim(EntryDifficulty.ELITE);
					return;
				} else {
					npcDialogue("Bring me the previous tier reward and I will upgrade it for you!");
					return;
				}
			}
		}
		
	}
	
	public void npcDialogue(String dialogue) {
		player.getDH().sendNpcChat1(dialogue, player.npcType, "Diary Manager");
		player.nextChat = -1;
	}
	public void addReward(int reward) {
		player.getItems().addItem(reward, 1);
		player.getDH().sendNpcChat1("Here you go, upgraded and ready to be used.", player.npcType, "Diary Manager");
	}
	public void upgradeReward(int reward, int upgrade) {
		player.getItems().replaceItem(player, reward, upgrade);
		player.getDH().sendNpcChat1("Here you go, upgraded and ready.", player.npcType, "Diary Manager");
	}
	public int getCount(int id) {
		return player.getItems().getItemCount(id, false);
	}

	@Override
	public Set<WesternDiaryEntry> getEasy() {
		return EASY_TASKS;
	}

	@Override
	public Set<WesternDiaryEntry> getMedium() {
		return MEDIUM_TASKS;
	}

	@Override
	public Set<WesternDiaryEntry> getHard() {
		return HARD_TASKS;
	}

	@Override
	public Set<WesternDiaryEntry> getElite() {
		return ELITE_TASKS;
	}
	
	int frameIndex;
	int amount = frameIndex == 10 || frameIndex == 16 || frameIndex == 20 ? 2 : 1;
	public final void display() {
		Set<WesternDiaryEntry> all = getAll();
		int[] frames = { 8148, 8149, 8150, 8151, 8152, 8154, 8155, 8156, 8157, 8159, 8160, 8161, 8162, 8164,
				8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8178, 8179, 8180, 8181,
				8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194 };

		player.getPA().resetQuestInterface();
		frameIndex = 0;
		
		player.getPA().sendFrame126("@red@Western Diary", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126(hasCompleted("EASY") ? "@blu@<str=1>Easy</str>" : "@blu@Easy", 8147);
		player.getPA().sendFrame126(hasCompleted("MEDIUM") ? "@blu@<str=1>Medium</str>" : "@blu@Medium", 8153);
		player.getPA().sendFrame126(hasCompleted("HARD") ? "@blu@<str=1>Hard</str>" : "@blu@Hard", 8158);
		player.getPA().sendFrame126(hasCompleted("ELITE") ? "@blu@<str=1>Elite</str>" : "@blu@Elite", 8163);
		
		all.forEach(entry -> {
			String description = entry.getDescription();
			
			/* %stage gets the current stage (e.g. 1)
			 * %maximumstage gets the maximum stage (e.g. 5)
			 * %totalstage gets both of these (e.g. 1/5)
			 */
			description = description.replace("%stagej", Integer.toString(getAbsoluteAchievementStage(entry)));
			description = description.replace("%maximumstage", Integer.toString(getMaximum(entry)));
			description = description.replace("%totalstage", (getAbsoluteAchievementStage(entry)) + "/" + getMaximum(entry));
			
			player.getPA().sendFrame126(hasDone(entry) ? "<str=0>" +description+ "</str>" : description, frames[frameIndex]);
			frameIndex += amount;
		});

		player.getPA().openQuestInterface();
	}

	@Override
	public int getMaximum(WesternDiaryEntry achievement) {
		return achievement.getMaximumStages();
	}

}