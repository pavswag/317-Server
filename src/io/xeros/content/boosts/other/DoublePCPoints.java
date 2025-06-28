package io.xeros.content.boosts.other;

import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;

import java.util.Calendar;

public class DoublePCPoints extends GenericBoost {

    @Override
    public String getDescription() {
        return "2x PC Points";
    }

    @Override
    public boolean applied(Player player) {
        return Configuration.BONUS_PC || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }
}