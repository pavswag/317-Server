package io.xeros.content.minigames.tob;

import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.content.minigames.tob.party.TobParty;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ExpMode;
import io.xeros.model.entity.player.mode.group.ExpModeType;
import io.xeros.model.items.GameItem;

import java.util.List;

/**
 * Handles actions outside of tob instance.
 */
public class TobContainer {

    private final Player player;

    public TobContainer(Player player) {
        this.player = player;
    }

    public void displayRewardInterface(List<GameItem> rewards) {
        player.getItems().sendItemContainer(22961, rewards);
        player.getPA().showInterface(22959);
    }

    public boolean handleClickObject(WorldObject object, int option) {
        if (object.getId() != TobConstants.ENTER_TOB_OBJECT_ID)
            return false;

        startTob();
        return true;
    }

    public void startTob() {
        if (!player.inParty(TobParty.TYPE)) {
            player.sendMessage("You must be in a party to start Theatre of Blood.");
            return;
        }

        player.getParty().openStartActivityDialogue(player, "Theatre of Blood", TobConstants.TOB_LOBBY::in, list -> new TobInstance(list.size()).start(list));
    }

    public boolean handleContainerAction1(int interfaceId, int slot) {
        if (inTob()) {
            return ((TobInstance) player.getInstance()).getFoodRewards().handleBuy(player, interfaceId, slot);
        }
        return false;
    }

    public boolean inTob() {
        return player.getInstance() != null && player.getInstance() instanceof TobInstance;
    }

}
