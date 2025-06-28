package io.xeros.content.minigames.arbograve.party;

import io.xeros.content.party.PartyFormAreaController;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Boundary;

import java.util.Set;

import static io.xeros.content.minigames.arbograve.ArbograveConstants.*;

public class ArbogravePartyFormAreaController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return ArbograveParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(ARBO_ENTRANCE);
    }

    @Override
    public PlayerParty createParty() {
        return new ArbograveParty();
    }
}
