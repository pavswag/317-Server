package io.xeros.content.tools;

import io.xeros.content.skills.Fishing;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.mining.Pickaxe;
import io.xeros.content.skills.woodcutting.Hatchet;
import io.xeros.model.entity.player.Player;

import java.util.*;

/**
 * Utility methods for tool augments and the Tinker Table.
 */
public class ToolAugments {

    /** Object id for the Tinker Table. */
    public static final int TINKER_TABLE_ID = 33408;
    /** Item id for tinker shards. Placeholder value. */
    public static final int TINKER_SHARD_ID = 5022;
    /** Default actions before an augment runs out. */
    public static final int DEFAULT_DURABILITY = 1000;
    /** Maximum augments per tool. */
    public static final int MAX_AUGMENTS_PER_TOOL = 2;

    /** Mapping of skill to crystal item id. */
    public static final Map<Skill, Integer> SKILLING_CRYSTALS = new HashMap<>();

    private static final Set<Integer> TOOL_IDS = new HashSet<>();
    private static final Map<Integer, Skill> TOOL_SKILLS = new HashMap<>();

    static {
        SKILLING_CRYSTALS.put(Skill.WOODCUTTING, 6000);
        SKILLING_CRYSTALS.put(Skill.MINING, 6001);
        SKILLING_CRYSTALS.put(Skill.FISHING, 6002);

        for (Pickaxe pickaxe : Pickaxe.values()) {
            TOOL_IDS.add(pickaxe.getItemId());
            TOOL_SKILLS.put(pickaxe.getItemId(), Skill.MINING);
        }
        for (Hatchet hatchet : Hatchet.values()) {
            TOOL_IDS.add(hatchet.getItemId());
            TOOL_SKILLS.put(hatchet.getItemId(), Skill.WOODCUTTING);
        }
        for (int[] data : Fishing.data) {
            if (data[2] > 0) {
                TOOL_IDS.add(data[2]);
                TOOL_SKILLS.put(data[2], Skill.FISHING);
            }
        }
    }

    /** Unlock augment from burning an item. */
    public static void unlockFromBurn(Player player, int itemId) {
        for (ToolAugment augment : ToolAugment.values()) {
            if (augment.getUnlockItemId() == itemId && player.getUnlockedToolAugments().add(augment)) {
                player.sendMessage("You can now apply the " + augment.getDescription() + " augment.");
            }
        }
    }

    public static boolean isTool(int itemId) {
        return TOOL_IDS.contains(itemId);
    }

    public static Skill getSkillForTool(int itemId) {
        return TOOL_SKILLS.get(itemId);
    }

    /** Apply or remove an augment when used on the Tinker Table. */
    public static void useOnTable(Player player, int itemId) {
        if (!isTool(itemId)) {
            player.sendMessage("Only skilling tools can be augmented here.");
            return;
        }

        if (applyOrUpgradeProficiency(player, itemId)) {
            return;
        }

        if (player.getUnlockedToolAugments().contains(ToolAugment.FOCUS)) {
            toggleAugment(player, itemId, ToolAugment.FOCUS);
            return;
        }

        player.sendMessage("You have no augments unlocked for this tool.");
    }

    private static boolean applyOrUpgradeProficiency(Player player, int itemId) {
        Skill skill = getSkillForTool(itemId);
        if (skill == null) {
            return false;
        }

        int crystalId = SKILLING_CRYSTALS.getOrDefault(skill, -1);
        List<AugmentInstance> list = player.getToolAugments().computeIfAbsent(itemId, k -> new ArrayList<>());
        AugmentInstance inst = list.stream()
                .filter(i -> i.getAugment() == ToolAugment.PROFICIENCY)
                .findFirst().orElse(null);

        AugmentTier next = inst == null ? AugmentTier.BASIC : inst.getTier().upgrade();
        if (inst != null && inst.getTier() == AugmentTier.MASTERWORK) {
            list.remove(inst);
            player.sendMessage("Removed skilling augment from your tool.");
            return true;
        }

        int owned = player.getItems().getItemCount(crystalId, false);
        player.sendMessage("Tinker Table: Tier " + next.name().toLowerCase() + " requires " + next.getCrystalCost() +
                " crystals. You have " + owned + ".");
        if (player.playerXP[skill.getId()] < next.getXpRequired()) {
            player.sendMessage("You need " + next.getXpRequired() + " " + skill.name().toLowerCase() +
                    " XP to unlock this tier.");
            return true;
        }
        if (owned < next.getCrystalCost()) {
            player.sendMessage("You do not have enough crystals.");
            return true;
        }
        player.getItems().deleteItem(crystalId, next.getCrystalCost());
        if (inst == null) {
            list.add(new AugmentInstance(ToolAugment.PROFICIENCY, next, DEFAULT_DURABILITY));
            player.sendMessage("Applied " + next.name().toLowerCase() + " augment.");
        } else {
            inst.setTier(next);
            inst.setDurability(DEFAULT_DURABILITY);
            player.sendMessage("Upgraded augment to " + next.name().toLowerCase() + ".");
        }
        player.getTinkerLogs().addFirst("Augment " + next.name());
        if (player.getTinkerLogs().size() > 10) {
            player.getTinkerLogs().removeLast();
        }
        checkSetBonuses(player);
        return true;
    }

    private static void toggleAugment(Player player, int itemId, ToolAugment augment) {
        List<AugmentInstance> list = player.getToolAugments().computeIfAbsent(itemId, k -> new ArrayList<>());
        for (Iterator<AugmentInstance> it = list.iterator(); it.hasNext();) {
            AugmentInstance i = it.next();
            if (i.getAugment() == augment) {
                it.remove();
                player.sendMessage("Removed " + augment.getDescription() + " from your tool.");
                checkSetBonuses(player);
                return;
            }
        }
        if (list.size() >= MAX_AUGMENTS_PER_TOOL) {
            player.sendMessage("This tool cannot hold any more augments.");
            return;
        }
        list.add(new AugmentInstance(augment, AugmentTier.BASIC, DEFAULT_DURABILITY));
        player.sendMessage("Applied " + augment.getDescription() + " to your tool.");
        checkSetBonuses(player);
    }

    /** Notify player of active augments on equip. */
    public static void onEquip(Player player, int itemId) {
        List<AugmentInstance> list = player.getToolAugments().get(itemId);
        if (list != null && !list.isEmpty()) {
            StringBuilder builder = new StringBuilder("Augments active: ");
            boolean first = true;
            for (AugmentInstance inst : list) {
                if (!inst.isActive()) continue;
                if (!first) builder.append(", ");
                builder.append(inst.getAugment().getDescription())
                        .append(" (")
                        .append(inst.getTier().name().toLowerCase())
                        .append(", ")
                        .append(inst.getDurability())
                        .append(")");
                first = false;
            }
            if (!first) {
                player.sendMessage(builder.toString());
            }
        }
        checkSetBonuses(player);
    }

    /** Apply XP boost if the tool has the proficiency augment or set bonuses. */
    public static int applyXpBoost(Player player, int itemId, int xp) {
        double multiplier = 1.0;
        List<AugmentInstance> list = player.getToolAugments().get(itemId);
        if (list != null) {
            for (AugmentInstance inst : list) {
                if (inst.isActive() && inst.getAugment() == ToolAugment.PROFICIENCY) {
                    multiplier += inst.getTier().getXpBoost();
                }
            }
        }
        if (player.isPrecisionSetActive()) {
            multiplier += 0.05; // precision set bonus
        }
        return (int) Math.round(xp * multiplier);
    }

    /** Chance to save a resource such as bait. */
    public static boolean rollSaveResource(Player player, int itemId) {
        List<AugmentInstance> list = player.getToolAugments().get(itemId);
        if (list == null) return false;
        for (AugmentInstance inst : list) {
            if (inst.isActive() && inst.getAugment() == ToolAugment.PROFICIENCY) {
                return Math.random() < inst.getTier().getSaveChance();
            }
        }
        return false;
    }

    /** Chance to gather an additional resource. */
    public static boolean rollDoubleGather(Player player, int itemId) {
        List<AugmentInstance> list = player.getToolAugments().get(itemId);
        if (list == null) return false;
        for (AugmentInstance inst : list) {
            if (inst.isActive() && inst.getAugment() == ToolAugment.PROFICIENCY) {
                return Math.random() < inst.getTier().getDoubleChance();
            }
        }
        return false;
    }

    /** Attempt to drop a crystal while skilling. */
    public static void tryGiveCrystal(Player player, Skill skill) {
        long now = System.currentTimeMillis();
        if (now - player.getCrystalHourStart() > 60_000L * 60) {
            player.setCrystalHourStart(now);
            player.setCrystalsFoundThisHour(0);
        }
        if (player.getCrystalsFoundThisHour() >= 3) {
            return;
        }
        double chance = 1.0 / 200.0;
        if (player.wearingFullSkillingOutfit()) {
            chance = 1.0 / 100.0;
        }
        if (Math.random() < chance) {
            int crystal = SKILLING_CRYSTALS.getOrDefault(skill, -1);
            if (crystal > 0) {
                player.getItems().addItem(crystal, 1);
                player.sendMessage("@blu@You've found a shimmering Skilling Crystal!");
                player.setCrystalsFoundThisHour(player.getCrystalsFoundThisHour() + 1);
            }
        }
    }

    /** Decrease durability of all augments on the tool after an action. */
    public static void decrementDurability(Player player, int itemId) {
        List<AugmentInstance> list = player.getToolAugments().get(itemId);
        if (list == null) return;
        for (AugmentInstance inst : list) {
            if (!inst.isActive()) continue;
            inst.degrade();
            if (!inst.isActive()) {
                player.sendMessage("Your " + inst.getAugment().getDescription() + " augment has depleted.");
            }
        }
    }

    /** Check for set bonuses like the Precision set. */
    public static void checkSetBonuses(Player player) {
        int focus = 0;
        for (int id : player.playerEquipment) {
            List<AugmentInstance> list = player.getToolAugments().get(id);
            if (list == null) continue;
            for (AugmentInstance inst : list) {
                if (inst.isActive() && inst.getAugment() == ToolAugment.FOCUS) {
                    focus++;
                }
            }
        }
        boolean active = focus >= 3;
        if (active != player.isPrecisionSetActive()) {
            player.setPrecisionSetActive(active);
            if (active) {
                player.sendMessage("@pur@Precision Set bonus active (+5% XP).");
            } else {
                player.sendMessage("Precision Set bonus no longer active.");
            }
        }
    }

}
