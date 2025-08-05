package io.xeros.content.pet;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic pet representation used by {@link PetManager}.
 */
public class Pet {

    private int npcId;
    private int level = 1;
    private int experience = 0;
    private short skillUpPoints = 0;
    private final List<PetPerk> petPerks = new ArrayList<>();

    public Pet(int npcId) {
        this.npcId = npcId;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public short getSkillUpPoints() {
        return skillUpPoints;
    }

    public void setSkillUpPoints(short skillUpPoints) {
        this.skillUpPoints = skillUpPoints;
    }

    public List<PetPerk> getPetPerks() {
        return petPerks;
    }

    public void addPerk(PetPerk perk) {
        petPerks.add(perk);
    }
}