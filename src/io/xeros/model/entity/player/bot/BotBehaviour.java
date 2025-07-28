package io.xeros.model.entity.player.bot;

import io.xeros.Server;
import io.xeros.content.skills.woodcutting.Tree;
import io.xeros.content.skills.woodcutting.Woodcutting;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.tickable.Tickable;
import io.xeros.model.tickable.TickableContainer;
import io.xeros.model.world.objects.GlobalObject;

import java.util.Optional;

/**
 * Simple behaviour controller for bot players.
 */
public class BotBehaviour implements Tickable<Player> {

    public enum Type {
        FIGHT_NEAREST_NPC,
        CHOP_NEAREST_TREE
    }

    private final Type type;

    public BotBehaviour(Type type) {
        this.type = type;
    }

    @Override
    public void tick(TickableContainer<Player> container, Player bot) {
        if (bot == null || bot.disconnected || !bot.isBot()) {
            container.stop();
            return;
        }

        switch (type) {
            case FIGHT_NEAREST_NPC:
                fightNearestNpc(bot);
                break;
            case CHOP_NEAREST_TREE:
                chopNearestTree(bot);
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
        if (nearest != null && best <= 10) {
            bot.attackEntity(nearest);
        }
    }

    private void chopNearestTree(Player bot) {
        WorldObject tree = findNearbyTree(bot, 4);
        if (tree != null) {
            Woodcutting.getInstance().chop(bot, tree.getId(), tree.getX(), tree.getY());
        }
    }

    private WorldObject findNearbyTree(Player bot, int radius) {
        Position pos = bot.getPosition();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = pos.getX() + dx;
                int y = pos.getY() + dy;
                for (Tree tree : Tree.values()) {
                    for (int id : tree.getTreeIds()) {
                        Optional<WorldObject> obj = bot.getRegionProvider().get(x, y).getWorldObject(id, x, y, pos.getHeight());
                        if (obj.isPresent() && !Server.getGlobalObjects().exists(tree.getStumpId(), x, y)) {
                            return obj.get();
                        }
                    }
                }
            }
        }
        return null;
    }
}
