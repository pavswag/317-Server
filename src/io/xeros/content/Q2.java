package io.xeros.content;

import io.xeros.Server;
import io.xeros.content.achievement.inter.AchieveV2;
import io.xeros.content.combat.stats.MonsterKillLog;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.content.playerinformation.CharacterInformationPanel;
import io.xeros.model.entity.player.Player;

public class Q2 {

    public static boolean Open(Player player, int buttonId) {
        switch (buttonId) {
            case 10282:
                player.getCollectionLog().openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10283:
                Server.getDropManager().openDefault(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10284:
                LootableInterface.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10285:
                MonsterKillLog.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10286:
                CharacterInformationPanel.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10287:
                AchieveV2.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
            return true;
        }
        return false;
    }
}
