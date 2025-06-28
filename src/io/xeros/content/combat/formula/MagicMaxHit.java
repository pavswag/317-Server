package io.xeros.content.combat.formula;

import io.xeros.Configuration;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.NewInterface;
import io.xeros.model.Bonus;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MagicMaxHit {

	public static int mageAttack(Player c) {
		int magicLevel = CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.MAGIC), CombatFormula.getPrayerMagicAccuracyBonus(c));
		double modifier = 1.00;
		if (c.fullVoidMage()) {
			modifier += .45;
		} else if (c.fullORVoidMage() && !c.getPosition().inWild()) {
			modifier += .90;
		} else if (c.fullEliteORVoidMage() && !c.getPosition().inWild()) {
			modifier += 1.50;
		} else if (c.fullCeremonial() && !c.getPosition().inWild()) {
			modifier += 2.10;
		} else if (c.fullTectonic() && !c.getPosition().inWild()) {
			modifier += 2.70;
		}

		if (Discord.jda != null) {
			Guild guild = Discord.getGuild();

			if (guild != null) {
				for (Member booster : guild.getBoosters()) {
					if (c.getDiscordUser() == booster.getUser().getIdLong() && !c.getPosition().inWild()) {
						modifier += 1.50;
					}
				}
			}
		}

		if (c.npcAttackingIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (npc != null) {
				// Salve amulet
				if (c.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
					modifier += .20;
					// Slayer helmet
				} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
					modifier += .25;
				} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
					modifier += .15;
				}
			}
		}

		magicLevel *= modifier * 1.7;
		magicLevel += CombatFormula.EFFECTIVE_LEVEL_BOOST;
		return CombatFormula.getEffectLevel(magicLevel, c.getItems().getBonus(Bonus.ATTACK_MAGIC));
	}

	public static int mageDefence(Player c) {
		double prayerDefence = CombatFormula.getPrayerDefenceBonus(c);
		double defence = Math.floor(c.playerLevel[1] * .3);
		double magicDefence = Math.floor(c.playerLevel[6] * .7);
		//defence += magicDefence + c.getItems().getBonus(Bonus.DEFENCE_MAGIC);
		//return (int) defence;
		return CombatFormula.getEffectLevel((int) ((magicDefence + defence) * prayerDefence), c.getItems().getBonus(Bonus.DEFENCE_MAGIC));
	}

	public static int getNightmareSpecialMaxHit(int magicLevel, int base) {
		double modifier = (double) magicLevel / 75d;
		return (int) Math.floor(base * modifier);
	}

	public static int magiMaxHit(Player c) {
		if (c.oldSpellId <= -1) {
			return 0;
		}
		double damage = CombatSpellData.MAGIC_SPELLS[c.oldSpellId][6];
		double damageMultiplier = 1.0 + ((double) c.getItems().getBonus(Bonus.MAGIC_DMG) / 100d);


		switch (c.playerEquipment[Player.playerWeapon]) {
			case 24424://volatile
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 44); // lowered to 44 from 50
				}
				break;
			case 24425://eldritch
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 39);
				}
				break;
		}

		if (c.playerEquipment[Player.playerFeet] == 10556 ) { //attacker icon
			damageMultiplier += .10;
		} else if (c.playerEquipment[Player.playerFeet] == 22954 ) { //Devout Boots
			damageMultiplier += .10;
		}

		if (c.npcAttackingIndex > 0) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
				damageMultiplier += .25;
			} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
				damageMultiplier += .15;
			}

			if (c.hasFollower && c.petSummonId == 25348) {
				damageMultiplier += .20;
			}

			if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && Boundary.isIn(c, Boundary.RAIDS) || c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.getTobContainer().inTob()) {
				damageMultiplier += .20;
			}
		}
		NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
		if (c.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
			damageMultiplier += .20;
		}
		boolean hasDarkVersion = (c.petSummonId == 30117 || c.petSummonId == 30120 || c.petSummonId == 30122);

		if (c.hasFollower
				&& ((c.petSummonId == 30017 || c.petSummonId == 30020 || c.petSummonId == 30022  || c.petSummonId == 25350))
				|| (hasDarkVersion)){
			if (hasDarkVersion) {
				damageMultiplier += .10;
			} else if (Misc.random(1) == 1) {
				damageMultiplier += .10;
			}
		}

		if (c.usingRage && !c.getPosition().inWild()) {
			damageMultiplier += 0.50;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS1) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS2) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS3) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			if (c.getMode().isOsrs() || c.getMode().is5x()) {
				damageMultiplier += 1.20;
			} else {
				damageMultiplier += 0.60;
			}
		}
		if (c.oldSpellId > -1) {
			switch (CombatSpellData.MAGIC_SPELLS[c.oldSpellId][0]) {
				case 12037:
					if (c.getItems().isWearingAnyItem(Items.SLAYERS_STAFF_E) && c.getSlayer().getTask().isPresent()) {

						//NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
						if (npc != null && c.getSlayer().getTask().get().matches(npc.getDefinition().getName())) {
							damage += (c.playerLevel[6] / 10) + 1;
						}
					} else {
						damage += c.playerLevel[6] / 14;
					}
					break;
			}

			damage *= damageMultiplier;
			return (int) damage;
		}

		return 0;
	}
}
