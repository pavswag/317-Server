package io.xeros.content.combat.core;

import io.xeros.content.activityboss.Groot;
import io.xeros.content.afkzone.AfkBoss;
import io.xeros.content.bosses.*;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.hydra.AlchemicalHydra;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.commands.admin.dboss;
import io.xeros.content.commands.moderator.vboss;
import io.xeros.content.globalboss.*;
import io.xeros.content.seasons.ChristmasBoss;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HitExecutorNpc extends HitExecutor {

    public HitExecutorNpc(Player c, Entity defender, Damage damage) {
        super(c, defender, damage);
    }

    @Override
    public void onHit() {
        NPC npc = defender.asNPC();
        npc.addDamageTaken(attacker, damage.getAmount());
        AlchemicalHydra.negateDamage(attacker, npc, damage);
        /**
         * Damage applied and maybe changed
         */
        if (npc.getNpcId() == 239) {
            if (KBD.damageCount.containsKey(attacker)) {
                KBD.damageCount.put(attacker, KBD.damageCount.get(attacker) + damage.getAmount());
            } else {
                KBD.damageCount.put(attacker, damage.getAmount());
            }
        } else if (npc.getNpcId() == 655) {
            if (AfkBoss.damageCount.containsKey(attacker)) {
                AfkBoss.damageCount.put(attacker, AfkBoss.damageCount.get(attacker) + damage.getAmount());
            } else {
                AfkBoss.damageCount.put(attacker, damage.getAmount());
            }
        } else if (npc.getNpcId() == 965) {
            if (KQ.damageCount.containsKey(attacker)) {
                KQ.damageCount.put(attacker, KQ.damageCount.get(attacker) + damage.getAmount());
            } else {
                KQ.damageCount.put(attacker, damage.getAmount());
            }
        } else if (npc.getNpcId() == 8713) {
            if (Sarachnis.damageCount.containsKey(attacker)) {
                Sarachnis.damageCount.put(attacker, Sarachnis.damageCount.get(attacker) + damage.getAmount());
            } else {
                Sarachnis.damageCount.put(attacker, damage.getAmount());
            }
        } else if (npc.getNpcId() == 11278) {
            if (NEX.damageCount.containsKey(attacker)) {
                NEX.damageCount.put(attacker, NEX.damageCount.get(attacker) + damage.getAmount());
            } else {
                NEX.damageCount.put(attacker, damage.getAmount());
            }
        }

        switch (npc.getNpcId()) {
            case 7413:
                npc.getHealth().setCurrentHealth(500000);
                break;
            /**
             * Corporeal Beast
             */
            case 320:
                if (!Boundary.isIn(attacker, Boundary.CORPOREAL_BEAST_LAIR)) {
                    attacker.attacking.reset();
                    attacker.sendMessage("You cannot do this from here.");
                    return;
                }
                break;

            /**
             * No melee
             */
            case 2042: // Zulrah
            case 2043:
            case 2044:
                if (attacker.usingMelee) {
                    damage.setAmount(0);//oh nvm yeah i see
                }
                break;

            /**
             * Magic only
             */
            case 1610:
            case 1611:
            case 1612:
                if (!attacker.usingMagic) {
                    damage.setAmount(0);
                }
                break;
            case 5126:
                if (!Boundary.isIn(attacker, new Boundary(3712, 3968, 3775, 4031))) {
                    attacker.attacking.reset();
                    attacker.sendMessage("You cannot do this from here.");
                    return;
                }
                vboss.targets.add(attacker);

                if (vboss.damageCount.containsKey(attacker)) {
                    vboss.damageCount.put(attacker, vboss.damageCount.get(attacker) + damage.getAmount());
                } else {
                    vboss.damageCount.put(attacker, damage.getAmount());
                }
                break;
            case 4923:
                if (!Boundary.isIn(attacker, Boundary.GROOT_BOSS)) {
                    attacker.attacking.reset();
                    attacker.sendMessage("You cannot do this from here.");
                    return;
                }
                Groot.targets.add(attacker);

                if (Groot.damageCount.containsKey(attacker)) {
                    Groot.damageCount.put(attacker, Groot.damageCount.get(attacker) + damage.getAmount());
                } else {
                    Groot.damageCount.put(attacker, damage.getAmount());
                }
                break;
            case 5169:
                if (Durial321.damageCount.containsKey(attacker)) {
                    Durial321.damageCount.put(attacker, Durial321.damageCount.get(attacker) + damage.getAmount());
                } else {
                    Durial321.damageCount.put(attacker, damage.getAmount());
                }
                break;
            case 2847:
                if (ChristmasBoss.damageCount.containsKey(attacker)) {
                    ChristmasBoss.damageCount.put(attacker, ChristmasBoss.damageCount.get(attacker) + damage.getAmount());
                } else {
                    ChristmasBoss.damageCount.put(attacker, damage.getAmount());
                }
                break;

            case 8096:
                if (!Boundary.isIn(attacker, new Boundary(3712, 3968, 3775, 4031))) {
                    attacker.attacking.reset();
                    attacker.sendMessage("You cannot do this from here.");
                    return;
                }

                dboss.targets.add(attacker);

                if (dboss.damageCount.containsKey(attacker)) {
                    dboss.damageCount.put(attacker, dboss.damageCount.get(attacker) + damage.getAmount());
                } else {
                    dboss.damageCount.put(attacker, damage.getAmount());
                }
                break;
            case 12821:
                Sol.targets.add(attacker);
                if (Sol.damageCount.containsKey(attacker)) {
                    Sol.damageCount.put(attacker, Sol.damageCount.get(attacker) + damage.getAmount());
                } else {
                    Sol.damageCount.put(attacker, damage.getAmount());
                }
                attacker.getPA().sendConfig(999, Sol.damageCount.getOrDefault(attacker, 0));
                break;
            case 319:
                if (!Boundary.isIn(attacker, Boundary.CORPOREAL_BEAST_LAIR)) {
                    attacker.attacking.reset();
                    attacker.sendMessage("You cannot do this from here.");
                    return;
                }

                CorporealBeast.targets.add(attacker);
                if (CorporealBeast.damageCount.containsKey(attacker)) {
                    CorporealBeast.damageCount.put(attacker, CorporealBeast.damageCount.get(attacker) + damage.getAmount());
                } else {
                    CorporealBeast.damageCount.put(attacker, damage.getAmount());
                }
                attacker.getPA().sendConfig(999, CorporealBeast.damageCount.getOrDefault(attacker, 0));
                break;
            case 7584:
            case 7604: //Skeletal mystic
            case 7605: //Skeletal mystic
            case 7606: //Skeletal mystic
                attacker.setSkeletalMysticDamageCounter(attacker.getSkeletalMysticDamageCounter() + damage.getAmount());
                break;

            case 7544: //Tekton
                attacker.setTektonDamageCounter(attacker.getTektonDamageCounter() + damage.getAmount());
                Tekton.tektonSpecial(attacker);
                break;

            case Hespori.NPC_ID:
                attacker.setHesporiDamageCounter(attacker.getHesporiDamageCounter() + damage.getAmount());
                break;

            case TheUnbearable.NPC_ID: //Sotetseg
                attacker.setGlodDamageCounter(attacker.getGlodDamageCounter() + damage.getAmount());
                NPCHandler.glodAttack = "MAGIC";
                break;

            case FragmentOfSeren.NPC_ID: //Fragement of Seren
                attacker.setIceQueenDamageCounter(attacker.getIceQueenDamageCounter() + damage.getAmount());
                FragmentOfSeren.handleSpecialAttack(attacker);
                break;

            case 6617:
            case 6616:
            case 6615:
                List<NPC> healer = Arrays.asList(NPCHandler.npcs);
                if (Scorpia.stage > 0 && healer.stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == 6617 && !n.isDead() && n.getHealth().getCurrentHealth() > 0)) {
                    NPC scorpia = NPCHandler.getNpc(6615);
                    Damage heal = new Damage(Misc.random(20 + 5)); //was 45 + 5 but heals to much for now
                    if (scorpia != null && scorpia.getHealth().getCurrentHealth() < 150) {
                        scorpia.getHealth().increase(heal.getAmount());
                    }
                }
                break;

            case 3118: //Tz-kek small
                attacker.appendDamage(attacker, 1, Hitmark.HIT);
                break;

/*            case 5862:
                if (!Boundary.isIn(attacker, Boundary.CERB_BOUNDARY2)) {
                    damage.setAmount(0);
                    attacker.sendMessage("@red@You should keep yourself in the middle so you don't get burned.");
                }
                break;*/

            case Skotizo.SKOTIZO_ID:
                if (attacker.getSkotizo() == null) break;
                Skotizo skotizo = attacker.getSkotizo();
                skotizo.skotizoSpecials();
                break;

            case Skotizo.AWAKENED_ALTAR_NORTH:
            case Skotizo.AWAKENED_ALTAR_SOUTH:
            case Skotizo.AWAKENED_ALTAR_WEST:
            case Skotizo.AWAKENED_ALTAR_EAST:
                if (attacker.playerEquipment[Player.playerWeapon] == 19675) {
                    attacker.getSkotizo().arclightEffect(npc);
                    return;
                }
                break;

            case 7144:
            case 7145:
            case 7146:
                int getTransformation = 0;
                attacker.totalGorillaDamage += damage.getAmount();
                if (attacker.totalGorillaDamage > 49) {
                    if (attacker.usingMelee) {
                        getTransformation = 7144;
                    } else if (attacker.usingBow || attacker.usingOtherRangeWeapons || attacker.usingBallista) {
                        getTransformation = 7145;
                    } else if (attacker.usingMagic || attacker.autocasting) {
                        getTransformation = 7146;
                    } else {
                        getTransformation = 7144;
                    }
                    if (damage.getAmount() > 0) { //reset
                        npc.requestTransform(getTransformation);
                        attacker.totalGorillaDamage = 0;
                    }
                }
                break;

            case 9021: //melee
            case 9022: //range
            case 9023: //mage
                int getTransformation2 = 0;
                attacker.totalHunllefDamage += damage.getAmount();

                if (attacker.totalHunllefDamage > 70) {
                    if (attacker.usingMelee) {
                        getTransformation2 = 9021;
                    } else if (attacker.usingBow || attacker.usingOtherRangeWeapons || attacker.usingBallista) {
                        getTransformation2 = 9022;
                    } else if (attacker.usingMagic || attacker.autocasting) {
                        getTransformation2 = 9023;
                    } else {
                        getTransformation2 = 9021;
                    }
                    if (damage.getAmount() > 0) { //reset
                        npc.requestTransform(getTransformation2);
                        attacker.totalHunllefDamage = 0;
                    }
                }
                break;
        }

        boolean rejectsFaceUpdate = false;
        if (npc.getNpcId() >= 2042 && npc.getNpcId() <= 2044 || npc.getNpcId() == 6720) {
            if (attacker.getZulrahEvent().getNpc() != null && attacker.getZulrahEvent().getNpc().equals(npc)) {
                if (attacker.getZulrahEvent().getStage() == 1) {
                    rejectsFaceUpdate = true;
                }
            }
//            if (attacker.getZulrahEvent().isTransforming()) {
//                return;
//            }
        }
        if (!rejectsFaceUpdate) {
            npc.facePlayer(attacker.getIndex());
        }
        if (npc.isAutoRetaliate()) {
            if (npc.underAttackBy > 0) {
                npc.attackEntity(attacker);
            } else if (npc.underAttackBy < 0) {
                npc.attackEntity(attacker);
            }
        }
    }
}
