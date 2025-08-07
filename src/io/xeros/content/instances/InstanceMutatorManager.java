package io.xeros.content.instances;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class InstanceMutatorManager {

    private static final EnumSet<InstanceMutator> ACTIVE = EnumSet.noneOf(InstanceMutator.class);

    public static void toggle(InstanceMutator mutator) {
        if (ACTIVE.contains(mutator)) {
            ACTIVE.remove(mutator);
        } else {
            ACTIVE.add(mutator);
        }
    }

    public static boolean isActive(InstanceMutator mutator) {
        return ACTIVE.contains(mutator);
    }

    public static String getActiveDisplay() {
        if (ACTIVE.isEmpty()) {
            return "None";
        }
        return ACTIVE.stream().map(Enum::name).collect(Collectors.joining(", "));
    }
}
