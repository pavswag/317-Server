package io.xeros.content.combat.core;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.WeaponGames.WGManager;
import io.xeros.content.WeaponGames.WGMedPack;
import io.xeros.content.WeaponGames.WGPowerup;
import io.xeros.content.bosses.bryophyta.BryophytaNPC;
import io.xeros.content.bosses.nightmare.attack.Spores;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.xeros.content.combat.effects.damageeffect.DamageEffect;
import io.xeros.content.combat.effects.damageeffect.impl.ToxicBlowpipeEffect;
import io.xeros.content.combat.effects.damageeffect.impl.ToxicStaffOfTheDeadEffect;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.GuthanEffect;
import io.xeros.content.combat.effects.special.impl.ScytheOfVitur;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.content.combat.formula.rework.MeleeCombatFormula;
import io.xeros.content.combat.formula.rework.RangeCombatFormula;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.SoundData;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.combat.range.Arrow;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.combat.specials.impl.*;
import io.xeros.content.combat.weapon.RangedWeaponType;
import io.xeros.content.items.ChristmasWeapons;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.items.aoeweapons.AOESystem;
import io.xeros.content.items.aoeweapons.AoeWeapons;
import io.xeros.content.minigames.pest_control.PestControl;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.content.skills.Skill;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.items.EquipmentSet;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This class determines the hit damage and queues the hit to be processed by {@link HitDispatcher}.
 */
public abstract class HitDispatcher {

    public static Random rand = new Random();

    public static HitDispatcher getHitEntity(Player attacker, Entity defender) {
        if (defender.isNPC()) {
            return new HitDispatcherNpc(attacker, defender);
        } else {
            return new HitDispatcherPlayer(attacker, defender);
        }
    }

    protected int damage;
    protected int damage2 = -1;
    protected int damage3 = -1;
    protected int defence;
    protected int maximumDamage;
    protected double maximumAccuracy;
    protected boolean success = true;
    private Hitmark hitmark1;
    private Hitmark hitmark2;
    private Hitmark hitmark3;
    protected Player attacker;
    protected Entity defender;

    public abstract void beforeDamageCalculated(CombatType type);

    public abstract void afterDamageCalculated(CombatType type, boolean successfulHit);

    public HitDispatcher(Player attacker, Entity defender) {
        this.attacker = attacker;
        this.defender = defender;
    }

    public void playerHitEntity(CombatType combatType, Special special) {
        playerHitEntity(combatType, special, false);
    }

    private void playerHitEntity(CombatType combatType, Special special, boolean applyingMultiHitAttack) {
        if (attacker == null || defender == null) {
            return;
        }

        // This defence calculation isn't used for magic, see the magic section
        if (combatType != CombatType.MAGE) {
            defence = defender.getDefenceLevel() + defender.getDefenceBonus(combatType, attacker);
        }
//        if (Misc.random(20) == 20) {
//            GlobalObject globalObject  = new GlobalObject(33493, attacker.getX() + Misc.random(1), attacker.getY() + Misc.random(1), attacker.getHeight(), 0, 10, 60, -1);
//            Server.getGlobalObjects().add(globalObject);
//            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
//        }
//
//        if  (Misc.random(20) == 20) {
//            GlobalObject globalObject  = new GlobalObject(33494, attacker.getX() + Misc.random(1), attacker.getY() + Misc.random(1), attacker.getHeight(), 0, 10, 60, -1);
//            Server.getGlobalObjects().add(globalObject);
//            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
//        }
        boolean gainExperience = attacker.getMode().isPVPCombatExperienceGained() && !(special instanceof Shove)
                && (defender.isPlayer() || defender.asNPC().getNpcId() != 7413);

        boolean isMaxHitDummy = false;
        if (defender.isNPC()) {
            isMaxHitDummy = defender.asNPC().getNpcId() == Npcs.MAX_DUMMY;
        }

        PvpWeapons.removeCharges(attacker, attacker.playerEquipment[Player.playerWeapon], 1);
        /**
         * Melee attack style
         */

        boolean usingSythe = false;


        if (combatType.equals(CombatType.MELEE)) {

            double defenceMultiplier = 1.0;
            double specialAccuracy = 1.0;
            double specialDamageBoost = 1.0;
            double specialPassiveMultiplier = 1.0;
            if (special != null) {
                specialAccuracy = special.getAccuracy();
                specialDamageBoost = special.getDamageModifier();

                if (special instanceof StatiusWarhammer) {
                    specialDamageBoost += isMaxHitDummy ? 1.0 : rand.nextDouble();
                } else if (special instanceof VestaLongsword) {
                    specialDamageBoost += isMaxHitDummy ? 1.0 : rand.nextDouble();
                    defenceMultiplier -= 0.75;
                }
            }


            maximumAccuracy = MeleeCombatFormula.get().getAccuracy(attacker, defender, specialAccuracy,
                    defenceMultiplier);
            maximumDamage = MeleeCombatFormula.get().getMaxHit(attacker, defender, specialDamageBoost,
                    specialPassiveMultiplier);

            beforeDamageCalculated(combatType);

            usingSythe = ScytheOfVitur.SCYTHE_EFFECT.activateSpecialEffect(attacker, defender);

            damage = isMaxHitDummy ? maximumDamage : Misc.random((int) maximumDamage);
            boolean isAccurate = isMaxHitDummy || maximumAccuracy >= rand.nextDouble();

            afterDamageCalculated(combatType, isAccurate);
            if (damage > 0) {
                // Guthan's Armour effect
                if (Misc.trueRand(4) == 1) {
                    boolean guthanGfxFlag = false;
                    if (GuthanEffect.INSTANCE.canUseEffect(attacker)) {
                        guthanGfxFlag = true;
                        GuthanEffect.INSTANCE.useEffect(attacker, defender, new Damage(damage));
                    } else if (EquipmentSet.GUTHAN.isWearing(attacker) || attacker.playerEquipmentCosmetic[Player.playerAura] == 10559 && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 2);
                    } else if (attacker.getItems().isWearingItem(13372) && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 3);
                    }
                    if (guthanGfxFlag) {
                        defender.startGraphic(new Graphic(399));
                    }
                }
            }

            if (attacker.getChristmasWeapons().getCharges(33161) > 0 && (attacker.getChristmasWeapons().getCharges(33161) - 50) > 0
                    && attacker.getItems().isWearingItem(33161) && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                    ChristmasWeapons.removeCharges(attacker, 33161, 50);
                } else if (Misc.trueRand(5) == 1) {
                    attacker.getHealth().increase((damage / 2));
                    defender.startGraphic(new Graphic(399));
                    ChristmasWeapons.removeCharges(attacker, 33161, 50);
                }
            }
            if (attacker.getItems().isWearingItem(25736) && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1) {
                    attacker.getHealth().increase((damage / 4));
                    defender.startGraphic(new Graphic(399));
                }
            }
            if (attacker.getItems().isWearingItem(28534) && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                    attacker.getHealth().increase((damage / 4));
                    //defender.startGraphic(new Graphic(399));
            }
            if (attacker.getItems().isWearingItem(30338) && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                attacker.prayerPoint = attacker.playerLevel[5];
            defender.startGraphic(new Graphic(399));
        }
            if (attacker.getItems().isWearingItem(30332) && attacker.getItems().isWearingItem(30330) && attacker.getItems().isWearingItem(30334)) {
                attacker.prayerPoint = attacker.playerLevel[5];
                    defender.startGraphic(new Graphic(399));
            }
            if (attacker.getItems().isWearingItem(25739) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1 && !attacker.getArboContainer().inArbo()) {
                    attacker.getHealth().increase((damage / 4));
                    defender.startGraphic(new Graphic(399));
                } else if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                }
            }

            if (attacker.getItems().isWearingItem(33184) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1 && !attacker.getArboContainer().inArbo()) {
                    attacker.getHealth().increase((damage / 3));
                    damage = (int) (damage + (damage * 0.15));
                    defender.startGraphic(new Graphic(399));
                } else if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                }
            }

            if (attacker.getItems().isWearingItem(33203) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1 && !attacker.getArboContainer().inArbo()) {
                    attacker.getHealth().increase((damage / 2));
                    damage = (int) (damage + (damage * 0.15));
                    defender.startGraphic(new Graphic(399));
                } else if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                }
            }

            if (attacker.getItems().isWearingItem(33431) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.45));
                    attacker.startGraphic(new Graphic(400));
                }
            }
            if (attacker.getItems().isWearingItem(33202) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                }
            }

            if (attacker.getItems().isWearingItem(33204) && !defender.isPlayer()) {
                if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                }
            }

            if (attacker.getChristmasWeapons().getCharges(33162) > 0
                    && (attacker.getChristmasWeapons().getCharges(33162) - 50) > 0
                    && attacker.getItems().isWearingItem(33162)) {
                if (Misc.trueRand(5) == 1) {
                    damage = (int) (damage + (damage * 0.15));
                    attacker.startGraphic(new Graphic(400));
                    ChristmasWeapons.removeCharges(attacker, 33162, 50);
                }
            }

            if (attacker.bonusDmg) {
                damage = (int) (damage + (damage * 0.20));
            }

            if (attacker.dailyDamage > 0) {
                damage = (int) (damage + (damage * 0.10));
            }
            if (attacker.EliteCentBoost > 0 && !attacker.getPosition().inWild()) {
                damage *= 2;
            }
            if (!isAccurate && attacker.getItems().isWearingItem(26219) || !isAccurate && attacker.getItems().isWearingItem(27246) || !isAccurate && attacker.getItems().isWearingItem(33202)) {
                if (damage <= 0) {
                    damage = 1;
                    isAccurate = true;
                }
            }

            // melee accuracy roll
            if (!isAccurate) {
                damage = 0;
                success = false;

                if (attacker.getItems().isWearingItem(26219) || attacker.getItems().isWearingItem(27246) || attacker.getItems().isWearingItem(33202)) {
                    damage = attacker.getItems().isWearingItem(33202) ? 5 : 1;
                    success = true;
                }
            }

            if (usingSythe) {
                if (defender.getEntitySize() >= 1 && !defender.isPlayer() || isMaxHitDummy) {
                    if (damage > 0 && (damage / 2) > 0) {
                        damage2 = (damage / 2);
                    } else {
                        damage2 = 0;
                    }
                    if (damage2 > 0 && (damage2 / 2) > 0) {
                        damage3 = (damage2 / 2);
                    } else {
                        damage3 = 0;
                    }
                }
            }

            if (attacker.isPrintAttackStats() && !applyingMultiHitAttack) {
                double hitPercentage = attacker.ignoreDefence ? 100 : maximumAccuracy * 100;

                attacker.sendMessage("p->e Melee"
                        + ", Hit%: " + String.format("%.2f", hitPercentage) + "%"
                        + ", Max: " + maximumDamage + "/" + maximumDamage
                        + ", IsAccurate: " + isAccurate
                        + ", Style: " + attacker.getCombatConfigs().getWeaponMode());
            }

            if (defender.getHealth().getCurrentHealth() - damage < 0) {
                damage = defender.getHealth().getCurrentHealth();
            }
            if (damage2 > 0) {
                if (damage == defender.getHealth().getCurrentHealth() && defender.getHealth().getCurrentHealth() - damage2 > 0) {
                    damage2 = 0;
                }
            }
            if (defender.getHealth().getCurrentHealth() - damage - damage2 < 0) {
                damage2 = defender.getHealth().getCurrentHealth() - damage;
            }
            if (damage < 0) {
                damage = 0;
            }
            if (damage2 < 0 && damage2 != -1) {
                damage2 = 0;
            }

            hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
            hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
            hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;

            if (gainExperience) {
                addCombatXP(CombatType.MELEE, damage + Math.max(0, damage2) + Math.max(0, damage3));
            }
            boolean hasDarkHealerVersion = attacker.petSummonId == 30118 || attacker.petSummonId == 30122;
            int healerChance = 10;
            if (damage > 0 && attacker.hasFollower && ((attacker.petSummonId == 30018 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122) || hasDarkHealerVersion) && Misc.random(healerChance) == 1) {
                attacker.getHealth().increase(damage / 3);
                if (attacker.playerLevel[3] > attacker.getPA().getLevelForXP(attacker.playerXP[3])) {
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                }
                attacker.getPA().refreshSkill(3);
            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.HEAL_TO_MAX) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.getHealth().setCurrentHealth(attacker.getHealth().getMaximumHealth());
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                    attacker.getPA().refreshSkill(3);
                }
            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.RESTORE_FULL_PRAYER) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                    attacker.getPA().refreshSkill(5);
                }
            }
            boolean hasDarkPrayerVersion = attacker.petSummonId == 30119 || attacker.petSummonId == 30122;
            int prayerChance = 10;
            if (damage > 0 && attacker.hasFollower && ((attacker.petSummonId == 30019 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122) || hasDarkPrayerVersion) && Misc.random(prayerChance) == 1) {
                int halfDamage = (int) (damage / 3);
                attacker.playerLevel[5] += (halfDamage);
                if (attacker.playerLevel[5] > attacker.getPA().getLevelForXP(attacker.playerXP[5])) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                }
                attacker.getPA().refreshSkill(5);
            }


            /**
             * Ranged attack style
             */
        } else if (combatType.equals(CombatType.RANGE)) {
            double defenceMultiplier = 1.0;
            double specialAccuracy = 1.0;
            double specialDamageBoost = 1.0;
            double specialPassiveMultiplier = 1.0;
            if (special != null) {
                specialAccuracy = special.getAccuracy();
                specialDamageBoost = special.getDamageModifier();

                if (special instanceof DarkBow) {
                    // Add a 20% damage boost to darkbow if using dragon arrows
                    int ammoId = attacker.playerEquipment[Player.playerArrows];
                    boolean usingDragonArrows = Arrow.matchesMaterial(ammoId, Arrow.DRAGON);
                    if (usingDragonArrows) {
                        specialAccuracy += 0.2;
                    }
                }
            }

            maximumAccuracy = RangeCombatFormula.STANDARD.getAccuracy(attacker, defender, specialAccuracy,
                    defenceMultiplier);
            maximumDamage = RangeCombatFormula.STANDARD.getMaxHit(attacker, defender, specialDamageBoost,
                    specialPassiveMultiplier);

            if (attacker.bonusDmg) {
                maximumDamage *= 1.20;
            }
            if (attacker.dailyDamage > 0) {
                maximumDamage *= 1.10;
            }
            if (attacker.EliteCentBoost > 0 && !attacker.getPosition().inWild()) {
                maximumDamage *= 2;
            }
            if (attacker.weaponUsedOnAttack == 10501) {
                maximumAccuracy = 0;
                maximumDamage = 0;
            }

            beforeDamageCalculated(combatType);
            damage = attacker.rubyBoltSpecial ? getRubyBoltDamage(attacker, defender) : Misc.random(maximumDamage);
            double roll = rand.nextDouble();
            boolean isAccurate = isMaxHitDummy || attacker.rubyBoltSpecial || maximumAccuracy >= roll;

            if (defender.isNPC()) {
                if (defender.asNPC().getNpcId() == Npcs.MAX_DUMMY)
                    isAccurate = true;
            }

            if (RangeData.wearingCrossbow(attacker) && RangeData.wearingBolt(attacker)) {
                Optional<DamageBoostingEffect> boltEffect = RangeData.getBoltEffect(attacker);
                if (boltEffect.isPresent()) {
                    maximumDamage *= 1.0 + boltEffect.get().getMaxHitBoost(attacker, defender);
                    if (attacker.usingZaryteSpec) {
                        maximumDamage *= .10;
                    }
                    boltEffect.get().execute(attacker, defender, new Damage(damage));
                }
            }

            // Dark Bow damage modifiers
            if (attacker.weaponUsedOnAttack == 11235 || attacker.weaponUsedOnAttack == 12765 || attacker.weaponUsedOnAttack == 12766 || attacker.weaponUsedOnAttack == 12767
                    || attacker.weaponUsedOnAttack == 12768 || attacker.bowSpecShot == 1 || attacker.weaponUsedOnAttack == 28922 || attacker.weaponUsedOnAttack == 28919) {

                int extraDamage = specialAccuracy == 1.5 ? 8 : 5;

                maximumDamage += extraDamage;
                damage2 = isMaxHitDummy ? maximumDamage : Misc.random(maximumDamage) + extraDamage;

                if (specialAccuracy == 1.5) {
                    maximumDamage = Math.min(maximumDamage, 48);
                    damage2 = Math.min(damage2, 48);
                }

                boolean isAccurate2 = isMaxHitDummy || maximumAccuracy >= rand.nextDouble();
                if (!isAccurate2 && !attacker.ignoreDefence) {
                    damage2 = 0;
                }
            }

            afterDamageCalculated(combatType, isAccurate);

            if (attacker.isPrintAttackStats() && !applyingMultiHitAttack) {
                String hitPercentage = String.format("%.2f", maximumAccuracy * 100.0);
                attacker.sendMessage("p->e Ranged"
                        + ", Hit%: " + (attacker.ignoreDefence ? 100 : hitPercentage) + "%"
                        + ", Max: " + maximumDamage + "/" + maximumDamage
                        + ", isAccurate: " + isAccurate);
                attacker.sendMessage("Rolled a " + String.format("%.2f", roll * 100));
            }

            if (!isAccurate && !attacker.ignoreDefence) {
                damage = 0;
                success = false;
            }

            if (damage > 0) {
                // Guthan's Armour effect
                if (Misc.trueRand(4) == 1) {
                    boolean guthanGfxFlag = false;
                    if (GuthanEffect.INSTANCE.canUseEffect(attacker)) {
                        guthanGfxFlag = true;
                        GuthanEffect.INSTANCE.useEffect(attacker, defender, new Damage(damage));
                    } else if (EquipmentSet.GUTHAN.isWearing(attacker) || attacker.playerEquipmentCosmetic[Player.playerAura] == 10559 && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 2);
                    } else if (attacker.getItems().isWearingItem(13372) && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 3);
                    }
                    if (guthanGfxFlag) {
                        defender.startGraphic(new Graphic(399));
                    }
                }
            }

            if (attacker.weaponUsedOnAttack == 10501 && defender.isPlayer()) {
                RangeData.fireProjectilePlayer(attacker, (Player) defender, 50, 70, 861, 30, 12, 55, 10);
                defender.startGraphic(new Graphic(862, 62, Graphic.GraphicHeight.MIDDLE));
            } else if (attacker.weaponUsedOnAttack == 10501 && defender.isNPC()) {
                RangeData.fireProjectileNpc(attacker, (NPC) defender, 50, 70, 861, 30, 12, 55, 10);
                defender.startGraphic(new Graphic(862, 62, Graphic.GraphicHeight.MIDDLE));
            }


            if (defender.getHealth().getCurrentHealth() - damage < 0) {
                damage = defender.getHealth().getCurrentHealth();
            }

            if (damage2 > 0) {
                if (damage == defender.getHealth().getCurrentHealth() && defender.getHealth().getCurrentHealth() - damage2 > 0) {
                    damage2 = 0;
                }
            }
            if (defender.getHealth().getCurrentHealth() - damage - damage2 < 0) {
                damage2 = defender.getHealth().getCurrentHealth() - damage;
            }
            if (damage < 0)
                damage = 0;
            if (damage2 < 0 && damage2 != -1)
                damage2 = 0;
            hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
            hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;

            if (gainExperience) {
                addCombatXP(CombatType.RANGE, damage + Math.max(damage2, 0));
            }
            boolean hasDarkHealerVersion = attacker.petSummonId == 30118 || attacker.petSummonId == 30122;
            int healerChance = 10;
            if (damage > 0 && attacker.hasFollower && (attacker.petSummonId == 30018 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122 || hasDarkHealerVersion) && Misc.random(healerChance) == 1) {
                attacker.getHealth().increase(damage / 3);
                if (attacker.playerLevel[3] > attacker.getPA().getLevelForXP(attacker.playerXP[3])) {
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                }
                attacker.getPA().refreshSkill(3);

            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.HEAL_TO_MAX) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                    attacker.getPA().refreshSkill(3);
                }
            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.RESTORE_FULL_PRAYER) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                    attacker.getPA().refreshSkill(5);
                }
            }
            boolean hasDarkPrayerVersion = attacker.petSummonId == 30119 || attacker.petSummonId == 30122;
            int prayerChance = 10;
            if (damage > 0 && attacker.hasFollower && (attacker.petSummonId == 30019 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122 || hasDarkPrayerVersion) && Misc.random(prayerChance) == 1) {
                int halfDamage = (damage / 3);
                attacker.playerLevel[5] += (halfDamage);
                if (attacker.playerLevel[5] > attacker.getPA().getLevelForXP(attacker.playerXP[5])) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                }
                attacker.getPA().refreshSkill(5);
            }
            dropArrows();

            /**
             * Magic attack style
             */
        } else if (combatType.equals(CombatType.MAGE)) {
            double defenceMultiplier = 1.0;
            double specialAccuracy = 1.0;
            double specialDamageBoost = 1.0;
            double specialPassiveMultiplier = 1.0;
            if (special != null) {
                specialAccuracy = special.getAccuracy();
                specialDamageBoost = special.getDamageModifier();
            }

            maximumAccuracy = MagicCombatFormula.STANDARD.getAccuracy(attacker, defender, specialAccuracy,
                    defenceMultiplier);
            maximumDamage = MagicCombatFormula.STANDARD.getMaxHit(attacker, defender, specialDamageBoost,
                    specialPassiveMultiplier);

            beforeDamageCalculated(combatType);
            damage = Misc.random(maximumDamage);

            boolean isAccurate = isMaxHitDummy || maximumAccuracy >= rand.nextDouble();

            afterDamageCalculated(combatType, isAccurate);

            if (attacker.bonusDmg) {
                damage *= 1.20;
            }

            if (attacker.dailyDamage > 0) {
                damage *= 1.10;
            }

            if (attacker.EliteCentBoost > 0 && !attacker.getPosition().inWild()) {
                damage *= 2;
            }
            if (attacker.isPrintAttackStats() && !applyingMultiHitAttack) {
                String hitPercentage = String.format("%.2f", maximumAccuracy * 100.0);
                attacker.sendMessage("p->e Magic"
                        + ", Hit%: " + (attacker.ignoreDefence ? 100 : hitPercentage) + "%"
                        + ", Max: " + maximumDamage + "/" + maximumDamage
                        + ", isAccurate: " + isAccurate);
            }


            if (!isAccurate) {
                damage = 0;
                success = false;
                attacker.getPA().sendSound(227, SoundType.SOUND);
            }

            if (damage > 0) {
                // Guthan's Armour effect
                if (Misc.trueRand(4) == 1) {
                    boolean guthanGfxFlag = false;
                    if (attacker.playerEquipmentCosmetic[Player.playerAura] == 10559 && !attacker.getArboContainer().inArbo() && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 2);
                    } else if (attacker.getItems().isWearingItem(13372) && !defender.isPlayer()) {
                        guthanGfxFlag = true;
                        attacker.getHealth().increase(damage / 3);
                    }
                    if (guthanGfxFlag) {
                        defender.startGraphic(new Graphic(399));
                    }
                }
            }

            if (defender.getHealth().getCurrentHealth() - damage < 0) {
                damage = defender.getHealth().getCurrentHealth();
            }

            DamageEffect tsotdEffect = new ToxicStaffOfTheDeadEffect();
            if (tsotdEffect.isExecutable(attacker)) {
                tsotdEffect.execute(attacker, defender, new Damage(6));
            }

            hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;

            if (gainExperience) {
                addCombatXP(CombatType.MAGE, damage + Math.max(damage2, 0));
            }
            boolean hasDarkHealerVersion = attacker.petSummonId == 30118 || attacker.petSummonId == 30122;
            int healerChance = 10;
            if (damage > 0 && attacker.hasFollower && (attacker.petSummonId == 30018 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122 || hasDarkHealerVersion) && Misc.random(healerChance) == 1) {
                attacker.getHealth().increase(damage / 3);
                if (attacker.playerLevel[3] > attacker.getPA().getLevelForXP(attacker.playerXP[3])) {
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                }
                attacker.getPA().refreshSkill(3);

            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.HEAL_TO_MAX) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.playerLevel[3] = attacker.getPA().getLevelForXP(attacker.playerXP[3]);
                    attacker.getPA().refreshSkill(3);
                }
            }
            if (PrestigePerks.hasRelic(attacker, PrestigePerks.RESTORE_FULL_PRAYER) && attacker.wildLevel <= 0 &&
                    (!Boundary.isIn(attacker, Boundary.OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST_AREA)
                            && !Boundary.isIn(attacker, Boundary.FOREST_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SNOW_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.ROCK_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.BOUNTY_HUNTER_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.FALLY_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.LUMBRIDGE_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.SWAMP_OUTLAST)
                            && !Boundary.isIn(attacker, Boundary.WG_Boundary))) {
                if (Misc.isLucky(10)) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                    attacker.getPA().refreshSkill(5);
                }
            }
            boolean hasDarkPrayerVersion = attacker.petSummonId == 30119 || attacker.petSummonId == 30122;
            int prayerChance = 10;
            if (damage > 0 && attacker.hasFollower && (attacker.petSummonId == 30019 || attacker.petSummonId == 30022 || attacker.petSummonId == 30122 || hasDarkPrayerVersion) && Misc.random(prayerChance) == 1) {
                int halfDamage = (damage / 3);
                attacker.playerLevel[5] += (halfDamage);
                if (attacker.playerLevel[5] > attacker.getPA().getLevelForXP(attacker.playerXP[5])) {
                    attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
                }
                attacker.getPA().refreshSkill(5);
            }
            doMagicEffects();
        }

        DamageEffect venomEffect = new ToxicBlowpipeEffect();
        if (venomEffect.isExecutable(attacker)) {
            venomEffect.execute(attacker, defender, new Damage(6));
        }

        attacker.attackTimer = attacker.attacking.getAttackDelay() + (Spores.isInfected(attacker) ? 1 : 0);


        if (defender != null && defender.isNPC()) {

            NPC n = (NPC) defender;

            if (n.getNpcId() == BryophytaNPC.GROWTHLING) {
                if (damage >= n.getHealth().getCurrentHealth()) {
                    damage = n.getHealth().getCurrentHealth() - 1;
                }
            }
        }


        int delay = attacker.hitDelay;

        if (combatType.equals(CombatType.MAGE)) {
            int distanceToTarget = (int) attacker.getDistance(defender.getX(), defender.getY());
            int delayToHit = (int) (4 + Math.floor((double) (1 + distanceToTarget) / 3));
            if (attacker.playerEquipment[Player.playerWeapon] == 33149) {
                delayToHit = 4;
            }
            if (attacker.playerEquipment[Player.playerWeapon] == 33433) {
                delayToHit = 3;
            }
            if (attacker.playerEquipment[Player.playerWeapon] == 33205) {
                delayToHit = 3;
            }
            delay = delayToHit;
        }

        Damage hit1 = new Damage(defender, damage, delay, attacker.playerEquipment, hitmark1, combatType,
                attacker.attacking.getRangedWeaponType(), special, success);
        attacker.getDamageQueue().add(hit1);

        if (special != null) {
            special.activate(attacker, defender, hit1);
        }

        if (damage2 > -1 || usingSythe && defender.getEntitySize() > 1) {
            attacker.getDamageQueue().add(new Damage(defender, usingSythe ? damage2 : Math.max(0, damage2), delay, attacker.playerEquipment,
                    hitmark2, combatType));
        }

        if (damage3 > -1 || usingSythe && defender.getEntitySize() > 1) {
            attacker.getDamageQueue().add(new Damage(defender, usingSythe ? damage3 : Math.max(0, damage3), delay + 1,
                    attacker.playerEquipment, hitmark3, combatType));
        }

        int totalDamage = damage + Math.max(0, damage2) + Math.max(0, damage3);

        if (Boundary.isIn(attacker, Boundary.XERIC)) {
            attacker.xericDamage += totalDamage;
        }

        if (Boundary.isIn(attacker, PestControl.GAME_BOUNDARY)) {
            attacker.pestControlDamage += totalDamage;
        }

        if (!(special instanceof VolatileNightmareStaff)) {
            if (!applyingMultiHitAttack && usingMultiAttack(combatType) && !attacker.getItems().isWearingItem(27610)) {
                List<Entity> multiHitEntities = getMultiHitEntities(MeleeData.usingSytheOfVitur(attacker));
                if (attacker.isPrintAttackStats()) {
                    attacker.sendMessage("Using multi-attack, " + multiHitEntities.size() + " possible targets.");
                }
                multiHitEntities.forEach(entity -> {
                    if (defender.isNPC()) {
                        if (entity.isPlayer()) {
                            Player target = entity.asPlayer();
                            if (!Boundary.isIn(attacker, Boundary.DUEL_ARENA) && !TourneyManager.getSingleton().isInArena(attacker) && !WGManager.getSingleton().isInArena(attacker)) {
                                if (!attacker.attackedPlayers.contains(target.getIndex()) && !PlayerHandler.players[target.getIndex()].attackedPlayers.contains(attacker.getIndex())) {
                                    if (!target.getMode().equals(Mode.forType(ModeType.WILDYMAN)) && !target.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
                                        attacker.attackedPlayers.add(target.getIndex());
                                        attacker.isSkulled = true;
                                        attacker.skullTimer = Configuration.SKULL_TIMER;
                                        attacker.headIconPk = 0;
                                        attacker.getPA().requestUpdates();
                                    }
                                }
                            }
                        }
                    }
                    getHitEntity(attacker, entity).playerHitEntity(combatType, special, true);
                });
            } else if (!applyingMultiHitAttack && usingMultiAttack(combatType) && defender.isNPC()) {
                List<Entity> multiHitEntities = getMultiHitEntities(MeleeData.usingSytheOfVitur(attacker));
                if (attacker.isPrintAttackStats()) {
                    attacker.sendMessage("Using multi-attack, " + multiHitEntities.size() + " possible targets.");
                }
                if (multiHitEntities.size() > 0) {
                    Entity randEnty = multiHitEntities.get(Misc.trueRand(multiHitEntities.size()));
                    if (defender.isNPC()) {
                        RangeData.fireProjectileNpc(attacker, (NPC) randEnty, 50, 70, 682, 43, 31, 37, 10);
                        getHitEntity(attacker, randEnty).playerHitEntity(combatType, special, true);
                    }
                }
            }
        }
        if (attacker.getItems().isWearingItem(1729)) {
            attacker.getHealth().setMaximumHealth(200000);
        }
        if (attacker.getItems().isWearingItem(33200)) {
            attacker.prayerPoint += damage / 2;
        }
        if (attacker.prayerActive[21]) {
            attacker.gfx0(436);
            attacker.heal((int) (damage / 5));
            if (attacker.playerLevel[5] > attacker.getPA().getLevelForXP(attacker.playerXP[5])) {
                attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
            }
            int halfDamage = (int) (damage / 10);
            attacker.playerLevel[5] += (halfDamage);
            if (attacker.playerLevel[5] > attacker.getPA().getLevelForXP(attacker.playerXP[5])) {
                attacker.playerLevel[5] = attacker.getPA().getLevelForXP(attacker.playerXP[5]);
            }
            attacker.getPA().refreshSkill(5);
            attacker.getPA().refreshSkill(5);
            attacker.getPA().refreshSkill(3);
        }
        if (attacker.rubyBoltSpecial)
            attacker.rubyBoltSpecial = false;
    }

    public int getRubyBoltDamage(Player attacker, Entity defender) {
        if (attacker == null || defender == null)
            return 0;

        int attackerHP = attacker.getHealth().getCurrentHealth() / 10;

        if (attackerHP > 100)
            attackerHP = 10;

        attacker.appendDamage(attacker, attackerHP, Hitmark.HIT);

        int defenderHP = defender.getHealth().getCurrentHealth() / 5;

        if (defenderHP > 100)
            defenderHP = 100;

        RangeData.createCombatGraphic(defender, 754, false);

        return defenderHP;
    }

    private void dropArrows() {
        RangedWeaponType type = attacker.attacking.getRangedWeaponType();
        int weaponId = attacker.playerEquipment[Player.playerWeapon];
        int itemId = type == RangedWeaponType.THROWN ? attacker.playerEquipment[Player.playerWeapon] :
                attacker.playerEquipment[Player.playerArrows];
        int slot = type == RangedWeaponType.THROWN ? Player.playerWeapon : Player.playerArrows;
        if (weaponId == Items.CRAWS_BOW) {
            return;
        }
        if (type == RangedWeaponType.NO_ARROWS) {
            return;
        }
        if (attacker.playerEquipment[Player.playerWeapon] == 12926) {
            return;
        }
        if (attacker.playerEquipment[Player.playerWeapon] == 28919) {
            return;
        }
        if (attacker.playerEquipment[Player.playerWeapon] == 28922) {
            return;
        }
        dropArrow(itemId);
        if (type == RangedWeaponType.DOUBLE_SHOT) {
            dropArrow(itemId);
        }
    }

    private void dropArrow(int arrowId) { // TODO delete arrows from player
        if (Boundary.OUTLAST.in(attacker))
            return;
        if (attacker.getItems().isWearingItem(10033) || attacker.getItems().isWearingItem(10034)
                || attacker.getItems().isWearingItem(11959) || attacker.getItems().isWearingItem(22333)) {
            return;
        }

        if (attacker.playerEquipment[Player.playerCape] == 10499 || attacker.getItems().isWearingItem(22109,
                Player.playerCape)
                || attacker.getItems().isWearingItem(33037, Player.playerCape)
                || SkillcapePerks.RANGING.isWearing(attacker) || SkillcapePerks.isWearingMaxCape(attacker)) {
            return;
        }

        if (attacker.playerEquipment[Player.playerWeapon] == 12926) {
            return;
        }
        if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33082) && Misc.random(0, 100) <= 10 && attacker.wildLevel < 0) {
            return;
        }

        int enemyX = defender.getX();
        int enemyY = defender.getY();
        int height = defender.getHeight();
        if (Misc.trueRand(3) == 0) {
            Server.itemHandler.createGroundItem(attacker, arrowId, enemyX, enemyY, height, 1, attacker.getIndex(), false);
        }
    }

    public void addCombatXP(CombatType type, int damage) {
        boolean pvpExperienceDrops = attacker.playerAttackingIndex > 0;

        double standardExperience = damage * 4;
        double hitpointsExperience = damage * 1.33;

        if (pvpExperienceDrops) {
            if (type == CombatType.RANGE) {
                attacker.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.RANGED.getId()));
            } else if (type == CombatType.MAGE) {
                attacker.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.MAGIC.getId()));
            } else {
                attacker.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
            }
        }

        attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.HITPOINTS.getId(), !pvpExperienceDrops);

        if (type == CombatType.MAGE && attacker.autocastingDefensive) {
            attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.MAGIC.getId(), !pvpExperienceDrops);
            attacker.getPA().addSkillXPMultiplied(damage, Skill.DEFENCE.getId(), !pvpExperienceDrops);
        } else if (type == CombatType.MAGE) {
            attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.MAGIC.getId(), !pvpExperienceDrops);
        } else {
            switch (attacker.getCombatConfigs().getWeaponMode().getAttackStyle()) {
                case ACCURATE:
                    if (type == CombatType.MELEE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.ATTACK.getId(),
                                !pvpExperienceDrops);
                    } else if (type == CombatType.RANGE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.RANGED.getId(),
                                !pvpExperienceDrops);
                    }
                    break;
                case AGGRESSIVE:
                    if (type == CombatType.MELEE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.STRENGTH.getId(),
                                !pvpExperienceDrops);
                    } else if (type == CombatType.RANGE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.RANGED.getId(),
                                !pvpExperienceDrops);
                    }
                    break;
                case DEFENSIVE:
                    if (type == CombatType.MELEE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.DEFENCE.getId(),
                                !pvpExperienceDrops);
                    } else if (type == CombatType.RANGE) {
                        attacker.getPA().addSkillXPMultiplied(damage, Skill.DEFENCE.getId(), !pvpExperienceDrops);
                        attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.RANGED.getId(),
                                !pvpExperienceDrops);
                    }
                    break;
                case CONTROLLED:
                    if (type == CombatType.MELEE) {
                        attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.STRENGTH.getId(),
                                !pvpExperienceDrops);
                        attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.ATTACK.getId(),
                                !pvpExperienceDrops);
                        attacker.getPA().addSkillXPMultiplied(hitpointsExperience, Skill.DEFENCE.getId(),
                                !pvpExperienceDrops);
                    } else if (type == CombatType.RANGE) {
                        attacker.getPA().addSkillXPMultiplied(standardExperience, Skill.RANGED.getId(),
                                !pvpExperienceDrops);
                    }
                    break;
            }
        }
    }

    private boolean usingMultiAttack(CombatType combatType) {
               AoeWeapons aoeData = AOESystem.getSingleton().getAOEData(attacker.playerEquipment[Player.playerWeapon]);

        if (attacker.usingSpecial && attacker.getItems().isWearingItem(21902)) {
            return true;
        } else if (combatType == CombatType.MAGE && Arrays.stream(CombatSpellData.MULTI_SPELLS).anyMatch(spell -> spell == CombatSpellData.getSpellId(attacker.getSpellId()))) {
            return true;
        } else if (combatType == CombatType.RANGE && Arrays.stream(RangeData.MULTI_WEAPONS).anyMatch(weapon -> weapon == attacker.playerEquipment[Player.playerWeapon])) {
            return true;
        } else if (MeleeData.usingSytheOfVitur(attacker)) {
            return true;
        } else if (attacker.getItems().isWearingItem(33205) ||attacker.getItems().isWearingItem(33434) || attacker.getItems().isWearingItem(33207)  || attacker.getItems().isWearingItem(33170) || attacker.getItems().isWearingItem(33171)) {
            return true;
        } else if (attacker.getItems().isWearingItem(33433)) {
            return true;
        } else if (attacker.getItems().isWearingItem(27610)) {
            return true;
        } else if (attacker.getItems().hasItemOnOrInventory(33233)) {
            return true;
        }

        return false;
    }

    public List<Entity> getMultiHitEntities(boolean sythe) {
        AoeWeapons aoeData = AOESystem.getSingleton().getAOEData(attacker.playerEquipment[Player.playerWeapon]);

        List<Entity> attackable = Lists.newArrayList();
        Entity[] entities;

        main:
        for (int i = 0; i < 2; i++) {
            entities = i == 0 ? PlayerHandler.players : NPCHandler.npcs;

            for (Entity entity : entities) {
                if (entity != null) {
                    if (!entity.equals(attacker) && !entity.equals(defender) && entity.getInstance() == defender.getInstance()
                            && !entity.isDead && entity.isRegistered()
                            && entity.getHeight() == defender.getHeight()) {
                        if (sythe) {
                            if (attacker.getItems().isWearingItem(33203) || attacker.getItems().isWearingItem(33431)) {
                                if (entity.distance(defender.getPosition()) <= 13 && attacker.attacking.attackEntityCheck(entity, false)) {
                                    attackable.add(entity);
                                    if (attackable.size() >= 9)
                                        break main;
                                }
                            } else {
                                if (entity.distance(defender.getPosition()) <= 7 && attacker.distance(entity.getPosition()) <= 7 && attacker.attacking.attackEntityCheck(entity, false)) {
                                    attackable.add(entity);
                                    if (attackable.size() >= 6)
                                        break main;
                                }
                            }
                        } else if (attacker.getItems().isWearingItem(33205) || attacker.getItems().isWearingItem(33433) || attacker.getItems().isWearingItem(33174) || attacker.getItems().isWearingItem(30350) ||attacker.getItems().isWearingItem(33205) || attacker.getItems().isWearingItem(33433) || attacker.getItems().isWearingItem(33174) || attacker.getItems().isWearingItem(33434) || attacker.getItems().isWearingItem(33207) || attacker.getItems().isWearingItem(9999)|| attacker.getItems().isWearingItem(33173)) {
                            if (entity.distance(defender.getPosition()) <= 9 && attacker.attacking.attackEntityCheck(entity, false)) {
                                attackable.add(entity);
                                if (attackable.size() >= 8)
                                    break main;
                            }
                        } else if (attacker.getItems().isWearingItem(27610)) {
                            if (entity.distance(defender.getPosition()) <= 7 && attacker.attacking.attackEntityCheck(entity, false)) {
                                attackable.add(entity);
                                if (attackable.size() >= 6)
                                    break main;
                            }
                        } else if (attacker.getItems().hasItemOnOrInventory(33233)) {
                            if (entity.distance(defender.getPosition()) <= 3 && attacker.attacking.attackEntityCheck(entity, false)) {
                                attackable.add(entity);
                                if (attackable.size() >= 9)
                                    break main;
                            }
                        } else {
                            if (entity.distance(defender.getPosition()) < 1.5 && attacker.attacking.attackEntityCheck(entity, false)) {
                                attackable.add(entity);
                                if (attackable.size() >= 9)
                                    break main;
                            }
                        }
                    }
                }
            }
        }

        return attackable;
    }

    private void doMagicEffects() {
        if (attacker.getSpellId() > -1) {
            int freezeDelay = CombatSpellData.getFreezeTime(attacker);

            // This feature was removed from OSRS
            /*if (defender.isPlayer() && defender.asPlayer().protectingMagic()) {
                switch (CombatSpellData.MAGIC_SPELLS[attacker.getSpellId()][0]) {
                    case 1592://entangle
                    case 1572://bind
                    case 1582://snare
                        freezeDelay /= 2;
                        break;
                }
            }*/
            if (damage > 0) {
                SoundData soundData = SoundData.forId(attacker.getSpellId());
                if (soundData != null) {
                    attacker.getPA().sendSound(soundData.forIdhit(), SoundType.AREA_SOUND);
                }
            }

            if (freezeDelay > 0 && defender.freezeTimer <= (defender.isNPC() ? 0 : -3) && success && defender.isFreezable()) {
                defender.freezeTimer = freezeDelay;
                defender.resetWalkingQueue();
                defender.frozenBy = EntityReference.getReference(attacker);
                if (defender.isPlayer()) {
                    Player defenderPlayer = defender.asPlayer();
                    defenderPlayer.sendMessage("You have been frozen.");
                    defenderPlayer.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS,
                            600 * freezeDelay);
                }
            }
        }
    }
}
