package io.xeros.content.fireofexchange;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.xeros.content.bosspoints.JarsToPoints;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.model.shops.ShopItem;
import io.xeros.model.world.ShopHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FireOfExchangeBurnPrice {

    public static int SHOP_ID;

    public static void init() {
        createBurnPriceShop();
    }

    public static int[] crystals = {33125,33126,33127,33128,33129,33130,33131,33132,33133,33134,33135,33136,33137,33138,33139,33140};

    public static void createBurnPriceShop() {
        Map<Integer, Integer> burnPrices = new HashMap<>();
        for (int i = 0; i < 60_000; i++) {
            int price = getBurnPrice(null, i, false);
            if (price > 0)
                burnPrices.put(i, price);
        }

        for (UpgradeMaterials value : UpgradeMaterials.values()) {
            int price = (int) (value.getCost() / 5);
            if (price > 0 && !burnPrices.containsKey(value.getReward().getId())) {
                burnPrices.put(value.getReward().getId(), price);
            }
        }

        for (int crystal : crystals) {
            burnPrices.remove(crystal);
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(burnPrices.entrySet());

        list.sort((a, b) -> {
            int comparison = b.getValue().compareTo(a.getValue());
            if (comparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }

            return comparison;
        });

        List<ShopItem> shopItems = list.stream().map(it -> new ShopItem(it.getKey() + 1 /* shops need this +1 lol */,
                it.getValue(), it.getValue())).collect(Collectors.toList());
        SHOP_ID = ShopHandler.addShopAnywhere("Nomad's Dissolving Rates", shopItems);

        /*try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/burn_prices.json"));
            if (!new File("./temp/burn_prices.json").exists()) {
                Preconditions.checkState(new File("./temp/burn_prices.json").mkdirs());
            }

            for (ShopItem shopItem : shopItems) {
                bufferedWriter.write("[TR][TD]" + ItemDef.forId(shopItem.getId()-1).getName() + "[/TD][TD]" + Misc.formatCoins(shopItem.getPrice()) + "[/TD][/TR]");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /* UNCOMMENT IF YOU WANT TO DUMP NOMAD TO ITEM.JSON */
//        Item.DumpItemsIntoJson();
    }

    @Getter @Setter
    public static class Item {
        private int id;
        private String name;
        private int price;
        private int wikiPrice;

        // Constructors, getters, and setters
        public static void DumpItemsIntoJson() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Item> items = new ArrayList<>();
            try (FileWriter writer = new FileWriter("./temp/items.json")) {
                for (int i = 0; i < 45000; i++) {
                    ItemDef itemDefinition = ItemDef.forId(i);
                    if (itemDefinition != null && !itemDefinition.getName().contains("unknown")) {
                        int itemId = itemDefinition.getId();
                        int value = ShopAssistant.getItemShopValue(itemId);
                        int foeValue = FireOfExchangeBurnPrice.getBurnPrice(null, itemId, false);
                        if (foeValue < 0) {
                            foeValue = 0;
                        }

                        Item item = new Item();
                        item.setId(itemId);
                        item.setName(itemDefinition.getName());
                        item.setPrice(value);
                        item.setWikiPrice(foeValue);

                        items.add(item);
                    }
                }
                // Write the entire list of items to JSON
                String json = gson.toJson(items);
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkPrices() {
        for (int i = 0; i < 40_000; i++) {
            int shopBuyPrice = FireOfExchange.getExchangeShopPrice(i);
            int burn = getBurnPrice(null, i, false);
            if (shopBuyPrice != Integer.MAX_VALUE) {
                Preconditions.checkState(shopBuyPrice >= burn, "Item burns for more than shop price: " + i);
            }
        }
    }

    public static void openExchangeRateShop(Player player) {
        if (ShopHandler.getShopItems(SHOP_ID).isEmpty()) {
            createBurnPriceShop();
        }
        player.getShops().openShop(SHOP_ID);
        player.sendMessage("<icon=282> @red@You cannot buy anything here.@bla@ This interface only displays @pur@Nomad's Dissolving Rates!");
    }

    public static boolean hasValue(int itemId) {
        return getBurnPrice(null, itemId, false) != -1;
    }

    /**
     * Burning price.
     */
    public static int getBurnPrice(Player c, int itemId, boolean displayMessage) {
        if (Arrays.stream(JarsToPoints.JARS).anyMatch(it -> itemId == it)) {
            return JarsToPoints.FOE_POINTS;
        }

        return switch (itemId) {
            case 33237 -> 1;
            case 4087, 4585, 1149, 1187 -> 2500;
            case 7980, 7981, 7979, 21275, 23077, 24466 -> 10_000;  //Lumberjack Boots
            //Lumberjack Top
            //Lumberjack legs
            //Lumberjack Hat
            //Angler Hat
            //Angler Top
            //Angler Waders
            //Angler Boots
            //Rogue Top
            //Rogue Mask
            //Rogue Trousers
            //Rogue Gloves
            //Rogue Boots
            //Farmers Jacket
            //Farmers Boro Trousers
            //Farmers Boots
            //Farmers Strawhat
            //Prospector Helmet
            //Prospector Jacket
            //Prospector Legs
            //Prospector Boots
            //Pyromancer Garb
            //Pyromancer Robe
            //Pyromancer Hood
            //Pyromancer Boots
            //elder top
            //elder robe
            case 11157, 10933, 10939, 10940, 10941, 13258, 13259, 13260, 13261, 5553, 5554, 5555, 5556, 5557, 13642,
                 13640, 13644, 13646, 12013, 12014, 12015, 12016, 20704, 20706, 20708, 20710, 20517, 20520,
                 20595 ->  //elder hood
                    20_000;
            case Items.TROUVER_PARCHMENT, 20790 -> 25_000;  //barrows start
            // all barrows complete
            case 9032, 3694, 4722, 4720, 4716, 4718, 4714, 4712, 4708, 4710, 4736, 4738, 4732, 4734, 4753, 4755, 4757,
                 4759, 4745, 4747, 4749, 4751, 4724, 4726, 4728, 4730, Items.DRAGON_CHAINBODY, Items.DRAGON_CHAINBODY_G,
                 Items.DRAGON_PLATEBODY_G, Items.DRAGON_PLATELEGS_G, Items.DRAGON_FULL_HELM_G,
                 Items.DRAGON_KITESHIELD_G, Items.DRAGON_SQ_SHIELD_G, Items.DRAGON_PLATESKIRT_G, Items.DRAGON_BOOTS_G ->
                    50_000;  //ranger boots
            case 9030, 9042, 2951, 20366, 22249, 23444, 23240, 2577, Items.AMULET_OF_THE_DAMNED -> 100_000;   //b ring
            //archer ring
            //seers ring
            //trident of the sea
            //dragon platebody
            //dragon kite
            //tyrannical ring
            //treasonaus ring
            //tassets
            //primordials
            //pegasion
            //eternal
            //blowpipe
            case 6737, 6733, 6731, 11907, 21892, 21895, 12603, 12605, 11834, 13239, 13237, 13235, 12924,
                 12926 ->  //blowpipe
                    150_000;  //avernic
            //kodai wand
            case 9040, 9028, 2948, 20789, 22322, 21006, 22477 ->  //avernic hilt
                    250_000;  //crystal corrupt legs
            //crystal corrupt helm
            case 23848, 23842, 23845 ->  //crystal corrupt plate
                    275_000;  //ancestral
            //ancestral
            //ancestral
            //justiciar
            //justiciar
            case 21018, 21021, 21024, 22326, 22327, 22328 ->  //justiciar
                    375_000;  //3rd age range begin
            //3rd age range finish
            //3rd age mage begin
            //3rd age mage finish
            //3rd age melee begin
            //3rd age melee finish
            //trident of swamp
            case 2950, 10330, 10332, 10334, 10336, 10338, 10340, 10342, 10344, 10346, 10348, 10350, 10352, 12899,
                 Items.THIRD_AGE_PLATESKIRT, Items.THIRD_AGE_BOW, Items.THIRD_AGE_DRUIDIC_ROBE_TOP,
                 Items.THIRD_AGE_DRUIDIC_CLOAK, Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS, Items.THIRD_AGE_DRUIDIC_STAFF,
                 Items.THIRD_AGE_LONGSWORD, Items.THIRD_AGE_AXE, Items.THIRD_AGE_PICKAXE -> 500_000;
            case 26710, 26708 -> 750_000;  //craws bow u
            //craws bow
            //viggs mace u
            //viggs mace
            //thams sceptre u
            case 22547, 22550, 22542, 22545, 22552, 22555 ->  //thams sceptre
                    800_000;  //barb icons start
            case 10556, 10557, 10558, 10559 ->  //barb icons end
                    1_000_000;  //inquisitor mace
            //crystal blade
            //inquisitor helm
            //inquisitor plate
            //inquisitor skirt
            case 24417, 23995, 24419, 24420, 24421, Items.ZURIELS_HOOD, Items.ZURIELS_ROBE_BOTTOM,
                 Items.ZURIELS_ROBE_TOP, Items.STATIUSS_FULL_HELM, Items.STATIUSS_PLATEBODY, Items.STATIUSS_PLATELEGS ->
                    1_250_000;
            case Items.VESTAS_CHAINBODY, Items.VESTAS_PLATESKIRT, Items.MORRIGANS_COIF, Items.MORRIGANS_LEATHER_BODY,
                 Items.MORRIGANS_LEATHER_CHAPS, Items.VESTAS_SPEAR, Items.ZURIELS_STAFF -> 1_500_000;
            case Items.VESTAS_LONGSWORD, Items.STATIUSS_WARHAMMER -> 2_000_000;  //t helm
            //t chest
            //t legs
            //ancient gdsw
            case 26382, 26384, 26386, 26233, 26235 ->  //zart vambs
                    2_250_000;
            case 18, 20788 -> 2_500_000;
            case 20786 -> 10_000_000;
            case 21129 -> 25_000_000;
            case 20787 -> 3_750_000;
            case 12006 -> 400_000;
            case 25066, 25063, 25059 -> 100_000;
            case 20997 -> 20_000_000;
            case 26482, 26486 -> 35000;
            case 19553, 19547, 19544 -> 20_000;
            case 22325, 22323 -> 20_000_000;//bandos boots
            case 11836, Items.BLACK_MASK_10 -> 57500;
            case Items.MAGES_BOOK -> 5000;
            case Items.DAGONHAI_HAT, Items.DAGONHAI_ROBE_BOTTOM, Items.DAGONHAI_ROBE_TOP -> 100000;
            case Items.LONG_BONE -> 20000;
            case Items.BARRELCHEST_ANCHOR -> 100_000;
            case Items.DRAGON_2H_SWORD -> 125000;
            case 21902 ->  //dragon crossbow
                    245000;
            case Items.RING_OF_THE_GODS -> 55000;
            case 11772 ->//warrior i
                    38000;
            case Items.SARACHNIS_CUDGEL -> 150_000;
            case Items.RING_OF_THIRD_AGE -> 99000;
            case Items.BONECRUSHER_NECKLACE -> 15000;
            case 11785 ->//arma crossbow
                    74000;//seers i
            //archer i
            case 11770, 11771, 11773 ->//b ring i
                    200000;
            case 21015 ->//dihns bulwark
                    453000;
            case 12929 ->//serp helm
                    153000;//abby dagger
            case 13265, 13271 ->//abby dagger poison
                    45300;
            case 21633 ->//ancient wyvern shield
                    153000;
            case 21000 ->//twisted shield
                    353000;//crystal body
            //crystal helm
            case 23975, 23971, 23979 ->//crystal legs
                    60000;
            case 22324 ->//ghrazi rapier
                    5_000_000;
            case 20716 ->//tome of fire
                    150000;
            case 22975 ->//brimstone ring
                    53000;//tyrannical ring i
            //tres ring (i)
            case 12691, 12692, Items.RING_OF_THE_GODS_I -> 200000;
            case 19478 ->//light ballista
                    16500;
            case 12902 -> //toxic staff of the dead
                    200_000; //dfs
            case 11284, 11283 ->//dfs
                    200_000;//army helm
            //army plate
            case 11826, 11828, 11830 ->//arma leg
                    250_000;//tanz helm
            case 13196, 13198 ->//magma helm
                    300_000;
            case Items.IMBUED_HEART -> 100_000;
            case 21003 ->//elder maul
                    414_000;
            case 21079 ->//arcane scroll
                    200_000;
            case 11832 ->//bcp
                    175_000;
            case 12922 ->//tanz fang
                    150_000;
            case 13263 ->//bludgon
                    275_000;
            case 21034 ->//dex scroll
                    214_000;
            case Items.NEITIZNOT_FACEGUARD, Items.RING_OF_SUFFERING_I, Items.SLED -> 200_000;
            case 22978 ->  //dragon hunter lance
                    1_200_000;  //spectral
            case 12821, 12825 ->  //arcane
                    500_000;
            case 22981 ->  //ferocious gloves
                    250_000;
            case 26374 ->  //zart cbow
                    8_000_000;
            case 24422 ->  //nightmare staff
                    5_000_000;  //eldritch orb
            //harmonised orb
            case 24517, 24511, 24514 ->  //volatile orb
                    1_500_000;
            case 12817 ->//ely
                    2_250_000;
            case 2399 -> // FOE KEY
                    5_000;
            //SKILLING ARTEFACTS
            case 11180 ->//ancient coin
                    10000;
            case 681 ->//ancient talisman
                    10000;
            case 9034 ->//golden stat
                    50000;
            //CHEST ARTIFACTS
            case 21547 ->//small enriched bone
                    25000;
            case 21549 ->//medium enriched bone
                    50000;
            case 21551 ->//large enriched bone
                    100000;
            case 21553 ->//rare enriched bone
                    200000;
            case 21012 -> 500_000;
            case 25918 -> 1_500_000;
            case 25916 -> 750_000;
            //SLAYER HEADS
            case 2425 ->  //vorkath head
                    50000;
            //special items
            //chest rate relic
            case 21046, 22316 ->  //sword of xeros
                    500;
            //FOE PETS START
            case 30010 ->//postie pete
                    16250;//toucan
            case 30012, 30011 ->//imp
                    19500;
            case 30013 ->//penguin king
                    27500;
            case 30014 ->//klik
                    10_000_000;//melee pet
            //range pet
            case 30015, 30016, 30017 ->//magic pet
                    487500;//healer
            case 30018, 30019 ->//prayer
                    250_000;
            case 30020 ->//corrupt beast
                    25_000_000;
            case 30021 ->//roc pet
                    25_000_000;
            case 30022 ->//kratos pet
                    75_000_000;
            case 23939 ->//seren
                    65000;
            case 3128 -> 10_000_000;
            //FOE PETS END
/*            case 33128:
            case 33132:
            case 33136:
            case 33140:
                return 10000;*/

            case 11802, 11804, 11806, 11808 -> 300_000;
            // New List
            case 13576 -> 550_000;
            case 19481 -> 550_000;
            case 4151 -> 100_000;
            case 20784 -> 250_000;
            case 6585 -> 25_000;
            case 12002 -> 12_500;
            case 12004 -> 50000;
            case 11924, 11926 -> 100_000;
            case 12806, 12807 -> 200_000;
            case 21028 -> 250_000;
            case 11920 -> 250_000;
            case 6739 -> 25_000;
            case 24664 -> 1_250_000;
            case 24666 -> 1_250_000;
            case 24668 -> 1_250_000;
            case 691 ->//foe cert
                    10000;
            case 692 ->//foe cert
                    25000;
            case 693 ->//foe cert
                    50000;
            case 696 ->//foe cert
                    250000;
            case 8866 ->//uim key
                    100;
            case 8868 ->//perm uim key
                    4000;
            case 33073 ->//DWARF_OVERLOAD
                    2_500_000;
            case 33074 ->//PK_MASTER
                    750_000;
            case 33090 ->//MAGIC_MASTER
                    750_000;
            case 33091 ->//YIN_YANG
                    750_000;
            case 33102 ->//NOVICE_ZERK
                    750_000;
            case 33103 ->//NOVICE_MAGICIAN
                    750_000;
            case 33104 ->//NOVICE_RANGER
                    750_000;
            case 33105 ->//PRO_ZERK
                    1_500_000;
            case 33106 ->//PRO_MAGICIAN
                    1_500_000;
            case 33107 ->//PRO_RANGER
                    1_500_000;
            case 33108 ->//SWEDISH_SWINDLE
                    2_500_000;
            case 33117 ->//MONK_HEALS
                    2_000_000;
            case 33118 ->//DRAGON_FIRE
                    800_000;
            case 33119 ->//OVERLOAD_PROTECTION
                    1_000_000;
            case 33121 ->//CANNON_EXTENDER
                    1_000_000;
            case 33079 ->//RUNECRAFTER
                    500_000;
            case 33080 ->//PRO_FLETCHER
                    500_000;
            case 33087 ->//SKILLED_THIEF
                    500_000;
            case 33088 ->//CRAFTING_GURU
                    500_000;
            case 33089 ->//HOT_HANDS
                    500_000;
            case 33093 ->//DEMON_SLAYER
                    500_000;
            case 33094 ->//SLAYER_MASTER
                    500_000;
            case 33095 ->//PYROMANIAC
                    500_000;
            case 33096 ->//SKILLED_HUNTER
                    500_000;
            case 33097 ->//MOLTEN_MINER
                    500_000;
            case 33098 ->//WOODCHIPPER
                    500_000;
            case 33099 ->//BARE_HANDS
                    500_000;
            case 33100 ->//BARE_HANDS_X3
                    750_000;
            case 33101 ->//PRAYING_RESPECTS
                    500_000;
            case 33122 ->//PURE_SKILLS
                    5_000_000;
            case 33077 ->//IRON_GIANT
                    500_000;
            case 33078 ->//SLAYER_OVERRIDE
                    1_000_000;
            case 33072 ->//THE_FUSIONIST
                    2_000_000;
            case 33075 ->//WILDY_SLAYER
                    750_000;
            case 33076 ->//SNEAKY_SNEAKY
                    500_000;
            case 33081 ->//CHISEL_MASTER
                    500_000;
            case 33082 ->//AVAS_ACCOMPLICE
                    500_000;
            case 33083 ->//DEEPER_POCKETS
                    1_500_000;
            case 33084 ->//RECHARGER
                    2_000_000;
            case 33085 ->//MAGIC_PAPER_CHANCE
                    1_500_000;
            case 33086 ->//DRAGON_BAIT
                    500_000;
            case 33092 ->//FOUNDRY_MASTER
                    5_000_000;
            case 33114 ->//CASKET_MASTER
                    1_500_000;
            case 33115 ->//VOTING_KING
                    5_000_000;
            case 33116 ->//PET_LOCATOR
                    2_000_000;
            case 33120 ->//LUCKY_COIN
                    1_500_000;
            case 33123 ->//PC_PRO
                    1_500_000;
            case 33124 ->//SLAYER_GURU
                    500_000;
            case 33162 ->//WHIP
                    10_000_000;//SCYTHE
            case 33161, 33160 ->//TBOW
                    22_500_000;
            case 12422, 12437, 12600 -> 500_000;
            case 12954 -> 25_000;
            case 26719, 26718 -> 1_000_000;
            case 26720 -> 500_000;
            case 26714, 26715, 26716, 26221, 26223, 26225 -> 1_500_000;
            case 26227, 26229 -> 250_000;
            case 33159 -> 7_500_000;
            default -> {
                for (UpgradeMaterials value : UpgradeMaterials.VALUES) {
                    if (value.getReward().getId() == itemId) {
                        yield (int) (value.getCost() / 5);
                    }
                }
                yield -1;
            }
        };
    }


}
