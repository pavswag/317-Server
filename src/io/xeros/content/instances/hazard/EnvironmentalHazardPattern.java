package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Boundary;

/**
 * Simple arena wide hazard patterns. The behaviour here is intentionally
 * lightweight and extensible so more elaborate effects can be added later.
 */
public class EnvironmentalHazardPattern {

    public enum PatternType {
        FLAME_RING,
        VOID_PULSE,
        TOXIC_TORRENT,
        SHOCKWAVE,
        CHAOS_RIFT
    }

    private PatternType type;

    public PatternType getType() {
        return type;
    }

    public void setType(PatternType type) {
        this.type = type;
    }

    /**
     * Activate the pattern for the supplied area.
     */
    public void activate(BossInstanceArea area) {
        switch (type) {
            case VOID_PULSE:
                // simple: stun all players briefly
                for (Player p : area.getPlayers()) {
                    p.freezeTimer = Math.max(p.freezeTimer, 5);
                    p.sendMessage("@blu@A void pulse shocks you!");
                }
                break;
            case TOXIC_TORRENT:
                for (Player p : area.getPlayers()) {
                    p.prayerPoint = Math.max(0, p.prayerPoint - 10);
                    p.getPA().refreshSkill(5);
                    p.sendMessage("@gre@Toxic fumes drain your prayer!");
                }
                break;
            case SHOCKWAVE:
                for (Player p : area.getPlayers()) {
                    p.appendDamage(10, io.xeros.content.combat.Hitmark.HIT);
                }
                break;
            case CHAOS_RIFT:
                Boundary b = area.getTier().getZoneBoundary();
                for (Player p : area.getPlayers()) {
                    int nx = p.getPosition().getX() + io.xeros.util.Misc.random(-1,1);
                    int ny = p.getPosition().getY() + io.xeros.util.Misc.random(-1,1);
                    nx = Math.max(b.getMinimumX(), Math.min(b.getMaximumX(), nx));
                    ny = Math.max(b.getMinimumY(), Math.min(b.getMaximumY(), ny));
                    p.getPA().movePlayer(nx, ny, area.getHeight());
                    p.sendMessage("@pur@Chaotic forces warp your position!");
                }
                break;
            case FLAME_RING:
            default:
                // damage all players mildly
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    int ticks;
                    @Override
                    public void execute(CycleEventContainer container) {
                        for (Player p : area.getPlayers()) {
                            p.appendDamage(5, io.xeros.content.combat.Hitmark.HIT);
                        }
                        if (++ticks >= 5) {
                            container.stop();
                        }
                    }
                }, 1);
                break;
        }
    }
}
