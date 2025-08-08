package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.InstanceMutator;
import io.xeros.content.instances.InstanceMutatorManager;
import io.xeros.content.instances.hazard.HazardTier;
import io.xeros.content.instances.hazard.HazardEffectModifier;
import io.xeros.content.instances.hazard.WeeklyHazardManager;
import io.xeros.content.instances.hazard.HazardDebugLogger;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;

public class EnvironmentalHazardScheduler {

    private final BossInstanceArea area;
    private boolean started;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private boolean inProgress;
    private final Map<EnvironmentalHazardType, HazardRecord> lastHazards = new EnumMap<>(EnvironmentalHazardType.class);
    private String lastDamageTick = "none";

    public EnvironmentalHazardScheduler(BossInstanceArea area) {
        this.area = area;
    }

    public void start() {
        if (started) {
            return;
        }
        EnvironmentalHazardLoader.HazardConfig config = EnvironmentalHazardLoader.get(area.getTier());
        EnvironmentalHazardPatternLoader.PatternConfig patternConfig = EnvironmentalHazardPatternLoader.get(area.getTier());
        WeeklyHazardManager.WeeklyHazardConfig weekly = WeeklyHazardManager.get();
        if (config != null && config.getHazards() != null && !config.getHazards().isEmpty()) {
            started = true;
            int frequency = config.getFrequency();
            if (InstanceMutatorManager.isActive(InstanceMutator.DOUBLE_HAZARD_MODE)) {
                frequency = Math.max(1, frequency / 2);
            }
            final List<EnvironmentalHazardDefinition> pool = config.getHazards().stream()
                    .filter(h -> weekly == null || weekly.staticHazards.contains(h.getType()))
                    .collect(Collectors.toList());
            int finalFrequency = frequency;
            CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (inProgress || pool.isEmpty()) {
                        return;
                    }
                    inProgress = true;
                    EnvironmentalHazardDefinition def = Misc.randomTypeOfList(pool);
                    HazardTier hazardTier = determineTier(def.getTier());
                    double dmgMod = 1.0;
                    String synergyMsg = null;
                    for (InstanceMutator mut : InstanceMutatorManager.getActiveMutators()) {
                        if (weekly != null && !weekly.synergyMutators.contains(mut)) {
                            continue;
                        }
                        for (HazardEffectModifier mod : InstanceMutatorManager.getHazardSynergies(mut)) {
                            if (mod.getTarget() == def.getType()) {
                                dmgMod *= mod.getDamageMultiplier();
                                synergyMsg = mod.getMessage();
                                InstanceMutatorManager.logSynergy(mut.name() + " -> " + def.getType());
                            }
                        }
                    }
                    HazardContext ctx = def.activate(area, hazardTier, dmgMod, synergyMsg);
                    recordLast(def, ctx);
                    InstanceMutatorManager.increaseDanger(5);
                    inProgress = false;
                }
            }, finalFrequency);
            if (weekly != null && weekly.eliteHazard != null) {
                CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (InstanceMutatorManager.getGlobalDanger() >= 80) {
                            EnvironmentalHazardDefinition elite = pool.stream()
                                    .filter(h -> h.getType() == weekly.eliteHazard)
                                    .findFirst().orElse(null);
                            if (elite != null) {
                                elite.activate(area, HazardTier.EXTREME, 1.0, "@red@Elite hazard empowered by global danger!");
                                InstanceMutatorManager.increaseDanger(10);
                            }
                        }
                    }
                }, 100);
            }
        }
        if (patternConfig != null && patternConfig.getPatterns() != null && !patternConfig.getPatterns().isEmpty()) {
            int pfreq = patternConfig.getFrequency();
            CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    EnvironmentalHazardPattern pattern = Misc.randomTypeOfList(patternConfig.getPatterns());
                    pattern.activate(area);
                    InstanceMutatorManager.increaseDanger(10);
                }
            }, pfreq);
        }
    }

    public void stop() {
        CycleEventHandler.getSingleton().stopEvents(area);
    }

    /**
     * Called by external systems when a reactive trigger occurs.
     */
    public void trigger(String trigger, Player player) {
        EnvironmentalHazardLoader.HazardConfig config = EnvironmentalHazardLoader.get(area.getTier());
        if (config == null || config.getHazards() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        for (EnvironmentalHazardDefinition def : config.getHazards()) {
            if (trigger.equalsIgnoreCase(def.getTriggerCondition())) {
                long last = cooldowns.getOrDefault(trigger, 0L);
                if (now - last < def.getCooldownWindow()) {
                    continue;
                }
                HazardTier hazardTier = determineTier(def.getTier());
                double dmgMod = 1.0;
                String synergyMsg = null;
                for (InstanceMutator mut : InstanceMutatorManager.getActiveMutators()) {
                    if (weekly != null && !weekly.synergyMutators.contains(mut)) {
                        continue;
                    }
                    for (HazardEffectModifier mod : InstanceMutatorManager.getHazardSynergies(mut)) {
                        if (mod.getTarget() == def.getType()) {
                            dmgMod *= mod.getDamageMultiplier();
                            synergyMsg = mod.getMessage();
                            InstanceMutatorManager.logSynergy(mut.name() + " -> " + def.getType());
                        }
                    }
                }
                HazardContext ctx = def.activate(area, hazardTier, dmgMod, synergyMsg);
                recordLast(def, ctx);
                cooldowns.put(trigger, now);
                InstanceMutatorManager.increaseDanger(7);
            }
        }
    }

    private HazardTier determineTier(HazardTier base) {
        HazardTier result = base;
        int danger = InstanceMutatorManager.getDangerLevel();
        int roll = Misc.random(99);
        if (danger > 66 && roll < danger - 66) {
            result = HazardTier.EXTREME;
        } else if (danger > 33 && roll < danger - 33) {
            result = HazardTier.ADVANCED;
        }
        return result;
    }

    /** Returns human readable debugging information for admin commands. */
    public List<String> debugState() {
        List<String> list = new ArrayList<>();
        list.add("started=" + started + ", inProgress=" + inProgress);
        list.add("cooldowns=" + cooldowns.toString());
        list.add("lastHazards=" + lastHazards.keySet());
        list.add("lastDamage=" + lastDamageTick);
        list.addAll(InstanceMutatorManager.getSynergyLog());
        return list;
        }

    private void recordLast(EnvironmentalHazardDefinition def, HazardContext ctx) {
        lastHazards.put(def.getType(), new HazardRecord(def, ctx));
        HazardDebugLogger.log(area, "trigger:" + def.getType());
    }

    public void recordDamage(String info) {
        lastDamageTick = info;
        HazardDebugLogger.log(area, "damage:" + info);
    }

    public boolean replay(EnvironmentalHazardType type) {
        HazardRecord r = lastHazards.get(type);
        if (r == null) return false;
        r.def.activate(area, r.ctx.getTier(), 1.0, null, r.ctx.getPosition());
        HazardDebugLogger.log(area, "replay:" + type);
        return true;
    }

    private static class HazardRecord {
        final EnvironmentalHazardDefinition def;
        final HazardContext ctx;
        HazardRecord(EnvironmentalHazardDefinition def, HazardContext ctx) {
            this.def = def; this.ctx = ctx;
        }
    }
}
