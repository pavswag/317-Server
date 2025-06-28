package io.xeros.model.entity.player;

import io.xeros.Server;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.items.ContainerUpdate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 15/02/2024
 */
public class CosmeticOverride {

    private static final int INTERFACE_ID = 42669;

    public static void openInterface(Player c) {
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
                || Boundary.isIn(c, Boundary.TELEGRAB_WILDYEDGE)
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
                || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
            return;
        }
        updateCosmeticInterface(c);
        c.getPA().showInterface(INTERFACE_ID);
        c.setOpenInterface(INTERFACE_ID);
    }

    private static void updateInventory(Player player) {
        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
//        player.getItems().sendWeapon(player.getEquipmentToShow(Player.playerWeapon));
        player.updateItems = true;
        player.getItems().calculateBonuses();
        MeleeData.setWeaponAnimations(player);
        player.setUpdateRequired(true);
        player.setAppearanceUpdateRequired(true);
        player.getItems().sendEquipmentContainer();
        player.getPA().requestUpdates();
    }

    public static void handleEquipCosmetic(Player player, ItemDef itemDef, int slot) {
        int targetSlot = ItemStats.forId(itemDef.getId()).getEquipment().getSlot();
        int item = itemDef.getId();

        if (targetSlot == -1) {
            player.sendMessage("This item cannot be worn.");
            return;
        }

        if (player.playerEquipmentCosmetic[targetSlot] > 0) {
            player.getItems().addItemUnderAnyCircumstance(player.playerEquipmentCosmetic[targetSlot], 1);
        }
        player.playerEquipmentCosmetic[targetSlot] = item;
        player.getItems().deleteItem(item, 1);
        updateInventory(player);
        openInterface(player);
    }

    public static boolean handleUnequipCosmetic(Player c, int removeId) {
        int[] equipmentCosmetic = c.playerEquipmentCosmetic;
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        if (interfaceIds.containsValue(removeId)) {

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
                    || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
                return true;
            }
            int slot = getKeyByValue(interfaceIds, removeId);
            c.getItems().addItemUnderAnyCircumstance(equipmentCosmetic[slot], 1);
            equipmentCosmetic[slot] = -1;
            updateInventory(c);
            openInterface(c);
            return true;
        }
        return false;
    }

    public static void setAllOverrides(Player player, boolean bool) {
        Arrays.fill(player.cosmeticOverrides, bool);
        updateInventory(player);
    }

    public static boolean handleToggleCosmetic(Player c, int toggleId) {
        boolean[] equipmentCosmetic = c.cosmeticOverrides;
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        if (interfaceIds.containsValue(toggleId)) {
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
                    || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
                return true;
            }
            int slot = getKeyByValue(interfaceIds, toggleId);
            equipmentCosmetic[slot] = !equipmentCosmetic[slot];
            updateInventory(c);
            return true;
        }

        return false;
    }
    private static Integer getKeyByValue(Map<Integer, Integer> map, Integer value) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1; // If the value is not found, return null
    }

    private static void updateCosmeticInterface(Player player) {
        int[] equipmentCosmetic = player.playerEquipmentCosmetic;

        // Map equipment slot index to interface ID
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        for (int i = 0; i < equipmentCosmetic.length; i++) {
            if (interfaceIds.containsKey(i)) {
                player.getPA().itemOnInterface(equipmentCosmetic[i], 1, interfaceIds.get(i), 0);
            }
        }
    }
}
