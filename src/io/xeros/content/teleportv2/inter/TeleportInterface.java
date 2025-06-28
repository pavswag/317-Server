package io.xeros.content.teleportv2.inter;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.item.lootable.impl.ArbograveChest;
import io.xeros.content.item.lootable.impl.ArbograveChestItems;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.items.GameItem;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;


public class TeleportInterface {

    public static final int[] FAVOURITE_BUTTON_IDS = {31009,31010,31011,31012,31013,31014,31015};

    public static final int[] TELEPORT_BUTTONS = {31066,31069,31072,31075,31078,31081,31084,31087,31090,31093,31096,31099,31102,31105,31108,31111,31114,31117,31120,31123,31126,31129,31132,31135,31138,31141,31144,31147,31150,31153};

    public static final int[] MARK_FAVOURITE = {31068, 31071, 31074, 31077, 31080, 31083, 31086, 31089, 31092, 31095, 31098, 31101, 31104, 31107, 31110, 31113, 31116, 31119, 31122, 31125, 31128, 31131, 31134, 31137, 31140, 31143, 31146, 31149, 31152, 31155};

    public static void resetOldData(Player player) {
        player.setCurrentTeleportTab(0);
        player.setCurrentTeleportClickIndex(0);
        for (int i = 0; i < 108; i++) {
            player.getPA().itemOnInterface(-1, 1, 31018,i);
        }
    }

    public static void open(Player player) {
        switch (player.getCurrentTeleportTab()) {
            case 0:
                sendMonsterTab(player);
                break;
            case 1:
                sendBossTab(player);
                break;
            case 2:
                sendMinigamesTab(player);
                break;
            case 3:
                sendDungeonsTab(player);
                break;
            case 4:
                sendMiscTab(player);
                break;
            case 5:
                sendPKTab(player);
                break;
            default:
                sendMonsterTab(player);
                break;
        }
    }

    public static boolean handleButton(Player player, int buttonID) {
        switch (buttonID) {
            case 31002:
                player.getPA().removeAllWindows();
                player.getPA().closeAllWindows();
                return true;
            case 31036:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendMonsterTab(player);
                return true;
            case 31037:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendBossTab(player);
                return true;
            case 31038:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendMinigamesTab(player);
                return true;
            case 31039:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendDungeonsTab(player);
                return true;
            case 31040:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendPKTab(player);
                return true;
            case 31041:
                cleanList(player);
                player.getPA().sendString(31006, "Locations");
                TeleportInterface.sendMiscTab(player);
                return true;
            case 31016:
                if (player.getCurrentTeleportClickIndex() >= 0) {
                    if (player.getCurrentTeleportTab() == 0 && player.getCurrentTeleportClickIndex() < MONSTERS.values().length) {
                        MONSTERS monsterData = MONSTERS.values()[player.getCurrentTeleportClickIndex()];
                        handleMonsterTeleport(player, monsterData);
                    } else if (player.getCurrentTeleportTab() == 1 && player.getCurrentTeleportClickIndex() < BOSSES.values().length) {
                        BOSSES bossData = BOSSES.values()[player.getCurrentTeleportClickIndex()];
                        handleBossTeleport(player, bossData);
                    } else if (player.getCurrentTeleportTab() == 2 && player.getCurrentTeleportClickIndex() < MINIGAMES.values().length) {
                        MINIGAMES minigameData = MINIGAMES.values()[player.getCurrentTeleportClickIndex()];
                        handleMinigameTeleport(player, minigameData);
                    } else if (player.getCurrentTeleportTab() == 3 && player.getCurrentTeleportClickIndex() < DUNGEONS.values().length) {
                        DUNGEONS wildyData = DUNGEONS.values()[player.getCurrentTeleportClickIndex()];
                        handleDungeonsTeleport(player, wildyData);
                    } else if (player.getCurrentTeleportTab() == 4 && player.getCurrentTeleportClickIndex() < SKILLING.values().length) {
                        SKILLING miscData = SKILLING.values()[player.getCurrentTeleportClickIndex()];
                        handleMiscTeleport(player, miscData);
                    } else if (player.getCurrentTeleportTab() == 5 && player.getCurrentTeleportClickIndex() < PK.values().length) {
                        PK pkData = PK.values()[player.getCurrentTeleportClickIndex()];
                        handlePKTeleport(player, pkData);
                    }
                } else {
                    player.sendMessage("@red@You've not selected a teleport yet.");
                }
                return true;
            case 31005:
                if (player.getPreviousTeleport().size() == 0) {
                    player.sendMessage("You need to have teleported somewhere first.");
                    return true;
                }
                Teleport data = player.getPreviousTeleport().get(0);
                if (data != null) {
                    if (data instanceof MONSTERS) {
                        handleMonsterTeleport(player, (MONSTERS) data);
                    } else if (data instanceof BOSSES) {
                        handleBossTeleport(player, (BOSSES) data);
                    } else if (data instanceof MINIGAMES) {
                        handleMinigameTeleport(player, (MINIGAMES) data);
                    } else if (data instanceof DUNGEONS) {
                        handleDungeonsTeleport(player, (DUNGEONS) data);
                    } else if (data instanceof SKILLING) {
                        handleMiscTeleport(player, (SKILLING) data);
                    } else if (data instanceof PK) {
                        handlePKTeleport(player, (PK) data);
                    }
                }
                return true;
        }
        for (int i : MARK_FAVOURITE) {
            if (buttonID == i) {
                int index = ArrayUtils.indexOf(MARK_FAVOURITE, i);
                Teleport data = null;
                if (player.getCurrentTeleportTab() == 0 && index < MONSTERS.values().length) {
                    data = MONSTERS.values()[index];
                } else if (player.getCurrentTeleportTab() == 1 && index < BOSSES.values().length) {
                    data = BOSSES.values()[index];
                } else if (player.getCurrentTeleportTab() == 2 && index < MINIGAMES.values().length) {
                    data = MINIGAMES.values()[index];
                } else if (player.getCurrentTeleportTab() == 3 && index < DUNGEONS.values().length) {
                    data = DUNGEONS.values()[index];
                } else if (player.getCurrentTeleportTab() == 4 && index < SKILLING.values().length) {
                    data = SKILLING.values()[index];
                } else if (player.getCurrentTeleportTab() == 5 && index < PK.values().length) {
                    data = PK.values()[index];
                }

                if (data != null) {
                    if (!player.getFavoriteTeleports().contains(data)) {
                        if (player.getFavoriteTeleports().size() >= 9) {
                            player.sendMessage("@red@Your favorites section is full.");
                            updateTab(player);
                            return true;
                        }
                        player.getFavoriteTeleports().add(data);
//                            player.sendMessage("Added favorite");
                    } else {
                        player.getFavoriteTeleports().remove(data);
//                            player.sendMessage("Removed favorite");
                    }
                    showFavorites(player);
                }
                updateTab(player);
                return true;
            }
        }


        if (buttonID >= 31007 && buttonID <= 31015) {
            int index = (buttonID - 31007);
            if (index < player.getFavoriteTeleports().size()) {
                Teleport data = player.getFavoriteTeleports().get(index);
                if (data != null) {
                    if (data instanceof MONSTERS) {
                        handleMonsterTeleport(player, (MONSTERS) data);
                    } else if (data instanceof BOSSES) {
                        handleBossTeleport(player, (BOSSES) data);
                    } else if (data instanceof MINIGAMES) {
                        handleMinigameTeleport(player, (MINIGAMES) data);
                    } else if (data instanceof DUNGEONS) {
                        handleDungeonsTeleport(player, (DUNGEONS) data);
                    } else if (data instanceof SKILLING) {
                        handleMiscTeleport(player, (SKILLING) data);
                    } else if (data instanceof PK) {
                        handlePKTeleport(player, (PK) data);
                    }
                    player.getPreviousTeleport().clear();
                }
            }
            return true;
        }
        for (int teleportButton : TELEPORT_BUTTONS) {
            if (buttonID == teleportButton) {
                int index = ArrayUtils.indexOf(TELEPORT_BUTTONS, buttonID);
                player.setCurrentTeleportClickIndex(index);
                if (player.getCurrentTeleportTab() == 0 && player.getCurrentTeleportClickIndex() < MONSTERS.values().length) {
                    MONSTERS monsterData = MONSTERS.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, monsterData.npcID);
                    player.getPA().sendString(31006, "Locations -> "+monsterData.name);
                } else if (player.getCurrentTeleportTab() == 1 && player.getCurrentTeleportClickIndex() < BOSSES.values().length) {
                    BOSSES bossData = BOSSES.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, bossData.npcID);
                    player.getPA().sendString(31006, "Locations -> "+bossData.name);
                } else if (player.getCurrentTeleportTab() == 2 && player.getCurrentTeleportClickIndex() < MINIGAMES.values().length) {
                    MINIGAMES minigameData = MINIGAMES.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, minigameData.npcID);
                    player.getPA().sendString(31006, "Locations -> "+minigameData.name);
                    if (minigameData.name.equalsIgnoreCase("blast furnace")) {
                        player.sendMessage("@red@Disconnecting with ores or bars in the system, will delete them!");
                    }
                } else if (player.getCurrentTeleportTab() == 3 && player.getCurrentTeleportClickIndex() < DUNGEONS.values().length) {
                    DUNGEONS wildyData = DUNGEONS.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, wildyData.npcID);
                    if (wildyData.name.equalsIgnoreCase("Donator bossing")) {
                        if(player.amDonated < 19) {
                            player.sendMessage("@red@You need to have a donator rank to teleport here!");
                            return false;
                        }
                    }
                    player.getPA().sendString(31006, "Locations -> "+wildyData.name);
                } else if (player.getCurrentTeleportTab() == 4 && player.getCurrentTeleportClickIndex() < SKILLING.values().length) {
                    SKILLING miscData = SKILLING.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, miscData.npcID);
                    player.getPA().sendString(31006, "Locations -> "+miscData.name);
                } else if (player.getCurrentTeleportTab() == 5 && player.getCurrentTeleportClickIndex() < PK.values().length) {
                    PK pkData = PK.values()[player.getCurrentTeleportClickIndex()];
                    sendDrops(player, pkData.npcID);
                    player.getPA().sendString(31006, "Locations -> "+pkData.name);
                }
                player.getPreviousTeleport().clear();
                return true;
            }
        }

        return false;
    }

    public static void updateTab(Player player) {
        switch (player.getCurrentTeleportTab()) {
            case 0:
                sendMonsterTab(player);
                break;
            case 1:
                sendBossTab(player);
                break;
            case 2:
                sendMinigamesTab(player);
                break;
            case 3:
                sendDungeonsTab(player);
                break;
            case 4:
                sendMiscTab(player);
                break;
            case 5:
                sendPKTab(player);
                break;
        }
    }

    public static void handleBossTeleport(Player player, BOSSES bossData) {
        player.getPreviousTeleport().add(bossData);
        Position data = new Position(bossData.teleportCords[0], bossData.teleportCords[1], bossData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void handleMonsterTeleport(Player player, MONSTERS monsterData) {
        player.getPreviousTeleport().add(monsterData);
        Position data = new Position(monsterData.teleportCords[0], monsterData.teleportCords[1], monsterData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void handleDungeonsTeleport(Player player, DUNGEONS wildyData) {
        player.getPreviousTeleport().add(wildyData);
        Position data = new Position(wildyData.teleportCords[0], wildyData.teleportCords[1], wildyData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void handleMiscTeleport(Player player, SKILLING miscData) {
        player.getPreviousTeleport().add(miscData);
        Position data = new Position(miscData.teleportCords[0], miscData.teleportCords[1], miscData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void handleMinigameTeleport(Player player, MINIGAMES minigameData) {
        player.getPreviousTeleport().add(minigameData);
        Position data = new Position(minigameData.teleportCords[0], minigameData.teleportCords[1], minigameData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void handlePKTeleport(Player player, PK pkData) {
        player.getPreviousTeleport().add(pkData);
        Position data = new Position(pkData.teleportCords[0], pkData.teleportCords[1], pkData.teleportCords[2]);
        player.getPA().startTeleport(data,"modern", false);
    }

    public static void showFavorites(Player player) {
        int id = 31007;
        for (int i = 0; i < 9; i++) {
            if (player.getFavoriteTeleports().size() > i) {
                player.getPA().sendString(id, player.getFavoriteTeleports().get(i).getName());
            } else {
                player.getPA().sendString(id, "");
            }
            id++;
        }
    }


    public static void setUp(Player player, int index) {
        resetOldData(player);
        player.setCurrentTeleportTab(index);
        showFavorites(player);
    }

    public static void cleanList(Player player) {
        int id = 31067;
        int config = 31068;

        for (int i = 0; i < 30; i++) {
            player.getPA().sendString(id, "");
            player.getPA().sendChangeSprite(config, (byte) 0);
            config += 3;
            id += 3;
        }
        
    }

    public static void showList(Player player, Teleport[] list) {
        int id = 31067;
        int config = 31068;
        for (Teleport data : list) {
            player.getPA().sendString(id, data.getName());
            if (player.getFavoriteTeleports().contains(data))
                player.getPA().sendChangeSprite(config, (byte) 1);
            else
                player.getPA().sendChangeSprite(config, (byte) 0);
            config += 3;
            id += 3;
        }
        player.getPA().showInterface(31000);

        SortedMap<String, BOSSES> map = new TreeMap<String, BOSSES>();
        for (BOSSES value : BOSSES.values()) {
            map.put(value.name, value);
        }
    }

    public static void sendDrops(Player player, int npcId) {
        for (int i = 0; i < 108; i++) {
            player.getPA().itemOnInterface(-1,1,31018,i);
        }
        if (npcId == -1) {
            return;
        }
        try {
            if (npcId == 8374) {
                for (int i = 0; i < TheatreOfBloodChest.getAllDrops().size(); i++) {
                    player.getPA().itemOnInterface(TheatreOfBloodChest.getAllDrops().get(i).getId(), TheatreOfBloodChest.getAllDrops().get(i).getAmount(), 31018, i);
                }
                return;

            } else if (npcId == 7_519) {
                for (int i = 0; i < RaidsChestRare.getAllRaidsDrops().size(); i++) {
                    player.getPA().itemOnInterface(RaidsChestRare.getAllRaidsDrops().get(i).getId(), RaidsChestRare.getAllRaidsDrops().get(i).getAmount(), 31018, i);
                }
                return;


            } if (npcId == 6477) {
                    for (int i = 0; i < ArbograveChestItems.getAllDrops().size(); i++) {
                        player.getPA().itemOnInterface(ArbograveChestItems.getAllDrops().get(i).getId(), ArbograveChestItems.getAllDrops().get(i).getAmount(), 31018, i);
                    }
                    return;

            } else if (Server.getDropManager().getAllNPCdrops(npcId) == null) {
                return;
            }

            else if (npcId  ==  1672) {
                List<GameItem> newList = new ArrayList<>();
                newList.addAll(Server.getDropManager().getAllNPCdrops(1672));
                newList.addAll(Server.getDropManager().getAllNPCdrops(1973));
                newList.addAll(Server.getDropManager().getAllNPCdrops(1674));
                newList.addAll(Server.getDropManager().getAllNPCdrops(1675));
                newList.addAll(Server.getDropManager().getAllNPCdrops(1676));
                newList.addAll(Server.getDropManager().getAllNPCdrops(1677));

                Set<Integer> ids = new HashSet<>();
                List<GameItem> uniqueList = new ArrayList<>();

                for (GameItem gameItem : newList) {
                    if (ids.add(gameItem.getId())) {
                        uniqueList.add(gameItem);
                    }
                }

                newList = uniqueList;

                if(!newList.isEmpty()) {
                    for (int i = 0; i < newList.size(); i++) {
                        player.getPA().itemOnInterface(newList.get(i).getId(), newList.get(i).getAmount(),31018,i);
                    }
                }
            }

            for (int i = 0; i < Server.getDropManager().getAllNPCdrops(npcId).size(); i++) {
                player.getPA().itemOnInterface(Server.getDropManager().getAllNPCdrops(npcId).get(i).getId(),Server.getDropManager().getAllNPCdrops(npcId).get(i).getAmount(),31018,i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void sendMonsterTab(Player player) {
        if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))){
            sendPKTab(player);
            return;
        }
        setUp(player, 0);
        player.getPA().sendChangeSprite(31036, (byte) 1);
        showList(player, MONSTERS.values());
    }

    public static void sendBossTab(Player player) {
        if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))){
            sendPKTab(player);
            return;
        }
        setUp(player, 1);
        showList(player, BOSSES.values());
    }

    public static void sendMinigamesTab(Player player) {
        if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))){
            sendPKTab(player);
            return;
        }
        setUp(player, 2);
        showList(player, MINIGAMES.values());
    }

    public static void sendDungeonsTab(Player player) {
        if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))){
            sendPKTab(player);
            return;
        }
        setUp(player, 3);
        showList(player, DUNGEONS.values());
    }

    public static void sendMiscTab(Player player) {
        if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))){
            sendPKTab(player);
            return;
        }
        setUp(player, 4);
        showList(player, SKILLING.values());
    }

    public static void sendPKTab(Player player) {
        setUp(player, 5);
        showList(player, PK.values());
    }

    public enum MONSTERS implements Teleport {

        ROCKCRAB("Rock Crabs", 100, new int[]{2673, 3710, 0}),
        SANCRAB("Sand Crabs", 5935, new int[]{1748, 3471, 0}),
        COWS("Cows", 5842, new int[]{3260, 3272, 0}),
        BOBSISLAND("Bob's Island", 1650, new int[]{2524, 4775, 0}),
        JORMUNGANDSPRISON("Jormungand's Prison", 1750, new int[]{2471, 10420, 0}),
        ELFWARRIOR("Elf Warriors", 3428, new int[]{2897, 2725, 0}),
        DAGANNOTHS("Dagannoths", 970, new int[]{2442, 10147, 0}),
        MITHRILDRAGONS("Mithril Dragons", 2919,new int[]{1740, 5342, 0})

        ;

        private final String name;

        private final int npcID;
        private final int[] teleportCords;

        MONSTERS(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }

    }


    public enum BOSSES implements Teleport {
        LIGHTBEARER("Lightbearer Wizard Knight", 1025, new int[]{2464, 5707, 0}),
        GHOST("Ghost of Darkness", 1429, new int[]{3084, 5210, 0}),
        CRYO("Cryomancer Ranger", 1656, new int[]{2317, 6369, 0}),
        ARRAV("Arrav", 1658, new int[]{3196, 4566, 0}),
        VARDORVIS("Vardorvis", 12223, new int[]{1129, 3413, 0}),//DONE
        THE_WHISPERER("The Whisperer", 12205, new int[]{2656, 6404, 0}),//DONE
        THE_LEVIATHAN("The Leviathan", 12214, new int[]{2067, 6368, 0}),//DONE
        DUKE_SUCELLUS("Duke Sucellus", 12191, new int[]{3039, 6431, 0}),//DONE
        BARREL_CHEST("Barrelchest", 6342, new int[]{2903, 3612, 0}),
        BRYOPHYTA("Bryophyta", 8195, new int[]{3174, 9898, 0}),
        OBOR("Obor", 7416, new int[]{3097, 9833, 0}),
        DAGANNOTH_KINGS("Dagannoth Kings", 2265, new int[]{1913, 4367, 0}),
        GIANT_MOLE("Giant Mole", 5779, new int[]{2993, 3376, 0}),
        KALPHITE_QUEEN("Kalphite Queen", 965, new int[]{3507, 9494, 0}),
        LIZARDMAN_SHAMAN("Lizardman Shaman", 6766, new int[]{1465, 3685, 0}),
        SARACHNIS("Sarachnis", 8713, new int[]{1842, 9926, 0}),
        GROTESQUE_GUARDIANS("Grotesque Guardians", 7851, new int[]{3428, 3541, 2}),
        NEX("Nex", 11278, new int[]{2906, 5203, 0}),
        THERMO("Thermo Smoke Devil", 499, new int[]{2404, 9415, 0}),
        KRAKEN("Kraken", 494, new int[]{2280, 10016, 0}),
        DEMONIC_GORILLA("Demonic Gorillas", 7144, new int[]{2124, 5660, 0}),
        CORPOREAL_BEAST("Corporeal Beast", 319, new int[]{2964, 4382, 2}),
        ZULRAH("Zulrah", 2043, new int[]{2203, 3056, 0}),
        CERBERUS("Cerberus", 5862, new int[]{1310, 1248, 0}),
        ABYSSAL_SIRE("Abyssal Sire", 5890, new int[]{3038, 4767, 0}),
        VORKATH("Vorkath", 8028, new int[]{2272, 4050, 0}),//2272, 4050 orig coords broke atm
        ALCHEMICAL_HYDRA("Alchemical hydra", 8622, new int[]{1354, 10259, 0}),
        THE_NIGHTMARE("The Nightmare", 9425, new int[]{3808, 9755, 1}),

        ;

        private final int[] teleportCords;

        private final int npcID;
        private final String name;

        BOSSES(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum MINIGAMES implements Teleport {

        ARBOGRAVE_SWAMP("Arbograve Swamp", 6477, new int[]{2834, 3261, 0}),
        PERKFINDER("Perk Finder", 1227, new int[]{3363, 9640, 0}),
        FORTIS_COLOSSEUM("Fortis Colosseum", 12821, new int[]{1825, 3099, 0}),
        TOA("Tombs of Amascut", 8374, new int[]{3357, 9120, 0}),
        TOB("Theatre of Blood", 8374, new int[]{3671, 3219, 0}),
        COX("Chambers of Xeric", 7_519, new int[]{1234, 3567, 0}),
        FIGHT_CAVES("Fight Caves", 3127, new int[]{2444, 5179, 0}),
        INFERNO("The Inferno", 7706, new int[]{2494, 5113, 0}),
        BARROWS("Barrows", 1672, new int[]{3565, 3316, 0}),
        DUEL_ARENA("Duel Arena", 2659, new int[]{3366, 3266, 0}),
        WARRIORS_GUILD("Warriors Guild", 2456, new int[]{2874, 3546, 0}),
        PEST_CONTROL("Pest Control", 1734, new int[]{2660, 2648, 0}),
        PURO_PURO("Puro Puro", 1644, new int[]{2594, 4320, 0}),
//        CLAN_WARS("Clan Wars", 2661, new int[]{3387, 3158, 0}),
        OUTLAST("Tournaments", 2662, new int[]{3109, 3481, 0}),
        MAGE_ARENA("Mage Arena", 1610, new int[]{2541, 4716, 0}),
        HORROR_FROM_THE_DEEP("Horror From The Deep", 983, new int[]{2508, 3641, 0}),
        MONKEY_MADNESS("Monkey Madness", 1443, new int[]{2465, 3495, 0}),
        WINTERTODT("Wintertodt", 7371, new int[]{1630,3949,0}),
        BLASTFURNACE("Blast Furnace", 6602, new int[]{1942, 4959,0}),
        PYRAMID_PLUNDER("Pyramid Plunder", 6602, new int[]{3288, 2786, 0}),
        ARIEL_FISHING("Molch Lake", 8521, new int[]{1368, 3630, 0}),


        ;

        private final String name;

        private final int npcID;
        private final int[] teleportCords;

        MINIGAMES(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum DUNGEONS implements Teleport {

        DONATORBOSS("Donator Bossing Dungeon! + Revs", 7937, new int[]{2338, 9800, 0}),
        SMOKEDEVILS("Smoke Devils", 498, new int[]{2404, 9415, 0}),
        SLAYERTOWER("Slayer Tower", 414, new int[]{3428, 3538, 0}),
        FREMDUNG("Frem. Slayer Dungeon", 1047, new int[]{2807, 10002, 0}),
        TAVERLYDUNGEON("Taverly Dungeon", 268, new int[]{2883, 9800, 0}),
        STRONGHOLDCAVE("Stronghold Cave", 484, new int[]{2452, 9820, 0}),
        MOUNTKARUULM("Mount Karuulm", 8610, new int[]{1311, 3795, 0}),
        ASGARNIANICE_DUNGEON("Asgarnian Ice Dungeon", 2085, new int[]{3053, 9578, 0}),
        BRIMHAVENDUNGEON("Brimhaven Dungeon", 1432, new int[]{2709, 9476, 0}),
        LITHREKVAULT("Lithkren Vault", 8031, new int[]{1567, 5074, 0}),
        FORTHOSDUNGEON("Forthos Dungeon", 2145, new int[]{1800, 9948, 0}),
        CRYSTALCAVERN("Crystal Cavern", 9026, new int[]{3272, 6052, 0}),
        DEATHPLATEAU("Death Plateau", 936, new int[]{2867, 3594, 0}),
        BRINERATCAVERN("Brine Rat Cavern", 4501, new int[]{2722, 10133, 0}),
        MOS_LEHAMLESS_CAVE("Mos Leharmless Cave", 1047, new int[]{3747, 9374, 0}),
        SHAMAN_TEMPLE("Shaman Temple", 8565, new int[]{1292, 10064, 0}),
        GWD("Godwars Dungeon", 2215, new int[]{2881,5310,2}),
        KOUREND_CATACOMBS("Kourend Catacombs", 7277,new int[]{1665,10049,0}),
        ;

        private final String name;

        private final int npcID;
        private final int[] teleportCords;

        DUNGEONS(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    public enum SKILLING implements Teleport {

        VARLAMORE( "Varlamore",306,  new int[]{1681, 3106, 0}),

        SKILLTP( "Enchanted Valley Skilling",306,  new int[]{3041, 4529, 0}),
                //AGILITY
        AGIGNOME( "Gnome Stronghold (1)",306,  new int[]{2474, 3437, 0}),
        //AGIPYRAMID( "Agility Pyramid (30)",306,  new int[]{3355, 2829, 0}),             //Needs coded
        AGIBARB( "Barbarian Outpost (35)",306,  new int[]{2552, 3556, 0}),
        //AGIPRIFF( "Prifddinas (75)",306,  new int[]{3210, 6078, 0}),                    //Needs coded

                //ROOFTOP AGILITY
        AGIDRAY( "Draynor (1)",306,  new int[]{3104, 3279, 0}),
        AGIKHARID( "Al Kharid (20)",306,  new int[]{3273, 3197, 0}),
        AGIVARR( "Varrock (30",306,  new int[]{3223, 3414, 0}),
        AGICANI( "Canifis (40)",306,  new int[]{3506, 3487, 0}),
        AGIFALLY( "Falador (50)",306,  new int[]{3036, 3340, 0}),
        //AGISEERS( "Seers (60)",306,  new int[]{2729, 3488, 0}),                //Unable to see roof texture
        //AGIPOLLY( "Pollnivneach (70)",306,  new int[]{3351, 2961, 0}),
        //AGIRELL( "Rellekka (80)",306,  new int[]{2624, 3678, 0}),                         //Needs coded
        AGIARDY( "Ardougne (90)",306,  new int[]{2673, 3297, 0}),

                //COOKING
        COOKGUILD( "Cooking Guild",306,  new int[]{3146, 3451, 0}),
        COOKHOMEBANK( "Varlamore Bank",306,  new int[]{1648, 3116, 0}),

                //CRAFTING
        CRAFTGUILD( "Crafting Guild",306,  new int[]{2933, 3285, 0}),
        //CRAFTHOMEBANK( "Varlamore Bank",306,  new int[]{1648, 3116, 0}),

                //FARMING ALOTTMENTS
        FARMARDY( "Ardougne Alottment",306,  new int[]{2674, 3375, 0}),
        FARMCANI( "Canifis Alottment",306,  new int[]{3603, 3532, 0}),
        FARMCATH( "Catherby Alottment",306,  new int[]{2806, 3463, 0}),
        FARMFALLY( "Falador Alottment",306,  new int[]{3053, 3304, 0}),

                //FARMING TREE PATCHES
        FARMFALLYPARK( "Falador Park",306,  new int[]{3001, 3375, 0}),
        FARMGNOMET( "Gnome Stronghold",306,  new int[]{2436, 3412, 0}),
        FARMLUMBY( "Lumbridge",306,  new int[]{3196, 3232, 0}),
        FARMTAV( "Taverly",306,  new int[]{2936, 3441, 0}),
        FARMVARR( "Varrock",306,  new int[]{3229, 3456, 0}),

                //FRUIT TREE PATCHES
        FARMBRIM( "Brimhaven",306,  new int[]{2765, 3215, 0}),
        FARMCATHB( "Catherby Beach",306,  new int[]{2858, 3433, 0}),
        FARMGNOMEF( "Gnome Stronghold",306,  new int[]{2475, 3443, 0}),

                //FISHING SPOTS
        //FISHDRAY( "Gnome Stronghold",306,  new int[]{3087, 3229, 0}),           //Needs coded
        //FISHBARBV( "Barbarian Village River",306,  new int[]{3108, 3433, 0}),     //Needs coded
        //FISHBARBV( "Gnome Stronghold",306,  new int[]{2475, 3443, 0}),
        //FISHBARBV( "Gnome Stronghold",306,  new int[]{2475, 3443, 0}),
        //FISHBARBV( "Gnome Stronghold",306,  new int[]{2475, 3443, 0}),
        //FISHBARBV( "Gnome Stronghold",306,  new int[]{2475, 3443, 0}),
        FARMING( "Farming",306,  new int[]{3053, 3301, 0}),
        HUNTER( "Hunter",306,  new int[]{3560, 4010, 0}),
        ZMI_ALTAR( "ZMI Altar",306,  new int[]{2453, 3231, 0}),
        Varrock( "Varrock",306,  new int[]{3210, 3424, 0}),
        FALADOR( "Falador",306,  new int[]{2964, 3378, 0}),
        DRAYNOR( "Draynor",306,  new int[]{3080, 3250, 0}),
        EDGEVILLE("Edgeville",306,  new int[]{Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0}),
        CANIFIS("Canifis",306,  new int[]{3491, 3484, 0}),
        LUMBRIDGE( "Lumbridge",306,  new int[]{3222, 3218, 0}),
        ARDOUGNE( "Ardougne",306,  new int[]{2662, 3305, 0}),
        BRIMHAVEN( "Brimhaven",306,  new int[]{2795, 3178, 0}),
        YANILLE( "Yanille",306,  new int[]{2614, 3102, 0}),
        CAMELOT( "Camelot",306,  new int[]{2757, 3477, 0}),
        CATHERBY( "Catherby",306,  new int[]{2808, 3435, 0}),
        AL_KHARID( "Al-Kharid",306,  new int[]{3275, 3167, 0}),
        SHILO_VILLAGE( "Shilo village",306,  new int[]{2852, 2960, 0}),
        TAVERLY( "Taverly",306,  new int[]{2897, 3456, 0}),
        KARAMJA( "Karamja",306,  new int[]{2841, 3041, 0}),
        HOSIDIUS( "Hosidius",306,  new int[]{1755, 3598, 0}),
        KOUREND_CASTLE( "Kourend Castle",306,  new int[]{1644, 3673, 0}),
        ARCEUUS( "Arceuus",306,  new int[]{1669, 3748, 0}),
        LOVAKENGJ( "Lovakengj",306,  new int[]{1499, 3812, 0}),
        SHAYZIEN( "Shayzien",306,  new int[]{1536, 3563, 0}),
        PISCARILIUS( "Port Piscarilius",306,  new int[]{1803, 3786, 0}),
        //SLAYER_MASTERS("Slayer Master",7663, new int[]{3089, 3491, 0}),


        ;

        private final String name;

        private final int npcID;
        private final int[] teleportCords;

        SKILLING(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PK implements Teleport {

        FEROX_ENCLAVE("Ferox Enclave", 306, new int[]{3134,3629,0}),
        ARA_BLUE("<img=21>Araphel(single)", 8164,new int[]{3132, 3842, 0}),
        ARA_RED("<img=21>Araphel(multi)", 8172,new int[]{3280, 3835, 0}),
        CHOASDRUID("Chaos Druid's", 6607, new int[]{3236,3632,0}),
        REV_CAVE("Revenant Cave", 7940, new int[]{3127,3835,0}),
        KBD("King Black Dragon", 239, new int[]{3005, 3849, 0}),
        VET_ION("Vet'ion",6611,new int[]{3200, 3794, 0}),
        CALLISTO("Callisto",6503,new int[]{3325, 3845, 0}),
        SCORPIA("Scorpia",6615,new int[]{3233, 3945, 0}),
        VENENATIS("Venenatis",6610,new int[]{3345, 3754, 0}),
        CHAOS_ELEMENTAL("Chaos Elemental",2054,new int[]{3285, 3925, 0}),
        CHAOS_FANATIC("Chaos Fanatic",6619,new int[]{2978, 3833, 0}),
        CRAZY_ARCHAEOLOGIST("Crazy Archaeologist",6618,new int[]{2984, 3713, 0}),
        MAGE_BANK("Mage Bank", 1612, new int[]{2539, 4716, 0}),
        WEST_DRAGONS("West Dragons", 264, new int[]{2976, 3591, 0}),
        DARK_CASTLE("Dark Castle", 6606, new int[]{3020, 3632, 0}),
        ELDER_CHAOS_DRUID("Elder Chaos Druids", 6607, new int[]{3232, 3642, 0}),
        HILL_GIANTS("Hill Giants (Multi)", 2098, new int[]{3304, 3657, 0}),
        BLACK_CHIN("Black Chinchompa", 2912, new int[]{3137, 3767, 0}),
        LAVA_MAZE("Lava Maze", 6593, new int[]{3025, 3836, 0}),
        WILDY_GOD_WARS("Wildy God Wars Dungeon", 2237, new int[]{3021, 3738, 0}),
        WILDY_AGILITY("Wilderness Agility Course", 107, new int[]{3003, 3934, 0}),

        ;

        private String name;
        private final int npcID;
        private int[] teleportCords;

        PK(String name, int npcID, int[] teleportCords) {
            this.name = name;
            this.npcID = npcID;
            this.teleportCords = teleportCords;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    public interface Teleport {
        String getName();

    }


}
