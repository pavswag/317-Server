package io.xeros.content.item.lootable.impl;

import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AOEChest implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(20849, 50),  //Dragon Thrownaxe
                new GameItem(22804, 50),  //Dragon knife
                new GameItem(4151, 1),    //Abyssal whip
                new GameItem(13025, 5),   //Rune Armour set
                new GameItem(12872, 5),   //Black D'hide set
                new GameItem(23113, 5),   //Mystic set (blue)
                new GameItem(5699, 5),    //Dragon dagger p++
                new GameItem(4151, 5),    //Granite maul
                new GameItem(6522, 150),  //Toktz-xil-ul
                new GameItem(6536, 2),    //Toktz-ket-xil
                new GameItem(6540, 2),    //Tzhaar-ket-om
                new GameItem(11234, 250), //Dragon dart p++
                new GameItem(3106, 15),   //Rock climbing boots
                new GameItem(12874, 3),   //Guthan's set
                new GameItem(12878, 3),   //Dharok's set
                new GameItem(12876, 3),   //Verac's set
                new GameItem(12882, 3),   //Ahrim's set
                new GameItem(12884, 3),   //Karil's set
                new GameItem(11841, 10),  //Dragon boots
                new GameItem(4088, 10),   //Dragon platelegs
                new GameItem(4586, 10),   //Dragon plateskirt
                new GameItem(19490, 75),  //Dragon javelin p++
                new GameItem(4588, 10),   //Dragon scimitar
                new GameItem(6738, 3),    //Berserker ring
                new GameItem(6732, 3),    //Seer's ring
                new GameItem(6734, 3),    //Archer ring
                new GameItem(6736, 3),    //Warrior ring
                new GameItem(12785, 1),   //Ring of wealth (i)
                new GameItem(11236, 5),   //Dark bow
                new GameItem(12954, 5),   //Dragon defender
                new GameItem(4676, 10),   //Ancient staff
                new GameItem(12829, 3),   //Spirit shield
                new GameItem(862, 15),    //Magic shortbow
                new GameItem(12696, 50),  //Combat potion (4)
                new GameItem(6686, 175),  //Saradomin brew
                new GameItem(3025, 175),  //Superrestore potion
                new GameItem(13307, 27500),  //Blood money
                new GameItem(6586, 3),    //Amulet of fury
                new GameItem(10843, 10),  //Helm of neitznot
                new GameItem(3750, 10),   //Archer's helm
                new GameItem(1713, 15),   //Amulet of glory
                new GameItem(11229, 175), //Dragon arrow p++
                new GameItem(22951, 1),   //Boots of brimstone
                new GameItem(990, 20),    //Crystal key
                new GameItem(4278, 5000), //Ecto-Token
                new GameItem(1409, 1),    //Iban's staff not noteable/stackable
                new GameItem(10551, 1),   //Fighter torso not noteable/stackable
                new GameItem(10548, 1)    //Fighter hat not noteable/stackable
        ));

        items.put(LootRarity.RARE, Arrays.asList(   // Decent Reward's only
                new GameItem(21012, 1),  //Dragon Hunter C'Bow
                new GameItem(21000, 1),  //twitsted buckler
                new GameItem(21015, 1),  //Dihn's bulwark
                new GameItem(21006, 1),  //Kodai wand
                new GameItem(21003, 1),  //Elder Maul
                new GameItem(20784, 1),  //Dragon Claws
                new GameItem(21018, 1),  //Ancestral hat
                new GameItem(21021, 1),  //Ancestral robe top
                new GameItem(21024, 1),  //Ancestral robe bottom
                new GameItem(21079, 1),  //Arcane prayer scroll
                new GameItem(21034, 1),  //Dexterous prayer scroll
                new GameItem(22326, 1),  //Justicar faceguard
                new GameItem(22327, 1),  //Justicar Chestguard
                new GameItem(22328, 1),  //Justicar Legguards
                new GameItem(22477, 1),  //Avernic defender hilt
                new GameItem(22326, 1),  //Justicar faceguard
                new GameItem(22327, 1),  //Justicar Chestguard
                new GameItem(22328, 1),  //Justicar Legguards
                new GameItem(22477, 1),  //Avernic defender hilt
                new GameItem(21295, 1),  //Infernal cape
                new GameItem(24422, 1),  //Nigtmare staff
                new GameItem(24419, 1),  //Inquisitor's great helm
                new GameItem(24421, 1),  //Inquisitor's plateskirt
                new GameItem(24420, 1),  //Inquisitor's hauberk
                new GameItem(24417, 1),  //Inquisitor's mace
                new GameItem(24419, 1),  //Inquisitor's great helm
                new GameItem(24421, 1),  //Inquisitor's plateskirt
                new GameItem(24420, 1),  //Inquisitor's hauberk
                new GameItem(24417, 1),  //Inquisitor's mace
                new GameItem(24517, 1),  //Eldritch orb
                new GameItem(24511, 1),  //Harmonised orb
                new GameItem(24514, 1),  //Volatile orb
                new GameItem(26233, 1),  //Ancient godsword
                new GameItem(26374, 1),  //Zaryte Crossbow
                new GameItem(26386, 1),  //Torva platelegs
                new GameItem(26384, 1),  //Torva platebody
                new GameItem(26382, 1),  //Torva full helm
                new GameItem(21012, 1),  //Armadyl helmet (or)
                new GameItem(21012, 1),  //Armadyl chestplate (or)
                new GameItem(21012, 1),  //Armadyl plateskirt (or)
                new GameItem(21012, 1),  //Bandos chestplate (or)
                new GameItem(21012, 1),  //Bandos tassets (or)
                new GameItem(21012, 1),  //Bandos boots (or)
                new GameItem(21012, 1),  //Ancient ceremonial top
                new GameItem(21012, 1),  //Ancient ceremonial legs
                new GameItem(21012, 1),  //Ancient ceremonial boots
                new GameItem(21012, 1),  //Ancient ceremonial gloves
                new GameItem(21012, 1),  //Ancient ceremonial mask
                new GameItem(26386, 1),  //Torva platelegs
                new GameItem(26384, 1),  //Torva platebody
                new GameItem(26382, 1),  //Torva full helm
                new GameItem(21012, 1),  //Armadyl helmet (or)
                new GameItem(21012, 1),  //Armadyl chestplate (or)
                new GameItem(21012, 1),  //Armadyl plateskirt (or)
                new GameItem(21012, 1),  //Bandos chestplate (or)
                new GameItem(21012, 1),  //Bandos tassets (or)
                new GameItem(21012, 1),  //Bandos boots (or)
                new GameItem(21012, 1),  //Ancient ceremonial top
                new GameItem(21012, 1),  //Ancient ceremonial legs
                new GameItem(21012, 1),  //Ancient ceremonial boots
                new GameItem(21012, 1),  //Ancient ceremonial gloves
                new GameItem(21012, 1),  //Ancient ceremonial mask
                new GameItem(21012, 1),  //Zaryte gloves
                new GameItem(13576, 1),  //Dragon warhammer
                new GameItem(19493, 1),  //Zenyte
                new GameItem(19479, 1),  //Light ballista
                new GameItem(19482, 1),  //Heavy ballista
                new GameItem(12817, 1),  //Elysian spirit shield
                new GameItem(12821, 1),  //Spectral spirit shield
                new GameItem(12825, 1),  //Arcane spirit shield
                new GameItem(12926, 1),  //Blowpipe
                new GameItem(12902, 1),  //Toxic staff
                new GameItem(12929, 1),  //Serpentine helm
                new GameItem(13200, 1),  //Tanzanite mutagen
                new GameItem(13201, 1),  //Magma mutagen
                new GameItem(13239, 1),  //Primordial boots
                new GameItem(13237, 1),  //Pegasian boots
                new GameItem(13235, 1),  //Eternal boots
                new GameItem(13233, 1),  //Smouldering stone
                new GameItem(11283, 1),  //Dragonfire shield
                new GameItem(22002, 1),  //Dragonfire ward
                new GameItem(21633, 1),  //Ancient wyvern shield

                new GameItem(22611, 1),  //Vesta spear
                new GameItem(22614, 1),  //Vesta longsword
                new GameItem(22617, 1),  //Vesta chainbody
                new GameItem(22617, 1),  //Vesta chainbody
                new GameItem(22620, 1),  //Vesta chainskirt
                new GameItem(22623, 1),  //Statius warhammer
                new GameItem(22626, 1),  //Statius full helm
                new GameItem(22629, 1),  //Statius platebody
                new GameItem(22632, 1),  //Statius platelegs
                new GameItem(22648, 1),  //Zuriel's staff
                new GameItem(22651, 1),  //Zuriel's hood
                new GameItem(22654, 1),  //Zuriel's robe top
                new GameItem(22657, 1),  //Zuriel's robe bottom
                new GameItem(22639, 1),  //Morrigan's coif
                new GameItem(22642, 1),  //Morrigan's leatherbody
                new GameItem(22645, 1),  //Morrigan's leather chaps

                new GameItem(20784, 1),  //Dragon Claws
                new GameItem(26233, 1),  //Ancient Godsword
                new GameItem(11804, 1),  //Bandos godsword
                new GameItem(11806, 1),  //Saradomin godsword
                new GameItem(11808, 1),  //Zamorak godsword
                new GameItem(11802, 1),  //Armadyl godsword
                new GameItem(11838, 1),  //Saradomin Sword

                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(20997, 1),  //Twisted Bow
                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(20997, 1),  //Twisted Bow
                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(20997, 1),  //Twisted Bow
                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(20997, 1),  //Twisted Bow
                new GameItem(20997, 1)   //Twisted Bow
        ));
    }
    private static GameItem randomChestRewards() {
        int rng = Misc.random(1000);
        List<GameItem> itemList = (rng > 950 ? getItems().get(LootRarity.RARE) : getItems().get(LootRarity.COMMON));
        return Misc.getRandomItem(itemList);
    }
    private static final int KEY = 13302;
    private static final int ANIMATION = 881;
    //Object ID = 43486;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 18; i++) {
            int pointz = 0;
            for (int ii = 0; ii < 32; ii++) {
                int rng = Misc.trueRand(1);
                int points = Misc.random(500, 2000);
                if (rng == 1) {
                    points /= 2;
                }
                pointz += points;
            }
            System.out.println(pointz);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();

            for (GameItem gameItem : getItems().get(LootRarity.RARE)) {
                if (gameItem.getId() == reward.getId()) {
                    PlayerHandler.executeGlobalMessage("@bla@[<col=7f0000>WILDY KEY@bla@] <col=990000>" + c.getDisplayName() + "@bla@ has just received a <col=990000>" + reward.getDef().getName() + ".");
                    break;
                }
            }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());

        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
