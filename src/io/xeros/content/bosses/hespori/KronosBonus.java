package io.xeros.content.bosses.hespori;

import io.xeros.content.QuestTab;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class KronosBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeKronosSeed = true;
        Hespori.KRONOS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeBugMessage("The Kronos has sprouted and is granting 1 hour of double Raids 1 keys, doubled chances at ToB & Arbograve Swap! @News-PvM");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kronos seed which" +
                " granted @red@1 hour of double Raids 1 keys");
        PlayerHandler.executeGlobalMessage("@red@                   , doubled chances at ToB & Arbograve Swap!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKronosSeed = false;
        Hespori.KRONOS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KRONOS;
    }
}
