package io.xeros.content.minigames.arborraid;

import com.google.common.collect.Lists;
import io.xeros.content.minigames.arborraid.rooms.*;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

import java.util.Collections;
import java.util.List;

public class ArboRaidConstants {

    public static final Position FINISHED_POSITION = new Position(2834, 3260, 0);

    public static final Boundary ARBO_RAID_ENTRANCE = new Boundary(2830, 3252, 2840, 3273);

    public static final Boundary ROOM_STARTER = new Boundary(1667,4263,1678,4275);
    public static final Boundary ROOM_ONE = new Boundary(1682,4262,1704,4275);
    public static final Boundary ROOM_TWO = new Boundary(1707,4262,1720,4275);
    public static final Boundary ROOM_THREE = new Boundary(1695,4247,1714,4259);
    public static final Boundary ROOM_FOUR = new Boundary(1673,4247,1690,4259);
    public static final Boundary ROOM_FIVE = new Boundary(1672,4228,1690,4245);

    public static final Boundary[] ALL_BOUNDARIES = {ROOM_STARTER, ROOM_ONE, ROOM_TWO, ROOM_THREE, ROOM_FOUR, ROOM_FIVE};

    public static final List<ArboRaidRoom> ROOM_LIST = Collections.unmodifiableList(
            Lists.newArrayList(new ArboStarterRoom(),
                    new ArboRoomOne(),
                    new ArboRoomTwo(),
                    new ArboRoomThree(),
                    new ArboRoomFour(),
                    new ArboRoomFive()));
}
