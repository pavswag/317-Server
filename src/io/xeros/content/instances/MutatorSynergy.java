package io.xeros.content.instances;

import java.util.List;

/**
 * Defines a multi-mutator synergy event. When all mutators in the list are
 * active a named combo effect can be triggered.
 */
public class MutatorSynergy {
    private List<InstanceMutator> mutators;
    private String name;

    public List<InstanceMutator> getMutators() { return mutators; }
    public String getName() { return name; }
}
