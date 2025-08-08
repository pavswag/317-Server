package io.xeros.content.instances.hazard;

import io.xeros.model.entity.npc.NPC;

import java.util.function.Consumer;

/**
 * Describes a delayed reaction an {@link IHazardReactive} entity can perform
 * after a hazard triggers. Reactions have a priority to allow weighting and a
 * delay in ticks before the action executes.
 */
public class HazardReaction {

    private final String name;
    private final int priority;
    private final int delay;
    private final Consumer<NPC> action;

    public HazardReaction(String name, int priority, int delay, Consumer<NPC> action) {
        this.name = name;
        this.priority = priority;
        this.delay = delay;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int getDelay() {
        return delay;
    }

    public Consumer<NPC> getAction() {
        return action;
    }

    public static HazardReaction of(String name, int priority, int delay, Consumer<NPC> action) {
        return new HazardReaction(name, priority, delay, action);
    }
}
