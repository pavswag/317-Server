package io.xeros.content.commands.donator;

import io.xeros.Server;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.commands.Command;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import static io.xeros.model.entity.player.Right.MAJOR_DONATOR;

public class Bank extends Command {

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
                || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(c, new Boundary(3117, 3640, 3137, 3644))
                || Boundary.isIn(c, new Boundary(3114, 3611, 3122, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3633, 3124, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3605, 3149, 3617))
                || Boundary.isIn(c, new Boundary(3122, 3617, 3125, 3621))
                || Boundary.isIn(c, new Boundary(3144, 3618, 3156, 3626))
                || Boundary.isIn(c, new Boundary(3155, 3633, 3165, 3646))
                || Boundary.isIn(c, new Boundary(3157, 3626, 3165, 3632))
                || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(c, Boundary.WG_Boundary)
                || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(c, Boundary.RAIDS)
                || Boundary.isIn(c, Boundary.OLM)
                || Boundary.isIn(c, Boundary.RAID_MAIN)
                || Boundary.isIn(c, Boundary.XERIC)
                || Boundary.isIn(c, Boundary.VOTE_BOSS)
                || Boundary.isIn(c, Boundary.NEX)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)
                || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) return;
        if (c.amDonated >= 500) {
            c.getPA().player.itemAssistant.openUpBank();
            c.inBank = true;
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return player.amDonated >= 20;
    }
}
