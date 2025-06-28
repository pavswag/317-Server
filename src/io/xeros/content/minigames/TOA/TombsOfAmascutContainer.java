package io.xeros.content.minigames.TOA;

import java.util.List;

import io.xeros.content.minigames.TOA.instance.TombsOfAmascutInstance;
import io.xeros.content.minigames.TOA.party.TombsOfAmascutParty;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

/**
 * Handles actions outside of tob instance.
 */
public class TombsOfAmascutContainer {

    private final Player player;

    public TombsOfAmascutContainer(Player player) {
        this.player = player;
    }

    public void displayRewardInterface(List<GameItem> rewards) {
        player.getItems().sendItemContainer(22961, rewards);
        player.getPA().showInterface(22959);
    }

    public boolean handleClickObject(WorldObject object, int option) {
        if (object.getId() != TombsOfAmascutConstants.ENTER_TOMBS_OF_AMASCUT_OBJECT_ID)
            return false;

        startTombsOfAmascut();
        return true;
    }

    public void startTombsOfAmascut() {
        if (!player.inParty(TombsOfAmascutParty.TYPE)) {
            player.sendMessage("You must be in a party to start Tombs of Amascut.");
            return;
        }

        player.getParty().openStartActivityDialogue(player, "Tombs of Amascut", TombsOfAmascutConstants.TOMBS_OF_AMASCUT_LOBBY::in, list -> new TombsOfAmascutInstance(list.size()).start(list));
    }

    public boolean handleContainerAction1(int interfaceId, int slot) {
        if (inTombsOfAmascut()) {
            return ((TombsOfAmascutInstance) player.getInstance()).getFoodRewards().handleBuy(player, interfaceId, slot);
        }
        return false;
    }

    public boolean inTombsOfAmascut() {
        return player.getInstance() != null && player.getInstance() instanceof TombsOfAmascutInstance;
    }
}
