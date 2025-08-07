package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.content.instances.InstanceMutator;
import io.xeros.content.instances.InstanceMutatorManager;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.util.Misc;

public class EnvironmentalHazardScheduler {

    private final BossInstanceArea area;
    private boolean started;

    public EnvironmentalHazardScheduler(BossInstanceArea area) {
        this.area = area;
    }

    public void start() {
        if (started) {
            return;
        }
        EnvironmentalHazardLoader.HazardConfig config = EnvironmentalHazardLoader.get(area.getTier());
        if (config == null || config.getHazards() == null || config.getHazards().isEmpty()) {
            return;
        }
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
            }
        }, finalFrequency);
    }

    public void stop() {
        CycleEventHandler.getSingleton().stopEvents(area);
    }
}
