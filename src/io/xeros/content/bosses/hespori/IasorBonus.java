package io.xeros.content.bosses.hespori;

import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.content.QuestTab;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.discord.Discord;

public class IasorBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeIasorSeed = true;
        Hespori.IASOR_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeBugMessage("The Iasor has sprouted and is granting 1 hour of double drops! @News-PvM");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @blu@" + player.getDisplayNameFormatted() + " @bla@sprouted the Iasor and it is granting 1 hr of double drops!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeIasorSeed = false;
        Hespori.IASOR_TIMER = 0;
    }

    @Override
    public boolean canPlant(Player player) {
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.IASOR;
    }
}
