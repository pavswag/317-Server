package io.xeros.content.pet;

import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.Player;

/**
 * Simple pet manager for opening and updating the pet interface.
 */
public class PetManager {

    public static void open(Player player) {
        updateInterface(player);
        player.getPA().showInterface(22731);
    }

    public static void updateInterface(Player player) {
        Pet pet = player.getCurrentPet();
        if (pet == null) {
            player.getPA().sendString(22747, "No pet selected");
            return;
        }
        player.getPA().sendString(22747, NpcDef.forId(pet.getNpcId()).getName());
        player.getPA().sendString(22754, "Lv: " + pet.getLevel());
        player.getPA().sendString(22755, "Xp: " + pet.getExperience());
    }

    public static void addXp(Player player, int xp) {
        Pet pet = player.getCurrentPet();
        if (pet == null) {
            return;
        }
        pet.setExperience(pet.getExperience() + xp);
        int newLevel = PetUtility.getLevelForXP(pet.getExperience());
        if (newLevel > pet.getLevel()) {
            pet.setLevel(newLevel);
            player.sendMessage("Your pet reached level " + newLevel + "!");
        }
        updateInterface(player);
    }
}