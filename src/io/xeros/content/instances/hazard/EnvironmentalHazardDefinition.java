package io.xeros.content.instances.hazard;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.hazard.HazardTier;
import io.xeros.content.instances.hazard.IHazardReactive;
import io.xeros.content.instances.hazard.HazardContext;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class EnvironmentalHazardDefinition {

    private EnvironmentalHazardType type;
    private int damage;
    private int drain;
    private int stun;
    private int duration;
    private String triggerCondition;
    private int cooldownWindow;
    private HazardTier tier = HazardTier.BASIC;

    public EnvironmentalHazardType getType() { return type; }
    public int getDamage() { return damage; }
    public int getDrain() { return drain; }
    public int getStun() { return stun; }
    public int getDuration() { return duration; }
    public String getTriggerCondition() { return triggerCondition; }
    public int getCooldownWindow() { return cooldownWindow; }
    public HazardTier getTier() { return tier; }

    public void activate(BossInstanceArea area, HazardTier finalTier, double dmgMod, String synergyMsg) {
        Boundary b = area.getTier().getZoneBoundary();
        int x = Misc.random(b.getMinimumX(), b.getMaximumX());
        int y = Misc.random(b.getMinimumY(), b.getMaximumY());
        Position pos = new Position(x, y, area.getHeight());
        int scaledDamage = (int) (finalTier.scale(damage) * dmgMod);
        int scaledDrain = (int) (finalTier.scale(drain) * dmgMod);
        int scaledStun = (int) (finalTier.scale(stun) * dmgMod);
        HazardContext ctx = new HazardContext(type, finalTier, pos);
        if (synergyMsg != null) {
            for (Player p : area.getPlayers()) {
                p.sendMessage(synergyMsg);
            }
        }
        switch (type) {
            case FIRE_TILE:
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    int ticks;
                    @Override
                    public void execute(CycleEventContainer container) {
                        for (Player p : area.getPlayers()) {
                            if (p.getPosition().equals(pos)) {
                                p.appendDamage(scaledDamage, Hitmark.HIT);
                                if (p instanceof IHazardReactive) {
                                    ((IHazardReactive)p).onHazardTriggered(ctx);
                                }
                            }
                        }
                        for (NPC n : area.getNpcs()) {
                            if (n.getPosition().equals(pos) && n instanceof IHazardReactive) {
                                ((IHazardReactive)n).onHazardTriggered(ctx);
                                HazardDebugLogger.logNpcReaction(area, n, ctx);
                            }
                        }
                        if (++ticks >= duration) {
                            container.stop();
                        }
                    }
                }, 1);
                break;
            case POISON_MIST:
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    int ticks;
                    @Override
                    public void execute(CycleEventContainer container) {
                        for (Player p : area.getPlayers()) {
                            Position pp = p.getPosition();
                            if (Math.abs(pp.getX() - x) <= 1 && Math.abs(pp.getY() - y) <= 1 && pp.getHeight() == pos.getHeight()) {
                                p.prayerPoint = Math.max(0, p.prayerPoint - scaledDrain);
                                p.getPA().refreshSkill(5);
                                p.sendMessage("@pur@Poison mist drains your prayer!");
                                if (p instanceof IHazardReactive) {
                                    ((IHazardReactive)p).onHazardTriggered(ctx);
                                }
                            }
                        }
                        for (NPC n : area.getNpcs()) {
                            Position np = n.getPosition();
                            if (Math.abs(np.getX() - x) <= 1 && Math.abs(np.getY() - y) <= 1 && np.getHeight() == pos.getHeight()) {
                                if (n instanceof IHazardReactive) {
                                    ((IHazardReactive)n).onHazardTriggered(ctx);
                                    HazardDebugLogger.logNpcReaction(area, n, ctx);
                                }

                            }
                        }
                        if (++ticks >= duration) {
                            container.stop();
                        }
                    }
                }, 1);
                break;
            case CRUMBLING_FLOOR:
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    int ticks;
                    @Override
                    public void execute(CycleEventContainer container) {
                        for (Player p : area.getPlayers()) {
                            if (p.getPosition().equals(pos)) {
                                p.freezeTimer = Math.max(p.freezeTimer, scaledStun);
                                p.sendMessage("@red@The floor collapses beneath you!");
                                if (p instanceof IHazardReactive) {
                                    ((IHazardReactive)p).onHazardTriggered(ctx);
                                }
                            }
                        }
                        for (NPC n : area.getNpcs()) {
                            if (n.getPosition().equals(pos) && n instanceof IHazardReactive) {
                                ((IHazardReactive)n).onHazardTriggered(ctx);
                                HazardDebugLogger.logNpcReaction(area, n, ctx);
                            }
                        }
                        if (++ticks >= duration) {
                            container.stop();
                        }
                    }
                }, 1);
                break;
        }
    }
}
