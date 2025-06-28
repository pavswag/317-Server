package io.xeros.content.minigames.arbograve;

import io.xeros.content.minigames.arbograve.instance.ArbograveInstance;
import io.xeros.content.minigames.arbograve.party.ArbograveParty;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ExpMode;
import io.xeros.model.entity.player.mode.group.ExpModeType;
import io.xeros.model.items.GameItem;

import java.util.List;

public class ArbograveContainer {

    private final Player player;

    public int lives;

    public ArbograveContainer(Player player) {this.player = player;}

    public boolean handleClickObject(WorldObject object) {
        if (object.getId() != 25154) {
            return false;
        }
        startArbo();
        return true;
    }

    public void startArbo() {
        if (!player.inParty(ArbograveParty.TYPE)) {
            player.sendMessage("You must be in a party to start Arbograve Swamp!");
            return;
        }
        player.getParty().openStartActivityDialogue(player, "Arbograve Swamp", ArbograveConstants.ARBO_ENTRANCE::in, list -> new ArbograveInstance(list.size()).start(list));
    }

    public boolean inArbo() {
        return player.getInstance() != null && player.getInstance() instanceof ArbograveInstance;
    }

}
