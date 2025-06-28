package io.xeros.content.boosts.other;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Calendar;

public class BuchuBoost extends GenericBoost {

    @Override
    public String getDescription() {
        return "x2 Boss Points (" + (Hespori.BUCHU_TIMER > 0 ? Misc.cyclesToDottedTime((int) Hespori.BUCHU_TIMER) : "Sunday Bonus") + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.BUCHU_TIMER > 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
}
