package io.xeros.content.combat.formula;

import io.xeros.Configuration;
import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.content.seasons.Christmas;
import io.xeros.content.seasons.ChristmasBoss;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.NewInterface;
import io.xeros.model.Bonus;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MeleeMaxHit {

	/**
	 * @param c
	 * @return
	 */
	public static int calculateBaseDamage(Player c) {
		int strengthBonusValue = c.getItems().getBonus(Bonus.STRENGTH); // attack
		double effective = getEffectiveStr(c);
		double base = (8 + effective + (strengthBonusValue / 8) + ((effective * strengthBonusValue) / 64)) / 10;

		if (c.npcAttackingIndex > 0) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (npc != null) {
				if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
					base *= 1.25;
				} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE)) {
					base *= 1.15;
				} else if (!c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE)) {
					if (c.getItems().isWearingItem(12018, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.20;
						}
					} else if (c.getItems().isWearingItem(10588, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.20;
						}
					} else if (c.getItems().isWearingItem(4081, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.15;
						}

					}
				}
				if (c.getItems().isWearingItem(22978)) {
					NPC npc1 = NPCHandler.npcs[c.npcAttackingIndex];
					if (Misc.linearSearch(Configuration.DRAG_IDS, npc.getNpcId()) != -1) {
						base *= 1.60;
					}
				}
				if (c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().contains("crystalline") && c.getItems().playerHasItem(23943)) {
					base *= 1.10;
				}

				if (c.getItems().isWearingItem(20727)) {
					NPC npc1 = NPCHandler.npcs[c.npcAttackingIndex];
					if (Misc.linearSearch(Configuration.LEAF_IDS, npc.getNpcId()) != -1) {
						base *= 1.18;
					}
				}
			}
		}

		if (EquipmentSet.DHAROK.isWearingBarrows(c)) {
			base *= ((c.getLevelForXP(c.playerXP[3]) - c.getHealth().getCurrentHealth()) * .01) + 1;
		}

		if (c.fullVoidMelee()) {
			base = (base * 1.10);
		} else if (c.fullORVoidMelee() && !c.getPosition().inWild()) {
			base = (base * 1.12);
		} else if (c.fullEliteORVoidMelee() && !c.getPosition().inWild()) {
			base = (base * 1.25);
		} else if (c.fullTorva() && !c.getPosition().inWild()) {
			base = (base * 1.17);
		} else if (c.fullGuardian() && !c.getPosition().inWild()) {
			base = (base * 1.22);
		} else if (c.fullSweet() && !c.getPosition().inWild() && Christmas.isChristmas()) {
			base = (base * 1.22);
		} else if (c.fullSanguine() && !c.getPosition().inWild()) {
			base = (base * 1.30);
		}

		if (c.hasFollower && c.petSummonId == 25348) {
			base = (base * 1.20);
		}

		if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && Boundary.isIn(c, Boundary.RAIDS) || c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.getTobContainer().inTob()) {
			base = (base * 1.20);
		}



		if (c.playerEquipment[Player.playerFeet] == 10556 && !c.getPosition().inWild()) { //attacker icon
			base = (base * 1.10);
		} else if (c.playerEquipment[Player.playerFeet] == 22954 && !c.getPosition().inWild()) { //Devout Boots
			base = (base * 1.10);
		}

		if (hasObsidianEffect(c)) {
			base = (base * 1.20 + (c.playerEquipment[2] == 23240 ? .05 : 0));
		}

		if (c.usingRage && !c.getPosition().inWild()) {
			base = (base * 1.50);
		}

		boolean hasDarkVersion = (c.petSummonId == 30115 || c.petSummonId == 30120 || c.petSummonId == 30122);
		if (c.hasFollower && ((
				(c.petSummonId == 30015 || c.petSummonId == 30020 || c.petSummonId == 30022 || c.petSummonId == 25350))
				|| (hasDarkVersion))) {
			if (hasDarkVersion) {
				base *= 1.10;
			} else if (Misc.random(1) == 1) {
				base *= 1.10;
			}
		}
		return (int) Math.floor(base);
	}

	public static double getEffectiveStr(Player c) {
		return CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.STRENGTH), CombatFormula.getPrayerStrengthBonus(c))
				+ c.attacking.getFightModeStrengthBonus();
	}

	public static final int[] obsidianWeapons = { 746, 747, 6523, 6525, 6526, 6527, 6528 };

	public static boolean hasObsidianEffect(Player c) {
		if (c.playerEquipment[2] != 11128 && c.playerEquipment[2] != 23240)
			return false;

		for (int weapon : obsidianWeapons) {
			if (c.playerEquipment[3] == weapon)
				return true;
		}
		return false;
	}

	public static int bestMeleeDef(Player c) {
		if (c.playerBonus[5] > c.playerBonus[6] && c.playerBonus[5] > c.playerBonus[7]) {
			return 5;
		}
		if (c.playerBonus[6] > c.playerBonus[5] && c.playerBonus[6] > c.playerBonus[7]) {
			return 6;
		}
		return c.playerBonus[7] <= c.playerBonus[5] || c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	public static int getMeleeDefenceBonus(Player c, CombatStyle style) {
		if (style == null) {
			System.err.println("Style is null!");
			return c.playerBonus[bestMeleeDef(c)];
		}

		switch (style) {
			case STAB:
				return c.getItems().getBonus(Bonus.DEFENCE_STAB);
			case SLASH:
				return c.getItems().getBonus(Bonus.DEFENCE_SLASH);
			case CRUSH:
				return c.getItems().getBonus(Bonus.DEFENCE_CRUSH);
			default:
				throw new IllegalStateException(style + "");
		}
	}

	public static int calculateMeleeDefence(Player c, Entity attacker) {
		int i;
		if (attacker.isPlayer()) {
			i = getMeleeDefenceBonus(c, attacker.asPlayer().getCombatConfigs().getWeaponMode().getCombatStyle());
		} else {
			i = c.playerBonus[bestMeleeDef(c)];
		}

		int defenceLevel = CombatFormula.getPrayerBoostedDefenceLevel(c);
		return CombatFormula.getEffectLevel(defenceLevel, i);
	}

	public static int getMeleeTypeBonus(Player c) {
		switch (c.getCombatConfigs().getWeaponMode().getCombatStyle()) {
			case STAB:
				return c.getItems().getBonus(Bonus.ATTACK_STAB);
			case SLASH:
				return c.getItems().getBonus(Bonus.ATTACK_SLASH);
			case CRUSH:
				return c.getItems().getBonus(Bonus.ATTACK_CRUSH);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static int calculateMeleeAttack(Player c) {
		int attackLevel = CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.ATTACK), CombatFormula.getPrayerMeleeAttackBonus(c));

		if (c.playerEquipment[Player.playerWeapon] == 4153 || c.playerEquipment[Player.playerWeapon] == 12848 || c.playerEquipment[Player.playerWeapon] == 24225 || c.playerEquipment[Player.playerWeapon] == 24227) {
			attackLevel -= c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.15;
		}
		if (c.playerEquipment[Player.playerWeapon] == 4718 && c.playerEquipment[Player.playerHat] == 4716 && c.playerEquipment[Player.playerChest] == 4720
				&& c.playerEquipment[Player.playerLegs] == 4722) {
			attackLevel -= c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.15;
		}
		NPC npc1 = NPCHandler.npcs[c.npcAttackingIndex];
		if (c.npcAttackingIndex > 0 && c.playerEquipment[Player.playerAmulet] == 4081 && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc1.getNpcId()) != -1) { //salve amulet
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.15;
		}
		if (c.npcAttackingIndex > 0 && c.playerEquipment[Player.playerAmulet] == 10588 && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc1.getNpcId()) != -1) { //salve amulet (e)
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.20;
		}
		if (c.npcAttackingIndex > 0 && c.playerEquipment[Player.playerAmulet] == 12018 && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc1.getNpcId()) != -1) { //salve amulet (ei)
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.20;
		}
		if (c.fullVoidMelee()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.10;
		} else if (c.fullEliteORVoidMelee() && !c.getPosition().inWild()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.40;
		} else if (c.fullORVoidMelee() && !c.getPosition().inWild()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.25;
		}

		if (c.fullTorva() && !c.getPosition().inWild()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.55;
		}
		if (c.fullGuardian() && !c.getPosition().inWild()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.95;
		}
		if (c.fullSweet() && !c.getPosition().inWild() && Christmas.isChristmas()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.95;
		}

		if (c.fullSanguine() && !c.getPosition().inWild()) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 1.25;
		}

		if (Discord.jda != null) {
			Guild guild = Discord.getGuild();

			if (guild != null) {
				for (Member booster : guild.getBoosters()) {
					if (c.getDiscordUser() == booster.getUser().getIdLong()) {
						attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.50;
					}
				}
			}
		}

		if (c.playerEquipment[Player.playerFeet] == 10556  && !c.getPosition().inWild()) { //attacker icon
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.10;
		} else if (c.playerEquipment[Player.playerFeet] == 22954 && !c.getPosition().inWild()) { //Devout Boots
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.10;
		}


		if (c.playerEquipment[Player.playerWeapon] == 28997 || c.playerEquipment[Player.playerWeapon] == 33161) {
			attackLevel += c.getLevelForXP(c.playerXP[Player.playerAttack]) * 1.4;
		}
		if (c.npcAttackingIndex > 0 && c.getItems().isWearingItem(19675, Player.playerWeapon) && c.getArcLightCharge() > 0) {
			if (c.debugMessage)
				c.sendMessage("Accuracy on reg: "+ attackLevel +"");
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (Misc.linearSearch(Configuration.DEMON_IDS, npc.getNpcId()) != -1) {
				attackLevel += (int) (c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.70);
				if (c.debugMessage)
					c.sendMessage("Accuracy on demon: "+ attackLevel +"");
			}
		}
		if (c.npcAttackingIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];

			if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
				attackLevel += (int) (c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.25);
			} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE)) {
				attackLevel += (int) (c.getLevelForXP(c.playerXP[Player.playerAttack]) * 0.15);
			}

		}
		if (hasObsidianEffect(c) || c.fullVoidMelee()) {
			attackLevel *= 1.20;
		}
		attackLevel += CombatFormula.EFFECTIVE_LEVEL_BOOST;
		attackLevel += + c.attacking.getFightModeAttackBonus();
		return CombatFormula.getEffectLevel(attackLevel, getMeleeTypeBonus(c));
	}
}