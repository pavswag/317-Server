package io.xeros.content.achievement_diary.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.xeros.content.achievement_diary.StatefulAchievementDiary;
import io.xeros.model.entity.player.Player;

import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.ALOT_OF_EARTH;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.APOTHECARY_STRENGTH;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.BECOME_A_DANCER;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.CHAMPIONS_GUILD;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.COOK_LOBSTER;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.DRESS_FOR_SUCESS;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.EARTH_RUNES;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.FILL_VIAL;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.GRAND_TREE_TELEPORT;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.MINE_ESSENCE;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.MINE_IRON;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.OBSTACLE_PIPE;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.POTION_DECANT;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.PRAY_WITH_SMITE;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.PURCHASE_KITTEN;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.SMITH_RUNE_KNIFES;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.SMITH_STEEL_KNIFES;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.SUPER_COMBAT;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.TEA_STALL;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.TELEPORT_ESSENCE_VAR;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.VARROCK_ROOFTOP;
import static io.xeros.content.achievement_diary.impl.VarrockDiaryEntry.YEWS_AND_BURN;

public final class VarrockAchievementDiary extends StatefulAchievementDiary<VarrockDiaryEntry> {
	
	public static final Set<VarrockDiaryEntry> EASY_TASKS = EnumSet.of(VARROCK_ROOFTOP,SMITH_STEEL_KNIFES,FILL_VIAL,EARTH_RUNES,TELEPORT_ESSENCE_VAR,MINE_ESSENCE,TEA_STALL,BECOME_A_DANCER,MINE_IRON);
	
	public static final Set<VarrockDiaryEntry> MEDIUM_TASKS = EnumSet.of(CHAMPIONS_GUILD,DRESS_FOR_SUCESS,APOTHECARY_STRENGTH,PURCHASE_KITTEN,GRAND_TREE_TELEPORT,POTION_DECANT);
	
	public static final Set<VarrockDiaryEntry> HARD_TASKS = EnumSet.of(PRAY_WITH_SMITE,OBSTACLE_PIPE,YEWS_AND_BURN,COOK_LOBSTER);
	
	public static final Set<VarrockDiaryEntry> ELITE_TASKS = EnumSet.of(SUPER_COMBAT,SMITH_RUNE_KNIFES,ALOT_OF_EARTH);
	
	public static final String NAME = "Varrock area";

	public VarrockAchievementDiary(Player player) {
		super(NAME, player);
	}
	
	public boolean hasCompleted(String difficulty) {
		switch (difficulty) {
		case "EASY":
			return hasDone(EntryDifficulty.EASY);
			
		case "MEDIUM":
			return hasDone(EntryDifficulty.MEDIUM);
			
		case "HARD":
			return hasDone(EntryDifficulty.HARD);
			
		case "ELITE":
			return hasDone(EntryDifficulty.ELITE);
		}
		return hasDone(EntryDifficulty.EASY);
	}
	
	int REWARD = 13104;

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
		player.getDH().sendNpcChat1("Here you go, you've earned it.", player.npcType, "Diary Manager");
	}
	public void upgradeReward(int reward, int upgrade) {
		player.getItems().replaceItem(player, reward, upgrade);
		player.getDH().sendNpcChat1("Here you go, upgraded and ready.", player.npcType, "Diary Manager");
	}
	public int getCount(int id) {
		return player.getItems().getItemCount(id, false);
	}

	@Override
	public Set<VarrockDiaryEntry> getEasy() {
		return EASY_TASKS;
	}

	@Override
	public Set<VarrockDiaryEntry> getMedium() {
		return MEDIUM_TASKS;
	}

	@Override
	public Set<VarrockDiaryEntry> getHard() {
		return HARD_TASKS;
	}

	@Override
	public Set<VarrockDiaryEntry> getElite() {
		return ELITE_TASKS;
	}
	
	int frameIndex;
	int amount = frameIndex == 10 || frameIndex == 16 || frameIndex == 20 ? 2 : 1;
	public final void display() {
		Set<VarrockDiaryEntry> all = getAll();
		int[] frames = { 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8158, 8159, 8160, 8161, 8162, 8163,
				8165, 8166, 8167, 8168, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8178, 8179, 8180, 8181,
				8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194 };

		player.getPA().resetQuestInterface();
		frameIndex = 0;
		
		player.getPA().sendFrame126("@red@Varrock Diary", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126(hasCompleted("EASY") ? "@blu@<str=1>Easy</str>" : "@blu@Easy", 8147);
		player.getPA().sendFrame126(hasCompleted("MEDIUM") ? "@blu@<str=1>Medium</str>" : "@blu@Medium", 8157);
		player.getPA().sendFrame126(hasCompleted("HARD") ? "@blu@<str=1>Hard</str>" : "@blu@Hard", 8164);
		player.getPA().sendFrame126(hasCompleted("ELITE") ? "@blu@<str=1>Elite</str>" : "@blu@Elite", 8169);
		
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
	public int getMaximum(VarrockDiaryEntry achievement) {
		return achievement.getMaximumStages();
	}

	public String getProgressText(EntryDifficulty difficulty) {
		Set<VarrockDiaryEntry> set = difficulty == EntryDifficulty.EASY ? getEasy()
				: difficulty == EntryDifficulty.MEDIUM ? getMedium()
				: difficulty == EntryDifficulty.HARD ? getHard()
				: getElite();
		List<Boolean> collect = set.stream().map(it -> {
			int current = getAbsoluteAchievementStage(it);
			int maximum = getMaximum(it);
			if (maximum == -1)
				maximum = 1;
			return current >= maximum;
		}).collect(Collectors.toList());
		int total = collect.size();
		int complete = (int) collect.stream().filter(it -> it == true).count();
		return complete + "/" + total;
	}

}