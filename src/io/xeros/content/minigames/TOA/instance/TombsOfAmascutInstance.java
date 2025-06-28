package io.xeros.content.minigames.TOA.instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.rooms.RoomSevenLoot;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class TombsOfAmascutInstance extends InstancedArea {

    public static final String TOMBS_OF_AMASCUT_DEAD_ATTR_KEY = "dead_TombsOfAmascut";
    private static final int TREASURE_ROOM_INDEX = 6;

    private final HashSet<String> chestRewards = new HashSet<>();
    private final FoodRewards foodRewards = new FoodRewards();
    private final MvpPoints mvpPoints = new MvpPoints();
    private final HashMap<String, List<GameItem>> chestRewardItems = new HashMap<>();
    private int roomIndex = -1;
    private boolean fightStarted = false;
    private boolean lastRoom;
    private final int size;

    private boolean finalBossComplete;

    public TombsOfAmascutInstance(int size) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, TombsOfAmascutConstants.ALL_BOUNDARIES1);
        this.size = size;
    }

    @Override
    public void remove(Player player) {
        if (roomIndex == TREASURE_ROOM_INDEX) {
            RoomSevenLoot.openChest(player, (TombsOfAmascutInstance) player.getInstance());
        }

        super.remove(player);
        player.getBossTimers().remove(TombsOfAmascutConstants.TOMBS_OF_AMASCUT);
    }

    public void removeButLeaveInParty(Player player) {
        super.remove(player);
    }

    public void start(List<Player> playerList) {
        if (playerList.isEmpty())
            return;
        initialiseNextRoom(playerList.get(0));
        TombsOfAmascutRoom tombsOfAmascutRoom = TombsOfAmascutConstants.ROOM_LIST1.get(0);
        playerList.forEach(plr -> {
            add(plr);
            plr.moveTo(resolve(tombsOfAmascutRoom.getPlayerSpawnPosition()));
            plr.sendMessage("Welcome to the TombsOfAmascut.");
            plr.getBossTimers().track(TombsOfAmascutConstants.TOMBS_OF_AMASCUT);
            plr.getPA().closeAllWindows();
        });
    }

    private void initialiseNextRoom(Player player) {
        roomIndex = getPlayerRoomIndex(player) + 1;  // whats the index do , its like it just doesnt register their being a room 4
        TombsOfAmascutRoom tombsOfAmascutRoom = TombsOfAmascutConstants.ROOM_LIST1.get(roomIndex);
        var boss = tombsOfAmascutRoom.spawn(this);
        if (boss != null) {
            var modifier = TombsOfAmascutConstants.getHealthModifier(size);
            var maxHealth = (int) (boss.getHealth().getMaximumHealth() * modifier);
            boss.getHealth().setCurrentHealth(maxHealth);
            boss.getHealth().setMaximumHealth(maxHealth);
        }
        GlobalObject foodChest = tombsOfAmascutRoom.getFoodChestPosition();
        if (foodChest != null) {
            Server.getGlobalObjects().add(foodChest.withHeight(resolveHeight(foodChest.getHeight())).setInstance(this));
        }
        fightStarted = false;
    }

    public void moveToNextRoom(Player player) {
        int nextRoomIndex = getPlayerRoomIndex(player) + 1;

        if (roomIndex < nextRoomIndex) {
            initialiseNextRoom(player);
        }


        player.healEverything();
        Position playerSpawnPosition = resolve(TombsOfAmascutConstants.ROOM_LIST1.get(nextRoomIndex).getPlayerSpawnPosition()); // its this, so delete ? no cus it wont tele you anymore
        System.out.println("moveToNextRoom, roomIndex:"+roomIndex+", nextRoomIndex:"+nextRoomIndex+", "+playerSpawnPosition.getFormattedString());
        player.moveTo(playerSpawnPosition);  // how i fix lol ?>
        player.getAttributes().removeBoolean(TOMBS_OF_AMASCUT_DEAD_ATTR_KEY);
    }

    private int getPlayerRoomIndex(Player player) {
        for (int index = 0; index < TombsOfAmascutConstants.ALL_BOUNDARIES1.length; index++) {
            if (TombsOfAmascutConstants.ALL_BOUNDARIES1[index].in(player)) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        if (object.getId() == TombsOfAmascutConstants.BOSS_GATE_OBJECT_ID || object.getId() == TombsOfAmascutConstants.ENTER_FINAL_ROOM_OBJECT_ID) {
            if (getPlayerRoomIndex(player) == roomIndex && !getCurrentRoom().isRoomComplete(this)
                    || lastRoom && object.getId() == TombsOfAmascutConstants.ENTER_FINAL_ROOM_OBJECT_ID) {// In last unlocked room and fight not completed
                if (!fightStarted || lastRoom) {
                    if (player.equals(player.getInstance().getPlayers().get(0))) {
                        player.start(new DialogueBuilder(player).option(new DialogueOption("Start fight", this::startFight),
                                new DialogueOption("Cancel", plr -> plr.getPA().closeAllWindows())));
                    } else {
                        player.sendMessage("Only the party leader can start a fight.");
                    }
                } else {
                    if (player.getAttributes().getBoolean(TOMBS_OF_AMASCUT_DEAD_ATTR_KEY)) {

                        player.sendMessage("You've been disqualified from the fight for dying, you must wait.");
                    } else {
                        player.sendMessage("The fight has started, there's no turning back now.");
                    }
                }
            } else {                                                                                                          // In room before last unlocked or room complete
                Optional<TombsOfAmascutRoom> gateRoomOptional = TombsOfAmascutConstants.ROOM_LIST1.stream().filter(gateRoom -> gateRoom.getBoundary().in(player)).findFirst();
                gateRoomOptional.ifPresent(tombsOfAmascutRoom -> gateRoomOptional.get().handleClickBossGate(player, object));
            }

            return true;
        }

        for (TombsOfAmascutRoom room : TombsOfAmascutConstants.ROOM_LIST1) {
            if (room.handleClickObject(player, object, option)) {
                return true;
            }
        }

        switch (object.getId()) {
            case TombsOfAmascutConstants.TREASURE_ROOM_ENTRANCE_OBJECT_ID:
            case TombsOfAmascutConstants.ENTER_NEXT_ROOM_OBJECT_ID:
            case TombsOfAmascutConstants.ENTER_NEXT_ROOM_OBJECT_ID1:
            case TombsOfAmascutConstants.ENTER_NEXT_ROOM_OBJECT_ID2:
                moveToNextRoom(player);
                return true;
            case TombsOfAmascutConstants.FOOD_CHEST_OBJECT_ID:
                foodRewards.openFoodRewards(player);
                return true;
        }

        return false;
    }
    @Override
    public boolean handleDeath(Player player) {
        int roomIndex = getPlayerRoomIndex(player);
        if (roomIndex == -1) {
            player.moveTo(TombsOfAmascutConstants.FINISHED_TOMBS_OF_AMASCUT_POSITION);
            player.sendMessage("Could not handle death!");
            return true;
        }

        TombsOfAmascutRoom room = TombsOfAmascutConstants.ROOM_LIST1.get(getPlayerRoomIndex(player));
        player.moveTo(resolve(room.getDeathPosition()));
        player.sendMessage("Oh dear, you have died!");
        player.getAttributes().setBoolean(TOMBS_OF_AMASCUT_DEAD_ATTR_KEY, true);

        if (allDead()) {
            Lists.newArrayList(getPlayers()).forEach(plr -> {
                plr.moveTo(TombsOfAmascutConstants.FINISHED_TOMBS_OF_AMASCUT_POSITION);
                removeButLeaveInParty(plr);
                plr.sendMessage("Your performance in the theatre left much to be desired; your team has been defeated.");
            });
        }
        return true;
    }

    private void startFight(Player player) {
        if (getPlayers().stream().allMatch(plr -> getPlayerRoomIndex(plr) == roomIndex)) {
            fightStarted = true;
            TombsOfAmascutRoom currentRoom = getCurrentRoom();
            getPlayers().forEach(plr -> {
                if (lastRoom)
                    moveToNextRoom(plr);
                else
                    plr.moveTo(resolve(currentRoom.getFightStartPosition()));
                plr.getPA().closeAllWindows();
            });
            lastRoom = false;
        } else {
            player.sendStatement("All players must be in the room to start the fight.");
        }
    }

    private boolean allDead() {
        return getPlayers().stream().allMatch(plr -> plr.getAttributes().getBoolean(TOMBS_OF_AMASCUT_DEAD_ATTR_KEY));
    }

    @Override
    public void onDispose() { }

    private TombsOfAmascutRoom getCurrentRoom() {
        return TombsOfAmascutConstants.ROOM_LIST1.get(roomIndex);
    }

    public HashSet<String> getChestRewards() {
        return chestRewards;
    }

    public FoodRewards getFoodRewards() {
        return foodRewards;
    }

    public MvpPoints getMvpPoints() {
        return mvpPoints;
    }

    public MvpPoints getChooseRareWinner() {
        return mvpPoints;
    }

    public HashMap<String, List<GameItem>> getChestRewardItems() {
        return chestRewardItems;
    }

    public int getPartySize() {
        return size;
    }

    public void setLastRoom(boolean lastRoom) {
        this.lastRoom = lastRoom;
    }

    public boolean isFinalBossComplete() {
        return finalBossComplete;
    }

    public void setFinalBossComplete(boolean finalBossComplete) {
        this.finalBossComplete = finalBossComplete;
    }

}
