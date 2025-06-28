package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.content.commands.owner.Npc;
import io.xeros.content.fireofexchange.FireOfExchange;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.util.Misc;

public class ExaminePacketHandler implements PacketType {

    public static final int EXAMINE_ITEM = 134;
    public static final int EXAMINE_NPC = 137;

    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        switch (packetType) {
            case EXAMINE_ITEM:
                int item = c.getInStream().readInteger();
                int count = c.getInStream().readInteger();
                ItemDef itemDefinition = ItemDef.forId(item);


                if (c.debugMessage) {
                    c.sendMessage("Examine item: " + item);
                    if (c.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                        c.getItems().addItemUnderAnyCircumstance(item, 1);
                    }
                }

                StringBuilder examine = new StringBuilder();
                if (itemDefinition != null) {
                    if (itemDefinition.getDescription() != null && !itemDefinition.getDescription().isEmpty()) {
                        examine.append(itemDefinition.getDescription());
                        examine.append(" ");
                    } else {
                        examine.append(itemDefinition.getName());
                        examine.append(" ");
                    }

                    int value = ShopAssistant.getItemShopValue(item);
                    int stackValue = value * count;

                    if (value > 0) {
                        examine.append("<col=800000>(Value: " + Misc.formatCoins(value));
                        if (count > 1) {
                            examine.append(", Stack value: " + Misc.formatCoins(stackValue)  + ")");
                        } else {
                            examine.append(")");
                        }
                        examine.append(" ");
                    }
                    int foeValue = FireOfExchangeBurnPrice.getBurnPrice(c, item, false);
                    if (foeValue > 0) {
                        examine.append("<col=800000>(Upgrade Points: " + Misc.formatCoins(foeValue));
                        examine.append(")");
                        examine.append(" ");
                    }
                    if (!itemDefinition.isTradable()) {
                        examine.append("<col=800000>(untradeable)");
                    }
                }

                if (examine.length() > 0) {
                    c.sendMessage(examine.toString());
                }
                break;
            case EXAMINE_NPC:
                int npcIndex = c.getInStream().readUnsignedWord();

                if (npcIndex > 30000) {
                    return;
                }

                NPC npc = NPCHandler.npcs[npcIndex];
                if (npc != null) {
                    if (c.debugMessage) {
                        c.sendMessage("Examined " + npc.getNpcId());
                    }
                    String header = "@dre@";

                    if (npc.getNpcId() == 10529) {
                        FireOfExchangeBurnPrice.openExchangeRateShop(c);
                        break;
                    }
                    NpcCombatDefinition definition = npc.getCombatDefinition();
                    if (definition != null && npc.getDefinition().getCombatLevel() > 0) {

                        c.sendMessage(header + "[" + npc.getDefinition().getName() + "]");
                        c.sendMessage(header +
                                "Levels ["
                                + "Melee: " + definition.getLevel(NpcCombatSkill.ATTACK) + ", "
                                + "Ranged: " + definition.getLevel(NpcCombatSkill.RANGE) + ", "
                                + "Magic: " + definition.getLevel(NpcCombatSkill.MAGIC) + ", "
                                + "Defence: " + definition.getLevel(NpcCombatSkill.DEFENCE) + ", "
                                + "Strength: " + definition.getLevel(NpcCombatSkill.STRENGTH)
                                + "]"
                        );
                        c.sendMessage(header + "Bonuses:");
                        c.sendMessage(header +
                                " - Attack ["
                                + "Melee: " + definition.getAttackBonus(NpcBonus.ATTACK_BONUS) + ", "
                                + "Ranged: " + definition.getAttackBonus(NpcBonus.ATTACK_RANGE_BONUS) + ", "
                                + "Magic: " + definition.getAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS) + ", "
                                + "Magic Damage: " + definition.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS) + ", "
                                + "Range Strength: " + definition.getAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS)
                                + "]"
                        );
                        c.sendMessage(header +
                                " - Defence ["
                                + "Stab: " + definition.getDefenceBonus(NpcBonus.STAB_BONUS) + ", "
                                + "Slash: " + definition.getDefenceBonus(NpcBonus.SLASH_BONUS) + ", "
                                + "Crush: " + definition.getDefenceBonus(NpcBonus.CRUSH_BONUS) + ", "
                                + "Ranged: " + definition.getDefenceBonus(NpcBonus.RANGE_BONUS) + ", "
                                + "Magic: " + definition.getDefenceBonus(NpcBonus.MAGIC_BONUS)
                                + "]"
                        );

                    } else
                        c.sendMessage(header + "It's " + npc.getDefinition().getName() + ".");

                    if (c.getRights().isOrInherits(Right.HELPER) && c.debugMessage) {
                        c.sendMessage(header + "Position: " + npc.getPosition() + ", Size: " + npc.getSize() + ", ID: " + npc.getNpcId() + ", idx: " + npc.getIndex());
                    }
                    if (npc.getDefinition().getCombatLevel() > 0) {
//                        c.getPA().sendDropTableData("Click HERE to view drops for "+npc.getName(), npc.getIndex());
                        c.sendMessage("Opening drops for: "+npc.getName()+"...");
                        Server.getDropManager().openForPacket(c, npc.getNpcId());
                    }
                }
                break;
        }
    }
}
