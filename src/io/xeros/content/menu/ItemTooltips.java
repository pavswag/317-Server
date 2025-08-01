package io.xeros.content.menu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.xeros.Configuration;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.skills.firemake.LogData;
import io.xeros.content.skills.herblore.Crushable;
import io.xeros.content.skills.herblore.PotionData;
import io.xeros.content.skills.mining.Pickaxe;
import io.xeros.content.skills.woodcutting.Hatchet;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.drops.TablePolicy;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.ItemConstants;
import io.xeros.util.Misc;

import io.xeros.model.Items;
import io.xeros.util.Reflection;

/**
 * Central registry of item tooltip text.
 */
public class ItemTooltips {

    /**
     * Map of item id to tooltip entry.
     */
    public static final Map<Integer, HoverMenu> MENUS = new HashMap<>();

    /**
     * Initialise the tooltip list and optionally export it to a json file.
     */
    public static void init(boolean export) {
        // Fire of Exchange burn values from the server definitions
        for (int id = 0; id < 45_000; id++) {
            int burn = FireOfExchangeBurnPrice.getBurnPrice(null, id, false);
            if (burn > 0) {
                addTooltip(id, "Dissolves in the Fire of Exchange for @lre@" +
                        Misc.insertCommas(burn) + "@whi@ FOE.");
            }
        }

// Upgradeable items
        for (UpgradeMaterials upgrade : UpgradeMaterials.values()) {
            addTooltip(upgrade.getRequired().getId(),
                    "Upgrades into " + ItemDef.forId(upgrade.getReward().getId()).getName() +
                            " for @lre@" + Misc.insertCommas((int) upgrade.getCost()) +
                            "@whi@ coins (" + upgrade.getSuccessRate() + "% success).");
            addTooltip(upgrade.getReward().getId(),
                    "Created by upgrading " + ItemDef.forId(upgrade.getRequired().getId()).getName() + ".");
        }

// Drop table items if drop configs are available
        loadDropRates(Paths.get(Configuration.DATA_FOLDER, "cfg", "drops"));
        // Skilling tools and resources
        loadSkillTools();
        loadBones();
        loadHerbloreIngredients();
        loadCrushables();
        loadFarmingSeeds();
        loadPets();
// Mystery boxes and similar lootable items
        loadMysteryBoxes();
        // Item option effects
        MENUS.put(Items.FISHING_PASS, new HoverMenu("Activates a membership pass when used."));
        MENUS.put(11740, new HoverMenu("Teleports you to the Island activity when used."));
        MENUS.put(Items.DWARVEN_ROCK_CAKE, new HoverMenu("Deals damage to lower your hitpoints (\"URGHHHHH!\")."));
        MENUS.put(6644, new HoverMenu("Opens a crystal container and grants several reward items."));

        // Herblore secondary ingredients
        MENUS.put(145, new HoverMenu("Used to create Super Combat 3 potions."));  // Super attack(3)
        MENUS.put(157, new HoverMenu("Used to create Super Combat 3 potions."));  // Super strength(3)
        MENUS.put(163, new HoverMenu("Used to create Super Combat 3 potions."));  // Super defence(3)
        MENUS.put(221, new HoverMenu("Used to create Attack, Super Attack potions."));  // Eye of newt
        MENUS.put(223, new HoverMenu("Used to create Guthix Balance, Restore, Super Restore, Weapon Poison Plus potions."));  // Red spiders' eggs
        MENUS.put(225, new HoverMenu("Used to create Strength, Super Strength potions."));  // Limpwurt root
        MENUS.put(231, new HoverMenu("Used to create Fishing, Prayer potions."));  // Snape grass
        MENUS.put(235, new HoverMenu("Used to create Antipoison, Sanfew 3, Sanfew 4, Super Antipoison potions."));  // Unicorn horn dust
        MENUS.put(239, new HoverMenu("Used to create Defence, Super Defence potions."));  // White berries
        MENUS.put(241, new HoverMenu("Used to create Antifire, Weapon Poison potions."));  // Dragon scale dust
        MENUS.put(245, new HoverMenu("Used to create Ranging potions."));  // Wine of zamorak
        MENUS.put(247, new HoverMenu("Used to create Zamorak Brew potions."));  // Jangerberries
        MENUS.put(269, new HoverMenu("Used to create Anti Venom Plus potions."));  // Clean torstol
        MENUS.put(1550, new HoverMenu("Used to create Guthix Balance potions."));  // Garlic
        MENUS.put(1975, new HoverMenu("Used to create Energy potions."));  // Chocolate dust
        MENUS.put(2152, new HoverMenu("Used to create Agility potions."));  // Toad's legs
        MENUS.put(2398, new HoverMenu("Used to create Weapon Poison Plus Plus potions."));  // Nightshade
        MENUS.put(2436, new HoverMenu("Used to create Super Combat 4 potions."));  // Super attack(4)
        MENUS.put(2440, new HoverMenu("Used to create Super Combat 4 potions."));  // Super strength(4)
        MENUS.put(2442, new HoverMenu("Used to create Super Combat 4 potions."));  // Super defence(4)
        MENUS.put(2970, new HoverMenu("Used to create Super Energy potions."));  // Mort myre fungi
        MENUS.put(3138, new HoverMenu("Used to create Magic potions."));  // Potato cactus
        MENUS.put(6016, new HoverMenu("Used to create Weapon Poison Plus potions."));  // Cactus spine
        MENUS.put(6018, new HoverMenu("Used to create Weapon Poison Plus Plus potions."));  // Poisonivy berries
        MENUS.put(6049, new HoverMenu("Used to create Antidote Plus potions."));  // Yew roots
        MENUS.put(6051, new HoverMenu("Used to create Antidote Plus Plus potions."));  // Magic roots
        MENUS.put(6693, new HoverMenu("Used to create Saradomin Brew potions."));  // Crushed nest
        MENUS.put(7650, new HoverMenu("Used to create Guthix Balance potions."));  // Silver dust
        MENUS.put(9736, new HoverMenu("Used to create Combat potions."));  // Goat horn dust
        MENUS.put(11994, new HoverMenu("Used to create Extended Antifire, Extended Super Antifire potions."));  // Lava scale shard
        MENUS.put(12640, new HoverMenu("Used to create Stamina potions."));  // Amylase crystal
        MENUS.put(12934, new HoverMenu("Used to create Anti Venom 1, Anti Venom 2, Anti Venom 3, Anti Venom 4 potions."));  // Zulrah's scales
        MENUS.put(20698, new HoverMenu("Used to create Rejuv Pot potions."));  // Bruma herb
        MENUS.put(21975, new HoverMenu("Used to create Super Antifire potions."));  // Crushed Superior Dragon dust
        MENUS.put(23867, new HoverMenu("Used to create Divine Magic Potion 3, Divine Magic Potion 4, Divine Ranging Potion 3, Divine Ranging Potion 4, Divine Super Combat 3, Divine Super Combat 4 potions."));  // Crystal dust
        MENUS.put(11864, new HoverMenu("Deal 10% more damage against current slayer task."));
        MENUS.put(11865, new HoverMenu("Deal 15% more damage against current slayer task."));
        MENUS.put(19639, new HoverMenu("Deal 20% more damage against current slayer task."));
        MENUS.put(19643, new HoverMenu("Deal 20% more damage against current slayer task."));
        MENUS.put(19647, new HoverMenu("Deal 20% more damage against current slayer task."));
        MENUS.put(21264, new HoverMenu("Deal 20% more damage against current slayer task."));
        MENUS.put(21888, new HoverMenu("Deal 20% more damage against current slayer task."));
        MENUS.put(7409, new HoverMenu("Provides 25% more herblore experience while worn."));
        MENUS.put(7451, new HoverMenu("Provides 25% more cooking experience while worn."));
        MENUS.put(11850, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(11852, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(11854, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(11856, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(11858, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(11860, new HoverMenu("Provides 25% more agility experience while wearing full set."));
        MENUS.put(10941, new HoverMenu("Provides 25% more woodcutting experience while wearing full set."));
        MENUS.put(10939, new HoverMenu("Provides 25% more woodcutting experience while wearing full set."));
        MENUS.put(10940, new HoverMenu("Provides 25% more woodcutting experience while wearing full set."));
        MENUS.put(10933, new HoverMenu("Provides 25% more woodcutting experience while wearing full set."));
        MENUS.put(12013, new HoverMenu("Provides 25% more mining experience while wearing full set."));
        MENUS.put(12014, new HoverMenu("Provides 25% more mining experience while wearing full set."));
        MENUS.put(12015, new HoverMenu("Provides 25% more mining experience while wearing full set."));
        MENUS.put(12016, new HoverMenu("Provides 25% more mining experience while wearing full set."));
        MENUS.put(13258, new HoverMenu("Provides 25% more fishing experience while wearing full set."));
        MENUS.put(13259, new HoverMenu("Provides 25% more fishing experience while wearing full set."));
        MENUS.put(13260, new HoverMenu("Provides 25% more fishing experience while wearing full set."));
        MENUS.put(13261, new HoverMenu("Provides 25% more fishing experience while wearing full set."));
        MENUS.put(13646, new HoverMenu("Provides 25% more farming experience while wearing full set."));
        MENUS.put(13642, new HoverMenu("Provides 25% more farming experience while wearing full set."));
        MENUS.put(13643, new HoverMenu("Provides 25% more farming experience while wearing full set."));
        MENUS.put(13640, new HoverMenu("Provides 25% more farming experience while wearing full set."));
        MENUS.put(13644, new HoverMenu("Provides 25% more farming experience while wearing full set."));
        MENUS.put(20708, new HoverMenu("Provides 25% more firemaking experience while wearing full set."));
        MENUS.put(20710, new HoverMenu("Provides 25% more firemaking experience while wearing full set."));
        MENUS.put(20712, new HoverMenu("Provides 25% more firemaking experience while wearing full set."));
        MENUS.put(20704, new HoverMenu("Provides 25% more firemaking experience while wearing full set."));
        MENUS.put(20706, new HoverMenu("Provides 25% more firemaking experience while wearing full set."));
        MENUS.put(5554, new HoverMenu("Provides 25% more thieving experience while wearing full set."));
        MENUS.put(5553, new HoverMenu("Provides 25% more thieving experience while wearing full set."));
        MENUS.put(5555, new HoverMenu("Provides 25% more thieving experience while wearing full set."));
        MENUS.put(5556, new HoverMenu("Provides 25% more thieving experience while wearing full set."));
        MENUS.put(5557, new HoverMenu("Provides 25% more thieving experience while wearing full set."));
        MENUS.put(6799, new HoverMenu("Does not stack and vanishes when you log out."));
        MENUS.put(6800, new HoverMenu("Does not stack and vanishes when you log out."));
        MENUS.put(6801, new HoverMenu("Does not stack and vanishes when you log out."));
        MENUS.put(6803, new HoverMenu("Does not stack and vanishes when you log out."));
        MENUS.put(23206, new HoverMenu("Hits guaranteed double."));
        if (export) {
            exportToJson("item_tooltips.json");
        }
    }

    private ItemTooltips() {
        // Utility class
    }
    /**
     * Populate tooltips for basic skilling tools and resources.
     */
    private static void loadSkillTools() {
        for (Hatchet hatchet : Hatchet.values()) {
            MENUS.putIfAbsent(hatchet.getItemId(),
                    new HoverMenu("Used for woodcutting, requires level " + hatchet.getLevelRequired() + "."));
        }
        for (Pickaxe pickaxe : Pickaxe.values()) {
            MENUS.putIfAbsent(pickaxe.getItemId(),
                    new HoverMenu("Used for mining, requires level " + pickaxe.getLevel() + "."));
        }
        for (LogData log : LogData.values()) {
            MENUS.putIfAbsent(log.getlogId(),
                    new HoverMenu("Burnable log requiring level " + log.getlevelRequirement() + " Firemaking."));
        }
    }
    private static HoverMenu merge(int id, String text, List<Integer> items) {
        HoverMenu existing = MENUS.get(id);
        if (existing == null) {
            return new HoverMenu(text, items);
        }
        StringBuilder sb = new StringBuilder(existing.getText());
        if (!existing.getText().endsWith(".")) {
            sb.append('.');
        }
        sb.append(' ').append(text);
        List<Integer> merged = existing.getItems();
        if (items != null && !items.isEmpty()) {
            merged = merged == null ? new ArrayList<>() : new ArrayList<>(merged);
            for (int i : items) {
                if (!merged.contains(i)) {
                    merged.add(i);
                }
            }
        }
        return new HoverMenu(sb.toString(), merged);
    }

    private static void addTooltip(int id, String text) {
        MENUS.put(id, merge(id, text, null));
    }

    private static void addTooltip(int id, String text, List<Integer> items) {
        MENUS.put(id, merge(id, text, items));
    }
    /**
     * Tooltips for farming seeds to indicate what they grow into.
     */
    private static void loadFarmingSeeds() {
        for (io.xeros.content.skills.farming.Plants plant : io.xeros.content.skills.farming.Plants.values()) {
            String harvestName = ItemDef.forId(plant.harvest).getName();
            MENUS.putIfAbsent(plant.seed,
                    new HoverMenu("Plant to grow " + harvestName + ", requires level " + plant.level + " Farming."));
        }
    }

    /**
     * Tooltips for boss and skilling pets with drop rate info.
     */
    private static void loadPets() {
        for (PetHandler.Pets pet : PetHandler.Pets.values()) {
            String npcName = NpcDef.forId(pet.npcId).getName();
            String text = "Pet dropped by " + npcName;
            if (pet.getDroprate() > 0) {
                text += " (1/" + pet.getDroprate() + ")";
            }
            MENUS.putIfAbsent(pet.getItemId(), new HoverMenu(text + "."));
        }
    }
    /**
     * Populate tooltips for herblore secondary ingredients based on PotionData definitions.
     */
    private static void loadHerbloreIngredients() {
        Map<Integer, Set<String>> uses = new HashMap<>();
        for (PotionData.FinishedPotions potion : PotionData.FinishedPotions.values()) {
            String name = ItemDef.forId(potion.getResult().getId()).getName();
            for (GameItem ingredient : potion.getIngredients()) {
                uses.computeIfAbsent(ingredient.getId(), k -> new HashSet<>()).add(name);
            }
        }
        for (Map.Entry<Integer, Set<String>> e : uses.entrySet()) {
            String list = String.join(", ", e.getValue());
            MENUS.putIfAbsent(e.getKey(), new HoverMenu("Used to create " + list + " potions."));
        }
    }

    /**
     * Tooltips for items that can be crushed with a pestle and mortar.
     */
    private static void loadCrushables() {
        for (Crushable crush : Crushable.values()) {
            String resultName = ItemDef.forId(crush.getResult().getId()).getName();
            MENUS.putIfAbsent(crush.getOriginal().getId(),
                    new HoverMenu("Crush with a pestle and mortar to make " + resultName + "."));
        }
    }
    /**
     * Populate tooltip entries for all mystery box style lootables so the client
     * can display their potential rewards.
     */
    private static void loadMysteryBoxes() {
        Set<Class<? extends Lootable>> classes =
                Reflection.getSubClasses("io.xeros.content.item.lootable.impl", Lootable.class);
        for (Class<? extends Lootable> cls : classes) {
            if (Modifier.isAbstract(cls.getModifiers()) || Modifier.isInterface(cls.getModifiers())) {
                continue;
            }
            try {
                Method idMethod = null;
                try {
                    idMethod = cls.getMethod("getItemId");
                } catch (NoSuchMethodException ignored) {
                    try {
                        idMethod = cls.getMethod("getLootableItem");
                    } catch (NoSuchMethodException ex) {
                        // not a box type we care about
                        continue;
                    }
                }

                Constructor<?> ctor = cls.getConstructors()[0];
                Object[] params = Arrays.stream(ctor.getParameterTypes()).map(t -> (Object) null).toArray();
                Object instance = ctor.newInstance(params);

                int itemId = (int) idMethod.invoke(instance);

                Method getLoot = cls.getMethod("getLoot");
                @SuppressWarnings("unchecked")
                Map<?, List<GameItem>> loot = (Map<?, List<GameItem>>) getLoot.invoke(instance);

                List<Integer> rewards = loot.values().stream()
                        .flatMap(List::stream)
                        .map(GameItem::getId)
                        .distinct()
                        .collect(Collectors.toList());

                MENUS.put(itemId, new HoverMenu("Contains various items:", rewards));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Populate tooltips for buryable bones.
     */
    private static void loadBones() {
        for (io.xeros.content.skills.prayer.Bone bone : io.xeros.content.skills.prayer.Bone.values()) {
            MENUS.putIfAbsent(bone.getItemId(),
                    new HoverMenu("Bury for " + bone.getExperience() + " Prayer XP."));
        }
    }
    /**
     * Read NPC drop tables if present and populate tooltips describing which NPC drops an item.
     */
    private static void loadDropRates(Path directory) {
        if (!Files.isDirectory(directory)) {
            return;
        }

        ItemConstants itemConstants = new ItemConstants().load();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try (Stream<Path> files = Files.walk(directory)) {
            files.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                    .forEach(path -> {
                        try {
                            JsonNode root = mapper.readTree(path.toFile());

                            List<Integer> npcIds = new ArrayList<>();
                            JsonNode npcNode = root.get("npc_id");
                            if (npcNode == null) {
                                return;
                            }
                            if (npcNode.isArray()) {
                                npcNode.forEach(n -> npcIds.add(n.asInt()));
                            } else {
                                npcIds.add(npcNode.asInt());
                            }

                            Set<String> npcNameSet = npcIds.stream()
                                    .map(id -> NpcDef.forId(id).getName())
                                    .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

                            String npcNames = String.join(", ", npcNameSet);

                            for (TablePolicy policy : TablePolicy.POLICIES) {
                                String key = policy.name().toLowerCase();
                                if (!root.has(key)) continue;

                                JsonNode table = root.get(key);
                                JsonNode items = table.get("items");
                                if (items == null) continue;

                                for (JsonNode item : items) {
                                    int itemId;
                                    if (item.has("item")) {
                                        itemId = item.get("item").asInt();
                                    } else if (item.has("name")) {
                                        itemId = itemConstants.get(item.get("name").asText());
                                    } else {
                                        continue;
                                    }
                                    MENUS.putIfAbsent(itemId, new HoverMenu("Dropped by " + npcNames));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the current tooltip mapping to a JSON file so the client can load it.
     */
    public static void exportToJson(String file) {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<Integer, String> data = MENUS.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getText()));
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
