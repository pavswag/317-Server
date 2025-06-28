package io.xeros.content.afkzone;

import io.xeros.Server;
import io.xeros.content.commands.owner.Pos;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Location3D;

import java.util.concurrent.TimeUnit;

public class Afk {
    /**
     * The stealing animation
     */
    private static final int ANIMATION = 881;

    public static void Start(Player c, Location3D location, int id) {
        c.facePosition(location.getX(), location.getY());
        c.afk_obj_position = new Position(location.getX(), location.getY(), 0);
        c.stopPlayerSkill = true;
        c.setAfkTier(id);
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.AFKZone);
        handleAnimation(c, id);

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.AFKZone, c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c.isDisconnected()) {
                    container.stop();
                    return;
                }
                if (!c.stopPlayerSkill) {
                    container.stop();
                    return;
                }

                int afkPoints = c.getAfkPoints();

                if (afkPoints < 0) {
                    c.setAfkPoints(Math.abs(afkPoints)); // Convert negative points to positive
                }

                c.setAfkPoints(c.getAfkPoints() + getPoints(c));
                c.setAfkAttempts(c.getAfkAttempts() + getPoints(c));

                AfkBoss.handleGoblinTick();

                c.afk_position = c.getPosition();
//                c.sendMessage("@red@You now gain " + getPoints(c) + " afk points, Total: " + c.getAfkPoints() + "!", TimeUnit.MINUTES.toMillis(3));

                if (container.getTotalExecutions() % 3 == 0) {
                    c.startAnimation(c.AfkAnimation);
                    giveXP(c, c.objectId);
                    if (c.AfkAnimation == 4975) {
                        c.gfx0(831);
                    }
                    if (c.AfkAnimation == 4951) {
                        c.gfx0(819);
                    }
                }

            }
        },3,false);
    }


    public static int getPoints(Player c) {
        int count = 1;

        if (c.amDonated >= 20 && c.amDonated < 50) {
            count += 1;
        } else if (c.amDonated >= 50 && c.amDonated < 100) {
            count += 2;
        } else if (c.amDonated >= 100 && c.amDonated < 250) {
            count += 3;
        } else if (c.amDonated >= 250 && c.amDonated < 500) {
            count += 4;
        } else if (c.amDonated >= 500 && c.amDonated < 3000) {
            count += 5;
        } else if (c.amDonated >= 3000) {
            count += 15;
        }

        count += afkSum(c);

        if (c.hasFollower && c.petSummonId == 33065) {
            count *= 2;
        }

        return count;
    }
    public static int[] afk_ids = {26858, 26860, 26862};
    public static int afkSum(Player c) {
        int total = 0;
        for (int grace : afk_ids) {
            if (c.getItems().isWearingItem(grace)) {
                total++;
            }
        }
        return total;
    }

    public static void roll(Player c) {
        T0.rolledCommon(c);
    }

    public static void giveXP(Player c, int objectId) {
        if (objectId == 33710 || objectId == 35969 || objectId == 36066) {
        //if (objectId == 33710 || objectId == 35969 || objectId == 36066) {
            //Hunter & herbs
            c.getPA().addSkillXP(50, Skill.WOODCUTTING.getId(), true);
            c.getPA().addSkillXP(50, Skill.FARMING.getId(), true);
        }
        if (objectId == 22772 || objectId == 35834) {
            //Hunter & herbs
            c.getPA().addSkillXP(50, Skill.HUNTER.getId(), true);
            c.getPA().addSkillXP(50, Skill.HERBLORE.getId(), true);
        }
        if (objectId == 35971) {
            //Fishing
            c.getPA().addSkillXP(50, Skill.FISHING.getId(), true);
        }
        if (objectId == 30932) {
            //Cooking
            c.getPA().addSkillXP(50, Skill.COOKING.getId(), true);
        }
        if (objectId == 39095) {
            //Mining & Smithing
            c.getPA().addSkillXP(50, Skill.MINING.getId(), true);
            c.getPA().addSkillXP(50, Skill.SMITHING.getId(), true);
        }
        if (objectId == 31934 || objectId == 34687) {
            //Demon Hunter & Slayer
            c.getPA().addSkillXP(50, Skill.DEMON_HUNTER.getId(), true);
            c.getPA().addSkillXP(50, Skill.SLAYER.getId(), true);
        }
    }

    private static void handleAnimation(Player player, int objectID) {
        switch (objectID) {
            case 35834:
                player.AfkAnimation = 2282;
                break;
            case 33710:
            case 35969:
                player.AfkAnimation = 8778;
                break;
            case 8988:
            case 8986:
                player.AfkAnimation = 827;
                break;
            case 30019:
                player.AfkAnimation = 4975;
                break;
            case 10091:
            case 35971:
                player.AfkAnimation = 4951;
                break;
            case 36570:
            case 36571:
            case 36572:
                player.AfkAnimation = 881;
                break;
            case 20211:
                player.AfkAnimation = 828;
                break;
            case 39095:
            case 33257:
            case 28562:
            case 15251:
                player.AfkAnimation = 8787;
                break;
        }
    }

    public static boolean handleAFKObjectCheck(Player player, GlobalObject go) {
        return switch (go.getObjectId()) {
            case 35834, 33710, 8988, 8986, 30019, 10091, 35971, 36570, 36571, 36572, 20211, 39095, 33257, 28562, 15251,
                 35969 -> {
                player.afk_object = go.getObjectId();
                yield true;
            }
            default -> false;
        };

    }
}
