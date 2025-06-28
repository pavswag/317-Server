package io.xeros.content.minigames.arbograve.party;

import io.xeros.content.minigames.raids.Raids;
import io.xeros.content.party.PartyInterface;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ExpMode;
import io.xeros.model.entity.player.mode.group.ExpModeType;

public class ArbograveParty extends PlayerParty {

    public static final String TYPE = "Arbo Party";

    public ArbograveParty() {
        super(TYPE, 15);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        if (invitedBy.connectedFrom.equals(invited.connectedFrom)) {
            invitedBy.sendErrorMessage("You can't use an alt with a main in the same region #Rules!");
            return false;
        }

        return true;
    }

    @Override
    public void onJoin(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }

    @Override
    public void onLeave(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }
}
