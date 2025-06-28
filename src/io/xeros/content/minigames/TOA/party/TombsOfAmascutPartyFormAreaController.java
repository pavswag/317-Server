package io.xeros.content.minigames.TOA.party;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Boundary;
import io.xeros.content.party.PartyFormAreaController;

import java.util.Set;

public class TombsOfAmascutPartyFormAreaController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return TombsOfAmascutParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(Boundary.TOMBS_OF_AMASCUT_LOBBY_ENTRANCE);
    }

    @Override
    public PlayerParty createParty() {
        return new TombsOfAmascutParty();
    }
}
