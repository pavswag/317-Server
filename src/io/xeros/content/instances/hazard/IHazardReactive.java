package io.xeros.content.instances.hazard;

/**
 * Objects that can react to environmental hazard triggers implement this
 * interface. Default method allows classes to opt-in without boilerplate.
 */
public interface IHazardReactive {

    default HazardReaction onHazardTriggered(HazardContext ctx) {
        // Default no-op.
        return null;
    }
}
