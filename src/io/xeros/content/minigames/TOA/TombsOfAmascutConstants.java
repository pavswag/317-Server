package io.xeros.content.minigames.TOA;

import com.google.common.collect.Lists;
import io.xeros.content.minigames.TOA.rooms.*;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

import java.util.Collections;
import java.util.List;

public class TombsOfAmascutConstants {

    public static final String TOMBS_OF_AMASCUT = "TombsOfAmascut";

    public static final Position LOBBY_TELEPORT_POSITION = new Position(3357, 9120);
    public static final Position FINISHED_TOMBS_OF_AMASCUT_POSITION = new Position(3357, 9120);

    /**
     * Lobby party overlay interface
     */
    public static final int LOBBY_WALKABLE_INTERFACE = 21_473;
    public static final int LOBBY_WALKABLE_INTERFACE_HEADER = 21_475;
    public static final int LOBBY_WALKABLE_NAME_CONTAINER = 21_476;
    public static final List<String> LOBBY_WALKABLE_EMPTY_NAME_LIST = Collections.unmodifiableList(Lists.newArrayList("-", "-", "-", "-", "-"));

    public static final int ENTER_TOMBS_OF_AMASCUT_OBJECT_ID = 46089;

    public static final int ENTER_NEXT_ROOM_OBJECT_ID = 45844;

    public static final int ENTER_NEXT_ROOM_OBJECT_ID1 = 45131;

    public static final int ENTER_NEXT_ROOM_OBJECT_ID2 = 45128;

    public static final int ENTER_FINAL_ROOM_OBJECT_ID = 32_751;

    public static final int BOSS_GATE_OBJECT_ID = 32_755;

    public static final int TREASURE_ROOM_ENTRANCE_OBJECT_ID = 43898;
    public static final int TREASURE_ROOM_EXIT_INSTANCE_OBJECT_ID = 32_996;

    public static final int FOOD_CHEST_OBJECT_ID = 29_069;

    public static final Boundary TOMBS_OF_AMASCUT_LOBBY = new Boundary(3345, 9108, 3372, 9129);

    public static final Boundary BABA_BOSS_ROOM_BOUNDARY = new Boundary(3788, 5397, 39827, 5419);
    public static final Boundary CRONDIS_BOSS_ROOM_BOUNDARY = new Boundary(3914, 5259, 3957, 5301);
    public static final Boundary APMEKEN1_BOSS_ROOM_BOUNDARY = new Boundary(3788, 5266, 3828, 5294);
    public static final Boundary AKKHA_BOSS_ROOM_BOUNDARY = new Boundary(3675, 5397, 3706, 5422,1);
    public static final Boundary KEPHRI_BOSS_ROOM_BOUNDARY = new Boundary(3542, 5400, 3559, 5416);
    public static final Boundary TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY = new Boundary(3790, 5136, 3826, 5174,2);
    public static final Boundary LOOT_ROOM_BOUNDARY = new Boundary(3667, 5135, 3696, 5174);

    public static Boundary[] ALL_BOUNDARIES1 = { BABA_BOSS_ROOM_BOUNDARY, CRONDIS_BOSS_ROOM_BOUNDARY,
            APMEKEN1_BOSS_ROOM_BOUNDARY, AKKHA_BOSS_ROOM_BOUNDARY, KEPHRI_BOSS_ROOM_BOUNDARY,
            TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY, LOOT_ROOM_BOUNDARY };

    public static final List<TombsOfAmascutRoom> ROOM_LIST1= Collections.unmodifiableList(Lists.newArrayList(
            new RoomOneBaba(), new RoomTwoCrondis(), new RoomThreeApmeken(), new  RoomFourAkkha(), new RoomFiveKephri(), new RoomSixTumekensWarden(), new RoomSevenLoot()));
    public static double getHealthModifier(final int size) {
        if (size == 5) {
            return 1;
        } else if (size == 4) {
            return 0.875;
        } else {
            return 0.75;
        }
    }
}
