package io.xeros.content.bosses;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss2 {

    public static void tick() {
        for (Player player : PlayerHandler.getPlayers()) {
            if (player.getDonorBossKCx() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatex(), LocalDate.now())) {
                player.setDonorBossKCx(0);
                player.setDonorBossDatex(LocalDate.now());
                if (player.amDonated >= 250) {
                    player.sendMessage("You can now kill the Extreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.ALMIGHTY_DONATOR)) {
            return 12;
        } else if (player.getRights().isOrInherits(Right.APEX_DONATOR)) {
            return 6;
        } else if (player.getRights().isOrInherits(Right.PLATINUM_DONATOR)) {
            return 5;
        } else if (player.getRights().isOrInherits(Right.GILDED_DONATOR)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.SUPREME_DONATOR)) {
            return 3;
        } else if (player.getRights().isOrInherits(Right.MAJOR_DONATOR)) {
            return 2;
        } else if (player.getRights().isOrInherits(Right.EXTREME_DONATOR)) {
            return 1;
        }
        return 0;
    }
}
