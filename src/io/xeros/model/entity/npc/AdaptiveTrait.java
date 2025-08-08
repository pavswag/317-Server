package io.xeros.model.entity.npc;

import lombok.Getter;

/**
 * Definition of a randomized adaptive trait applied to bosses on spawn.
 */
@Getter
public class AdaptiveTrait {

    private final String name;
    private final String description;

    public AdaptiveTrait(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
