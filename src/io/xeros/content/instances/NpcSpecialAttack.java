package io.xeros.content.instances;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Definition for a special attack an instance NPC can use.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpcSpecialAttack {
    private String name;
    /** chance between 0 and 1 */
    private double activationChance;
    /** cooldown in game ticks */
    private int cooldown;
    private int animation;
    private int gfx;
    private int sound;
    /** free form effect identifier for combat script */
    private String effect;
    /** optional combat dialogue lines */
    private List<String> messages;

    public NpcSpecialAttack copy() {
        return new NpcSpecialAttack(name, activationChance, cooldown, animation, gfx, sound, effect, messages);
    }

    public NpcSpecialAttack withAdjustedChance(double chance) {
        NpcSpecialAttack copy = copy();
        copy.setActivationChance(chance);
        return copy;
    }
}
