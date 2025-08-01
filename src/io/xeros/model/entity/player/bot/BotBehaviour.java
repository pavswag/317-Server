package io.xeros.model.entity.player.bot;

import io.xeros.Server;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.skills.SkillHandler;
import io.xeros.content.skills.woodcutting.Tree;
import io.xeros.content.skills.woodcutting.Woodcutting;
import io.xeros.content.skills.mining.Mining;
import io.xeros.content.skills.mining.Mineral;
import io.xeros.content.skills.Fishing;
import io.xeros.model.CombatType;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Location3D;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.tickable.Tickable;
import io.xeros.model.tickable.TickableContainer;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Simple behaviour controller for bot players.
 */
public class BotBehaviour implements Tickable<Player> {
    private static final Logger logger = LoggerFactory.getLogger(BotBehaviour.class);
    private static final String[] PK_PHRASES = {
            "You good, bro?",
            "Gf",
            "Sit!",
            "Nice try",
            "Smoked"
    };
    public enum Type {
        FIGHT_NEAREST_NPC,
        CHOP_NEAREST_TREE,
        MINE_NEAREST_ROCK,
        PK_NEAREST_PLAYER,
        FISH_NEAREST_SPOT
    }

    private static final java.util.Map<Integer, Integer> FISHING_DATA = new java.util.HashMap<>();

    static {
        FISHING_DATA.put(7155, 1);
        FISHING_DATA.put(3913, 1);
        FISHING_DATA.put(3917, 1);
        FISHING_DATA.put(7200, 5);
        FISHING_DATA.put(3317, 14);
        FISHING_DATA.put(4712, 15);
        FISHING_DATA.put(1524, 11);
        FISHING_DATA.put(3417, 4);
        FISHING_DATA.put(7468, 4);
        FISHING_DATA.put(1507, 4);
        FISHING_DATA.put(1506, 4);
        FISHING_DATA.put(3657, 8);
        FISHING_DATA.put(635, 13);
        FISHING_DATA.put(6825, 16);
        FISHING_DATA.put(1531, 17);
        FISHING_DATA.put(1520, 9);
        FISHING_DATA.put(310, 9);
        FISHING_DATA.put(314, 9);
        FISHING_DATA.put(317, 9);
        FISHING_DATA.put(318, 9);
        FISHING_DATA.put(328, 9);
        FISHING_DATA.put(331, 9);
        FISHING_DATA.put(322, 10);
        FISHING_DATA.put(334, 10);
        FISHING_DATA.put(321, 7);
        FISHING_DATA.put(324, 7);
        FISHING_DATA.put(329, 6);
    }

    private final Type type;
    private int nextActionTick = 0;

    public BotBehaviour(Type type) {
        this.type = type;
    }
    private void healIfNeeded(Player bot) {
        int max = bot.getHealth().getMaximumHealth();
        if (bot.getHealth().getCurrentHealth() <= max / 3) {
            bot.startAnimation(829);
            bot.getHealth().increase(20);
        }
    }
    @Override
    public void tick(TickableContainer<Player> container, Player bot) {
        if (bot == null || !bot.isBot()) {
            container.stop();
            return;
        }

        if (container.getTicks() < nextActionTick) {
            return;
        }
        healIfNeeded(bot);
        nextActionTick = container.getTicks() + Misc.random(3, 7);

        switch (type) {
            case FIGHT_NEAREST_NPC:
                fightNearestNpc(bot);
                break;
            case CHOP_NEAREST_TREE:
                chopNearestTree(bot);
                break;
            case MINE_NEAREST_ROCK:
                mineNearestRock(bot);
                break;
            case FISH_NEAREST_SPOT:
                fishNearestSpot(bot);
                break;
            case PK_NEAREST_PLAYER:
                fightNearestPlayer(bot);
                break;
        }
    }
    private void fightNearestNpc(Player bot) {
        NPC nearest = null;
        double best = Double.MAX_VALUE;
        for (NPC npc : NPCHandler.npcs) {
            if (npc == null || npc.isDeadOrDying() || npc.heightLevel != bot.getHeight())
                continue;
            double distance = bot.getPosition().distanceTo(npc.getPosition());
            if (distance < best) {
                best = distance;
                nearest = npc;
            }
        }

        if (nearest == null || best > 10) {
            randomWalk(bot);
            return;
        }

        //logger.debug("{} attacking player {}", bot.getDisplayName(), nearest.getDisplayName());

        logger.debug("{} attacking npc {}", bot.getDisplayName(), nearest.getNpcId());

        if (best > 1) {
            bot.getPA().playerWalk(nearest.getX(), nearest.getY());
        } else if (bot.npcAttackingIndex == 0) {
            bot.attackEntity(nearest);
        }
    }

    private void fightNearestPlayer(Player bot) {
        if (!bot.getPosition().inWild()) {
            randomWalk(bot);
            return;
        }

        Player target = null;
        double best = Double.MAX_VALUE;

        if (bot.lastDefend != null) {
            Player attacker = (Player) bot.lastDefend.get();
            if (attacker != null && !attacker.isDead && attacker.getPosition().inWild()) {
                target = attacker;
                best = bot.getPosition().distanceTo(attacker.getPosition());
            }
        }

        if (target == null) {
            for (Player player : PlayerHandler.players) {
                if (player == null || player == bot || player.isDead || !player.getPosition().inWild())
                    continue;
                double distance = bot.getPosition().distanceTo(player.getPosition());
                if (distance < best) {
                    best = distance;
                    target = player;
                }
            }
        }

        if (target == null || best > 10) {
            randomWalk(bot);
            return;
        }

        if (best > 1) {
            bot.getPA().playerWalk(target.getX(), target.getY());
        } else if (bot.playerAttackingIndex == 0 || bot.playerAttackingIndex != target.getIndex()) {
            bot.faceEntity(target);
            bot.attackEntity(target);
            if (Misc.random(4) == 0) {
                bot.forcedChat(PK_PHRASES[Misc.random(PK_PHRASES.length - 1)]);
            }
        }

        adjustPrayers(bot);
        maybeFreeze(bot, target);
    }

    private void adjustPrayers(Player bot) {
        CombatType type = bot.lastHitType;
        if (type == null) {
            return;
        }
        switch (type) {
            case MELEE:
                CombatPrayer.activatePrayer(bot, CombatPrayer.PROTECT_FROM_MELEE);
                break;
            case RANGE:
                CombatPrayer.activatePrayer(bot, CombatPrayer.PROTECT_FROM_RANGED);
                break;
            case MAGE:
                CombatPrayer.activatePrayer(bot, CombatPrayer.PROTECT_FROM_MAGIC);
                break;
            default:
                break;
        }
    }

    private void maybeFreeze(Player bot, Player target) {
        if (target.isFreezable() && target.freezeTimer <= 0 && target.freezeDelay <= 0) {
            int delay = 10;
            target.frozenBy = EntityReference.getReference(bot);
            target.freezeTimer = delay;
            target.freezeDelay = delay;
            target.resetWalkingQueue();
            target.sendMessage("You have been frozen.");
            target.getPA().sendGameTimer(ClientGameTimer.FREEZE, java.util.concurrent.TimeUnit.MILLISECONDS, 600 * delay);
        }
    }

    private void chopNearestTree(Player bot) {
        if (isSkilling(bot))
            return;

        WorldObject tree = findNearbyTree(bot, 6);
        if (tree == null) {
            randomWalk(bot);
            return;
        }

        logger.debug("{} chopping tree {} at ({}, {})", bot.getDisplayName(), tree.getId(), tree.getX(), tree.getY());

        if (bot.distanceToPoint(tree.getX(), tree.getY()) > 1) {
            bot.getPA().playerWalk(tree.getX(), tree.getY());
        } else {
            Woodcutting.getInstance().chop(bot, tree.getId(), tree.getX(), tree.getY());
        }
    }

    private void mineNearestRock(Player bot) {
        if (isSkilling(bot))
            return;

        WorldObject rock = findNearbyRock(bot, 6);
        if (rock == null) {
            randomWalk(bot);
            return;
        }

        logger.debug("{} mining rock {} at ({}, {})", bot.getDisplayName(), rock.getId(), rock.getX(), rock.getY());

        if (bot.distanceToPoint(rock.getX(), rock.getY()) > 1) {
            bot.getPA().playerWalk(rock.getX(), rock.getY());
        } else {
            new Mining(bot).mine(rock.getId(), new Location3D(rock.getX(), rock.getY(), rock.getHeight()));
        }
    }

    private void fishNearestSpot(Player bot) {
        if (isSkilling(bot))
            return;

        NPC spot = findNearbyFishingSpot(bot, 6);
        if (spot == null) {
            randomWalk(bot);
            return;
        }

        logger.debug("{} fishing at spot {}", bot.getDisplayName(), spot.getNpcId());

        if (bot.distanceToPoint(spot.getX(), spot.getY()) > 1) {
            bot.getPA().playerWalk(spot.getX(), spot.getY());
        } else {
            Integer data = FISHING_DATA.get(spot.getNpcId());
            if (data != null)
                Fishing.attemptdata(bot, data);
        }
    }

    private WorldObject findNearbyTree(Player bot, int radius) {
        Position pos = bot.getPosition();
        WorldObject bestObj = null;
        int bestLevel = -1;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = pos.getX() + dx;
                int y = pos.getY() + dy;
                for (Tree tree : Tree.values()) {
                    for (int id : tree.getTreeIds()) {
                        Optional<WorldObject> obj = bot.getRegionProvider().get(x, y).getWorldObject(id, x, y, pos.getHeight());
                        if (obj.isPresent() && !Server.getGlobalObjects().exists(tree.getStumpId(), x, y, pos.getHeight())) {
                            if (tree.getLevelRequired() > bestLevel) {
                                bestLevel = tree.getLevelRequired();
                                bestObj = obj.get();
                            }
                        }
                    }
                }
            }
        }
        return bestObj;
    }

    private WorldObject findNearbyRock(Player bot, int radius) {
        Position pos = bot.getPosition();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = pos.getX() + dx;
                int y = pos.getY() + dy;
                for (Mineral mineral : Mineral.values()) {
                    for (int id : mineral.getObjectIds()) {
                        Optional<WorldObject> obj = bot.getRegionProvider().get(x, y).getWorldObject(id, x, y, pos.getHeight());
                        if (obj.isPresent() && !Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, x, y, pos.getHeight())) {
                            return obj.get();
                        }
                    }
                }
            }
        }
        return null;
    }

    private NPC findNearbyFishingSpot(Player bot, int radius) {
        NPC nearest = null;
        double best = Double.MAX_VALUE;
        for (NPC npc : NPCHandler.npcs) {
            if (npc == null || npc.isDeadOrDying() || npc.heightLevel != bot.getHeight())
                continue;
            if (!FISHING_DATA.containsKey(npc.getNpcId()))
                continue;
            double distance = bot.getPosition().distanceTo(npc.getPosition());
            if (distance < best && distance <= radius) {
                best = distance;
                nearest = npc;
            }
        }
        return nearest;
    }

    private boolean isSkilling(Player bot) {
        return Server.getEventHandler().isRunning(bot, "skilling");
    }

    private void randomWalk(Player bot) {
        int dx = Misc.random(-1, 1);
        int dy = Misc.random(-1, 1);
        if (dx != 0 || dy != 0) {
            bot.getPA().playerWalk(bot.getX() + dx, bot.getY() + dy);
        }
    }
}