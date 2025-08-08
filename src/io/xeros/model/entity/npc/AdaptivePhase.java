package io.xeros.model.entity.npc;

import java.util.function.Consumer;

/**
 * Represents a single phase in an adaptive boss fight. Each phase can be
 * triggered by a hitpoint percentage or time milestone and may apply
 * environment effects when activated.
 */
public class AdaptivePhase {

    private final double hpPercent; // value between 0 and 1, 0 if unused
    private final long timeMillis;  // milliseconds since spawn, 0 if unused
    private final String introMessage;
    private final Consumer<NPC> environment;

    public AdaptivePhase(double hpPercent, long timeMillis, String introMessage) {
        this(hpPercent, timeMillis, introMessage, null);
    }

    public AdaptivePhase(double hpPercent, long timeMillis, String introMessage, Consumer<NPC> environment) {
        this.hpPercent = hpPercent;
        this.timeMillis = timeMillis;
        this.introMessage = introMessage;
        this.environment = environment;
    }

    public double getHpPercent() {
        return hpPercent;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public String getIntroMessage() {
        return introMessage;
    }

    public void activateEnvironment(NPC npc) {
        if (environment != null) {
            environment.accept(npc);
        }
    }
}
