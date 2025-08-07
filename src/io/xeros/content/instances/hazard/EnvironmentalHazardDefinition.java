package io.xeros.content.instances.hazard;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
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

    public EnvironmentalHazardType getType() { return type; }
    public int getDamage() { return damage; }
    public int getDrain() { return drain; }
    public int getStun() { return stun; }
    public int getDuration() { return duration; }

    public void activate(BossInstanceArea area) {
        Boundary b = area.getTier().getZoneBoundary();
        int x = Misc.random(b.getMinimumX(), b.getMaximumX());
        int y = Misc.random(b.getMinimumY(), b.getMaximumY());
        Position pos = new Position(x, y, area.getHeight());
        switch (type) {
            case FIRE_TILE:
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    int ticks;
                    @Override
                    public void execute(CycleEventContainer container) {
                        for (Player p : area.getPlayers()) {
                            if (p.getPosition().equals(pos)) {
                                p.appendDamage(damage, Hitmark.HIT);
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
                                p.prayerPoint = Math.max(0, p.prayerPoint - drain);
                                p.getPA().refreshSkill(5);
                                p.sendMessage("@pur@Poison mist drains your prayer!");
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
                                p.freezeTimer = Math.max(p.freezeTimer, stun);
                                p.sendMessage("@red@The floor collapses beneath you!");
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
