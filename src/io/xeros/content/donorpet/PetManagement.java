package io.xeros.content.donorpet;

import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

import java.util.ArrayList;
import java.util.Arrays;

public class PetManagement {

    private final Player player;
    private final ArrayList<Integer> petIds = new ArrayList<>();



    public PetManagement(Player player) {
        this.player = player;
    }

    public void addPet(int petId) {
        if (player.getPetManagement().petIds.contains(petId)) {
            player.sendMessage("You already have this pet stored.");
            return;
        }

        if (Arrays.stream(PetHandler.Pets.values()).anyMatch(i -> i.getItemId() == petId)) {
            player.getPetManagement().petIds.add(petId);
            player.getItems().deleteItem2(petId, 1);
            player.sendMessage("You add a pet to your coin.");
        }
    }

    public boolean isCashMoney(int id) {
        return id == 30122;
    }

    public void removePets() {
        if (!player.getPetManagement().petIds.isEmpty()) {
            player.getPetManagement().petIds.forEach(i -> player.getItems().addItemUnderAnyCircumstance(i, 1));
            player.sendMessage("You remove your pets from your coin.");
            player.getPetManagement().petIds.clear();
        } else {
            player.sendMessage("Your donor coin, isn't holding any pets.");
        }
    }

    public void openInterface() {
        io.xeros.content.pet.PetManager.open(player);
    }


}
