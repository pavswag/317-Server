package io.xeros.content.prestige;

import io.xeros.Configuration;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class PrestigeSkills {

	public Player player;
	
	public PrestigeSkills(Player player) {
		this.player = player;
	}
	/**
	 * Previously players could only prestige a limited number of times. In
	 * order to allow unlimited prestiges we set the cap extremely high. The
	 * check in {@link #prestige()} compares using equality so a large value
	 * effectively makes the cap unreachable during normal play.
	 */
	public final int MAX_PRESTIGE = Integer.MAX_VALUE;
	
	public int points = 1; // This is the base prestige points given
	
	public void openPrestige() { // Refreshes all text lines before showing the interface - Looks better
		for (int j = 0; j < 22; j++) {
			player.getPA().sendFrame126(""+player.prestigeLevel[0]+"", 37400 + j); // Update Current Prestige on interface
		}
		registerClick(0);
		player.getPA().showInterface(37300);
	}
	
	public void openShop() {
		player.sendMessage("@blu@ You have "+player.getPrestigePoints()+" prestige points.");
		player.getShops().openShop(120);
	}
	
	public void registerClick(int i) {
		i = 0;
		player.prestigeNumber = i;
		player.currentPrestigeLevel = player.prestigeLevel[player.prestigeNumber];

		String canPrestige = ((player.maxRequirements(player)) ? "@gre@Yes" : "@red@No"); // String version for interface Yes or No
		player.getPA().sendFrame126("Overall", 37307); // Update Skill Name
		player.getPA().sendFrame126("Current Prestige: @whi@"+player.currentPrestigeLevel, 37308); // Update Current Prestige in box
		int projectedReward = 2000 + (player.currentPrestigeLevel * 100);
		player.getPA().sendFrame126("Reward: @whi@"+ projectedReward +" Points",37309);
		player.getPA().sendFrame126("Can Prestige: "+ canPrestige, 37390); // Update If you can prestige
	}
	
	public void prestige() {
		// The MAX_PRESTIGE value is effectively unlimited, but we keep
		// this check in case a player somehow reaches it.
		if (player.prestigeLevel[0] == MAX_PRESTIGE) {
			player.sendMessage("You are the max prestige level in this skill!");
			return;
		}

		if (player.getItems().isWearingItems()) { // Change to prestige master
			player.getDH().sendNpcChat1("You must remove your equipment to prestige this stat.", 2989, "Ak-Haranu");
			return;
		}

		if (player.maxRequirements(player)) { // If the skill is 99
			if (player.VERIFICATION == 0) { // Verification Check
				player.sendMessage("@red@Please click prestige again to confirm prestiging of the all skills (except DemonHunter & Fortune).");
				player.VERIFICATION++;
				return;
			}
			player.VERIFICATION = 0;

			for (int i = 0; i < 22; i++) {
				player.playerLevel[i] =  ( i == 3 ? 10 : 1);
				player.playerXP[i] = player.getPA().getXPForLevel(( i == 3 ? 10 : 1));
				player.getPA().refreshSkill(i);
				player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
				player.getPA().levelUp(i);
			}
			if (Misc.random(2) == 2) {
				player.getItems().addItemUnderAnyCircumstance(28693, 1);
				int rights = player.getRights().getPrimary().getValue() - 1;
				PlayerHandler.executeGlobalMessage("[@red@PRESTIGE@bla@] @cr20@<col=255> <img=" + rights + ">"
						+ player.getDisplayName() + "</col> received a Turmoil Prayer Scroll from <col=255>Prestiging.</col>.");
			}
			if (Misc.random(2) == 2) {
				player.getItems().addItemUnderAnyCircumstance(28696, 1);
				int rights = player.getRights().getPrimary().getValue() - 1;
				PlayerHandler.executeGlobalMessage("[@red@PRESTIGE@bla@] @cr20@<col=255> <img=" + rights + ">"
						+ player.getDisplayName() + "</col> received a SoulSplit Prayer Scroll from <col=255>Prestiging.</col>.");
			}
			if (Misc.random(3) == 3) {
				player.getItems().addItemUnderAnyCircumstance(26886, 1);
				int rights = player.getRights().getPrimary().getValue() - 1;
				PlayerHandler.executeGlobalMessage("[@red@PRESTIGE@bla@] @cr20@<col=255> <img=" + rights + ">"
						+ player.getDisplayName() + "</col> received a Upgrader Cell from <col=255>Prestiging.</col>.");
			}

			int level = player.prestigeLevel[player.prestigeNumber];
			int coinReward = 25 + (level * 5);
			player.getItems().addItemUnderAnyCircumstance(33251, coinReward);
			int rights = player.getRights().getPrimary().getValue() - 1;
			PlayerHandler.executeGlobalMessage("[@red@PRESTIGE@bla@] @cr20@<col=255> <img=" + rights + ">"+
					player.getDisplayName() + "</col> received " + coinReward + " donation coins from <col=255>Prestiging.</col>.");

			player.combatLevel = player.calculateCombatLevel();
			player.getPA().sendFrame126("Combat Level: " + player.combatLevel + "", 3983);
			int pointReward = 2000 + (level * 100);
			player.prestigePoints += pointReward;
			player.prestigeLevel[player.prestigeNumber] += 1;
			registerClick(player.prestigeNumber);
			player.getPA().sendFrame126(""+player.prestigeLevel[player.prestigeNumber]+"", 37400 + player.prestigeNumber); // Update Current Prestige on interface
		} else {
			player.sendMessage("You need to have level 99 in all skills except Fortune & DemonHunter.");
		}
	}
	
	public boolean prestigeClicking(int id) {
		if (id != 146015)
			player.VERIFICATION = 0;
		switch (id) {
			case 145191:
				registerClick(0);
			return true;
			case 145192:
				registerClick(1);
			return true;
			case 145193:
				registerClick(2);
			return true;
			case 145194:
				registerClick(3);
			return true;
			case 145195:
				registerClick(4);
			return true;
			case 145196:
				registerClick(5);
			return true;
			case 145197:
				registerClick(6);
			return true;
			case 145198:
				registerClick(7);
			return true;
			case 145199:
				registerClick(8);
			return true;
			case 145200:
				registerClick(9);
			return true;
			case 145201:
				registerClick(10);
			return true;
			case 145202:
				registerClick(11);
			return true;
			case 145203:
				registerClick(12);
			return true;
			case 145204:
				registerClick(13);
			return true;
			case 145205:
				registerClick(14);
			return true;
			case 145206:
				registerClick(15);
			return true;
			case 145207:
				registerClick(16);
			return true;
			case 145208:
				registerClick(17);
			return true;
			case 145209:
				registerClick(18);
			return true;
			case 145210:
				registerClick(19);
			return true;
			case 145211:
				registerClick(20);
			return true;
			case 145212:
				registerClick(21);
			return true;
			case 146015:
				prestige();
			return true;
			case 145182:
				player.getPA().closeAllWindows();
			return true;
		}
		return false;
	}
}