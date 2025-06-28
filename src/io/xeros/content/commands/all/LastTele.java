package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.content.preset.PresetManager;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class LastTele extends Commands {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getPosition().inWild()
                || Server.getMultiplayerSessionListener().inAnySession(c)
                || Boundary.isIn(c, Boundary.DUEL_ARENA)
                || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                || c.getPosition().inClanWarsSafe()
                || Boundary.isIn(c, Boundary.INFERNO)
                || c.getInstance() != null
                || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(c, Boundary.WG_Boundary)
                || Boundary.isIn(c, Boundary.RAIDS)
                || Boundary.isIn(c, Boundary.OLM)
                || Boundary.isIn(c, Boundary.RAID_MAIN)
                || Boundary.isIn(c, Boundary.XERIC)
                || Boundary.isIn(c, Boundary.VOTE_BOSS)
                || Boundary.isIn(c, Boundary.NEX)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)
                || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
            return;
        }

        if (Boundary.isIn(c, new Boundary(1664, 4224, 1727, 4287))) {
            return;
        }
        if (c.getInstance() != null) {
            return;
        }

        if (c.teleTimer > 0) {
            return;
        }
        PresetManager.getSingleton().loadLastPreset(c);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Pulls previous preset (Need to rename)");
    }
}
