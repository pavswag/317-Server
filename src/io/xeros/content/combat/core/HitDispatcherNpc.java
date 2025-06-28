package io.xeros.content.combat.core;

import io.xeros.content.bosses.Hunllef;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.AhrimEffect;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.KarilEffect;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.VeracsEffect;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.weapon.WeaponInterface;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.questing.hftd.DagannothMother;
import io.xeros.content.seasons.HalloweenBoss;
import io.xeros.content.skills.mining.Pickaxe;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.Spell;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HitDispatcherNpc extends HitDispatcher {

    public HitDispatcherNpc(Player attacker, Entity defender) {
        super(attacker, defender);
    }

    @Override
    public void beforeDamageCalculated(CombatType type) {
        NPC npc = (NPC) defender;
        attacker.getBossTimers().track(npc);
        StyleWarning.styleWarning(attacker, defender, type);
        int npcId = npc.getNpcId();

        if (npc.isInvisible()) {
            return;
        }

        if (type == CombatType.MELEE) {
            if (PvpWeapons.activateEffect(attacker, Items.VIGGORAS_CHAINMACE))
                PvpWeapons.degradeWeaponAfterCombat(attacker, false);
        } else if (type == CombatType.RANGE) {
            // Craws bow
            if (PvpWeapons.activateEffect(attacker, Items.CRAWS_BOW))
                PvpWeapons.degradeWeaponAfterCombat(attacker, false);

        } else if (type == CombatType.MAGE) {
            if (attacker.getSpellId() > -1) {
                int spellId = CombatSpellData.MAGIC_SPELLS[attacker.getSpellId()][0];
                int shield = attacker.playerEquipment[Player.playerShield];
                int staff = attacker.playerEquipment[Player.playerWeapon];
                Spell spell = Spell.forId(spellId);

                if (PvpWeapons.activateEffect(attacker, Items.THAMMARONS_SCEPTRE))
                    PvpWeapons.degradeWeaponAfterCombat(attacker, true);

            }
        }

        if (npcId == Npcs.CORPOREAL_BEAST) {
            WeaponInterface weaponInterface = attacker.getCombatConfigs().getWeaponData().getWeaponInterface();
            if (!(type == CombatType.MELEE && (weaponInterface == WeaponInterface.SPEAR || weaponInterface == WeaponInterface.HALBERD)) && attacker.playerEquipment[Player.playerWeapon] != 33175 && type != CombatType.MAGE && attacker.playerEquipment[Player.playerWeapon] != 33204) {
                maximumDamage *= 0.33;
                attacker.sendMessage("@red@Corporeal beast can only be effectively damaged with spears or halberds!",
                        TimeUnit.MINUTES.toMillis(10));
            }


            if (attacker.playerEquipment[Player.playerWeapon] == 33204) {
                maximumDamage *= 3;
            }

            if (attacker.playerEquipment[Player.playerWeapon] == 33175) {
                maximumDamage *= 2;
            }
        }

/*        if (npcId == 655 && !Boundary.isIn(attacker, Boundary.AFK_ZONE_BOSS)) {
            maximumDamage = 0;

            attacker.sendMessage("@red@You can't deal damage unless you're inside the area for the afk boss!",
                    TimeUnit.MINUTES.toMillis(10));
        }*/

        if (npcId == 7623) {
            maximumDamage = HalloweenBoss.handleDamage(npc, attacker, type, maximumDamage);
        }

        if (npc.isGodmode) {
            maximumDamage = 0;
            attacker.sendMessage("This npc is now immune to all attacks!", TimeUnit.MINUTES.toMillis(10));
        }

        if (attacker.getArboContainer().inArbo()) {
            if (npcId == 5630) {//Giant Snails -> MELEE Only
                if (type == CombatType.MAGE || type == CombatType.RANGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Gaint Snail's are immune to Mage/Range damage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }

            if (npcId == 6477) { //Mutant Tarn, Can't attack until minions are dead.
                if (attacker.getArboContainer().inArbo()) {
                    List<NPC> minion = Arrays.asList(NPCHandler.npcs);
                    if (minion.stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == 6474 && !n.isDead() && n.getHealth().getCurrentHealth() > 0)) {
                        maximumDamage = 0;
                        attacker.sendMessage("@red@You can't attack tarn until his puppies are dead!",
                                TimeUnit.MINUTES.toMillis(10));
                    }
                }
            }

            if (npcId == 6474) {//Terror dog MELEE or Mage
                if (type == CombatType.RANGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Terror dog's are immune to range damage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 7527) {
                if (type == CombatType.MAGE || type == CombatType.RANGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Vanguard is immune to Range/Mage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 7528) {
                if (type == CombatType.MELEE || type == CombatType.RANGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Vanguard is immune to Range/Melee.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 7529) {
                if (type == CombatType.MAGE || type == CombatType.MELEE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Vanguard is immune to Melee/Mage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 12821) {
                if (type == CombatType.RANGE || type == CombatType.MAGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Sol Heredit is immune to Range/Mage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 7529) {
                if (type == CombatType.RANGE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Tekton is immune to Range.",
                            TimeUnit.MINUTES.toMillis(10));
                }
                if (type == CombatType.MAGE) {
                    maximumDamage = (int) (maximumDamage * 0.2);
                    attacker.sendMessage("@red@Tekton's shell protect's him from magic.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }
            if (npcId == 1101 || npcId == 7594) { // Giant Sea Snake && Cave Snake = Range Only
                if (type == CombatType.MAGE || type == CombatType.MELEE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Snakes are immune to Melee/Mage damage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }

            if (npcId == 7804) { // Tar Monster Mage Or Range
                if (type == CombatType.MELEE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Tar Monster is immune to Melee damage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }

            if (npcId == 2948) { // Nail Beast MAGE or MELEE
                if (type == CombatType.MELEE) {
                    maximumDamage = 0;
                    attacker.sendMessage("@red@Nail Beast's are immune to melee damage.",
                            TimeUnit.MINUTES.toMillis(10));
                }
            }


        }

        if (npcId == 2847  && npc.spawnedBy == 0) {
            if (!Boundary.isIn(attacker, Boundary.XMAS_CASTLE)) {
                maximumDamage = 0;
                attacker.sendMessage("@red@You can't attack the evil yeti without being inside the castle walls!", TimeUnit.MINUTES.toMillis(10));
            }
            if (type != CombatType.MAGE) {
                maximumDamage = 0;
                attacker.sendMessage("@red@Evil yeti is only weak to magic damage", TimeUnit.MINUTES.toMillis(10));
            }
        }

        if (npcId == 7585) {
            if (type != CombatType.MAGE) {
                maximumDamage *= 0.45;
            }
        }

        if (npcId == 5371) {
            if (type != CombatType.RANGE) {
                maximumDamage *= 0.45;
            }
        }

        if (npcId == 7544) {
            if (type == CombatType.RANGE) {
                maximumDamage *= 0.45;
            }
            if (type == CombatType.MAGE) {
                maximumDamage *= 0.45;
            }
        }

        if (npcId == 1226) {
            if (type != CombatType.MELEE)  {
                maximumDamage = 0;
            }
        }

        // Demonic gorilla
        if (type == CombatType.MELEE && npcId == Npcs.DEMONIC_GORILLA
                || type == CombatType.RANGE && npcId == Npcs.DEMONIC_GORILLA_2
                || type == CombatType.MAGE && npcId == Npcs.DEMONIC_GORILLA_3) {
            maximumDamage = 0;
            maximumAccuracy = 0;
        }

        switch (npcId) {

            /**
             * Air based spells only
             */
            case 6373:
            case DagannothMother.AIR_PHASE:
                if (!CombatSpellData.airSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            /**
             * Water based spells only
             */
            case DagannothMother.WATER_PHASE:
            case 6375:
                if (!CombatSpellData.waterSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            /**
             * Fire based spells only
             */
            case DagannothMother.FIRE_PHASE:
            case 6376:
                if (!CombatSpellData.fireSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            /**
             * Earth based spells only
             */
            case DagannothMother.EARTH_PHASE:
            case 6378:
                if (!CombatSpellData.earthSpells(attacker)) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            case 2847:
                if (npc.spawnedBy == 0) {
                    if (!CombatSpellData.fireSpells(attacker)) {
                        maximumDamage = (maximumDamage/3);
                        attacker.sendMessage("@red@Evil Yeti is weak to Fire damage", TimeUnit.MINUTES.toMillis(10));
                    } else {
                        maximumDamage = (maximumDamage + (maximumDamage/3));
                    }
                }
                break;
            /**
             * Melee only
             */
            case DagannothMother.MELEE_PHASE:
            case 6374:
                if (!attacker.usingMelee) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;

            /**
             * Range only
             */
            case DagannothMother.RANGE_PHASE:
            case 6377:
                if (!attacker.usingBow && !attacker.usingOtherRangeWeapons && !attacker.usingBallista) {
                    maximumDamage = 0;
                    maximumAccuracy = 0;
                }
                break;
        }

        if (npcId == Hunllef.MELEE_PROTECT && type == CombatType.MELEE
                || npcId == Hunllef.MAGE_PROTECT && type == CombatType.MAGE
                || npcId == Hunllef.RANGED_PROTECT && type == CombatType.RANGE) {
            maximumDamage = 0;
            maximumAccuracy = 0;
        }
    }

    /**
     * Anything that blocks damage totally should go in the method above and set
     * {@link HitDispatcher#maximumDamage} to zero.
     */
    @Override
    public void afterDamageCalculated(CombatType type, boolean successfulHit) {
        NPC defender = this.defender.asNPC();

        if (successfulHit) {
            if (AhrimEffect.INSTANCE.canUseEffect(attacker)) {
                AhrimEffect.INSTANCE.useEffect(attacker, defender, null);
            } else if (KarilEffect.INSTANCE.canUseEffect(attacker)) {
                KarilEffect.INSTANCE.useEffect(attacker, defender, new Damage(damage));
            } else if (VeracsEffect.INSTANCE.canUseEffect(attacker)) {
                VeracsEffect.INSTANCE.useEffect(attacker, null, null);
            }
        }

        switch (type) {
            case MELEE:
                if (defender.getNpcId() == 6600) {
                    Pickaxe pickaxe = Pickaxe.getBestPickaxe(attacker);
                    if (pickaxe != null && attacker.getItems().isWearingItem(pickaxe.getItemId())) {
                        damage += Misc.random(pickaxe.getPriority() * 4);
                    }
                }

                switch (defender.getNpcId()) {
                    case 7930:
                    case 7931:
                    case 7932:
                    case 7933:
                    case 7934:
                    case 7935:
                    case 7936:
                    case 7937:
                    case 7938:
                    case 7939:
                    case 7940:
                        if (attacker.playerEquipment[Player.playerHands] == 21816 && attacker.braceletEtherCount <= 0) {
                            attacker.getItems().equipItem(21817, 1, 9);
                        }
                        break;
                    case 7413://combat dummy
                        this.maximumAccuracy = 100.0;
                        damage = maximumDamage;
                        break;


                    case 2266:
                    case 2267:
                        damage = 0;
                        break;

                    case 965:
                        if (!EquipmentSet.VERAC.isWearingBarrows(attacker)) {
                            damage = 0;
                        }
                        break;
                }

                if (defender.getNpcId() == 5666) {
                    damage = damage / 4;
                    if (damage < 0) {
                        damage = 0;
                    }
                }
                break;
            case RANGE:
                switch (defender.getNpcId()) {
                    case 7413://combat dummy
                        damage = maximumDamage;
                        maximumAccuracy = 100.0;
                        break;

                    case 2265:
                    case 2267:
                        damage = 0;
                        break;
                    case 8781:
                        int health1 = defender.getHealth().getCurrentHealth();
                        if (health1 > 500 && health1 < 600) {
                            defender.forceChat("YOU'LL NEVER TAKE ME DOWN!!");
                            defender.gfx0(1028);
                            defender.gfx0(1009);
                            attacker.attacking.reset();
                        }
                        break;
                    case 5890:
                        if (!attacker.getSlayer().getTask().isPresent()) {
                            return;
                        }
                        if (!attacker.getSlayer().getTask().isPresent() && !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon") && !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal sire")) {
                            attacker.sendMessage("The sire Fdoes not seem interested.");
                            attacker.attacking.reset();
                            return;
                        }
                        int health = defender.getHealth().getCurrentHealth();
                        if (health > 329) {
                            if (!CombatSpellData.shadowSpells(attacker)) {
                                attacker.sendMessage("This would not be effective, I should try shadow spells.");
                                attacker.attacking.reset();
                            }
                        }
                        break;

                }
                break;
            case MAGE:
                switch (defender.getNpcId()) {
                    case 7413://combat dummy
                        damage = maximumDamage;
                        maximumAccuracy = 100.0;
                        break;

                    case 5890:
                        if (!attacker.getSlayer().getTask().isPresent()) {
                            return;
                        }
                        if (!attacker.getSlayer().getTask().isPresent() && !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon") && !attacker.getSlayer().getTask().get().getPrimaryName().equals("abyssal sire")) {
                            attacker.sendMessage("The sire does not seem interested.");
                            attacker.attacking.reset();
                            return;
                        }
                        int health = defender.getHealth().getCurrentHealth();
                        if (health > 329) {
                            if (!CombatSpellData.shadowSpells(attacker)) {
                                attacker.sendMessage("This would not be effective, I should try shadow spells.");
                                attacker.attacking.reset();
                            }
                        }
                        break;
                    case 2265:
                    case 2266:
                        damage = 0;
                        break;
                }
                break;
            default:
                break;
        }

        if (attacker.getSkotizo() != null && defender.getNpcId() == Skotizo.SKOTIZO_ID) {
            damage = attacker.getSkotizo().calculateSkotizoHit(attacker, damage);
        }

        if (!defender.canBeDamaged(attacker)) {
            damage = 0;
            if (damage2 > 0) {
                damage2 = 0;
            }
        }
    }

}
