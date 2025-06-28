package io.xeros.content.minigames.TOA.party;

import io.xeros.content.party.PartyInterface;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class TombsOfAmascutParty extends PlayerParty {

    public static final String TYPE = "TombsOfAmascutParty";

    public TombsOfAmascutParty() {
        super(TYPE, 5);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
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
