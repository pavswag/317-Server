package io.xeros.content.bosses;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss {



    public static void tick() {
        for (Player player : PlayerHandler.getPlayers()) {
            if (player.getDonorBossKC() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDate(), LocalDate.now())) {
                player.setDonorBossKC(0);
                player.setDonorBossDate(LocalDate.now());
                player.sendMessage("You can now kill the Donator boss!");
                player.getTaskMaster().handleDailySkips();
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.amDonated >= 3000) {
            return 15;
        } else if (player.amDonated >= 2000) {
            return 10;
        } else if (player.amDonated >= 1500) {
            return 9;
        } else if (player.amDonated >= 1500) {
            return 8;
        } else if (player.amDonated >= 1000) {
            return 6;
        } else if (player.amDonated >= 750) {
            return 5;
        } else if (player.amDonated >= 500) {
            return 4;
        } else if (player.amDonated >= 250) {
            return 3;
        } else if (player.amDonated >= 100) {
            return 2;
        } else if (player.amDonated >= 10) {
            return 1;
        }
        return 0;
    }

}
