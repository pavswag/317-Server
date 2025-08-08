package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.InstanceMutator;
import io.xeros.content.instances.InstanceMutatorManager;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentalHazardScheduler {

    private final BossInstanceArea area;
    private boolean started;
    private final Map<String, Long> cooldowns = new HashMap<>();

    public EnvironmentalHazardScheduler(BossInstanceArea area) {
        this.area = area;
    }

    public void start() {
        if (started) {
            return;
        }
        EnvironmentalHazardLoader.HazardConfig config = EnvironmentalHazardLoader.get(area.getTier());
        EnvironmentalHazardPatternLoader.PatternConfig patternConfig = EnvironmentalHazardPatternLoader.get(area.getTier());
        if (config != null && config.getHazards() != null && !config.getHazards().isEmpty()) {
            started = true;
            int frequency = config.getFrequency();
            if (InstanceMutatorManager.isActive(InstanceMutator.DOUBLE_HAZARD_MODE)) {
                frequency = Math.max(1, frequency / 2);
            }
            int finalFrequency = frequency;
            CycleEventHandler.getSingleton().addEvent(area, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    EnvironmentalHazardDefinition def = Misc.randomTypeOfList(config.getHazards());
                    def.activate(area);
                    InstanceMutatorManager.increaseDanger(5);
                }
            }, finalFrequency);
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
                def.activate(area);
                cooldowns.put(trigger, now);
                InstanceMutatorManager.increaseDanger(7);
            }
        }
    }
}
