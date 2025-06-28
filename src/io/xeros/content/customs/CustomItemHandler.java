package io.xeros.content.customs;

import io.xeros.annotate.PostInit;
import io.xeros.model.Graphic;
import io.xeros.model.items.ItemAction;
public class CustomItemHandler {

    @PostInit
    public static void handleCustomItem() {

        ItemAction.registerInventory(2520, 1, ((player, item) -> {
            player.startAnimation(918);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2522, 1, ((player, item) -> {
            player.startAnimation(919);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2524, 1, ((player, item) -> {
            player.startAnimation(920);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2526, 1, ((player, item) -> {
            player.startAnimation(921);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(13215, 1, ((player, item) -> {
            player.startAnimation(3414);
            player.forcedChat("Grrrrr!");
        }));
        ItemAction.registerInventory(13216, 1, ((player, item) -> {
            player.startAnimation(3413);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(13217, 1, ((player, item) -> {
            player.startAnimation(3541);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(13218, 1, ((player, item) -> {
            player.startAnimation(3839);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(4613, 1, ((player, item) -> {
            player.startAnimation(1902);
        }));
        ItemAction.registerInventory(3801, 1, ((player, item) -> {
            player.startAnimation(1329);
        }));
        ItemAction.registerInventory(4079, 1, ((player, item) -> {
            player.startAnimation(1457);
        }));
        ItemAction.registerInventory(4079, 2, ((player, item) -> {
            player.startAnimation(1458);
        }));
        ItemAction.registerInventory(4079, 3, ((player, item) -> {
            player.startAnimation(1459);
        }));
        ItemAction.registerInventory(13188, 1, ((player, item) -> {
            player.startAnimation(5283);
            player.startGraphic(new Graphic(1171));
        }));
        ItemAction.registerInventory(23446, 2, ((player, item) -> {
            player.startAnimation(8332);
            player.startGraphic(new Graphic(1680));
        }));
        ItemAction.registerInventory(6722, 1, ((player, item) -> {
            player.startAnimation(2840);
            player.forcedChat("Alas!");
        }));
        ItemAction.registerInventory(6722, 2, ((player, item) -> {
            player.startAnimation(2844);
            player.forcedChat("Mwuhahahaha!");
        }));
        ItemAction.registerInventory(27873, 1, (((player, item) -> {
            player.startAnimation(10045);
        })));
        ItemAction.registerInventory(716, 1, (((player, item) -> {
            player.startAnimation(908);
            player.startGraphic(new Graphic(81));
        })));
        ItemAction.registerInventory(25646, 1, (((player, item) -> {  //$100 Deal
            player.getItems().deleteItem2(25646, 1);
            player.getItems().addItemUnderAnyCircumstance(7776, 2);  //Credits
            player.getItems().addItemUnderAnyCircumstance(7774, 2);   //Credits

            player.getItems().addItemUnderAnyCircumstance(26886, 2);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(995, 55_000_000);  //Cash
            player.getItems().addItemUnderAnyCircumstance(696, 2);  //250,000 Nomad Point Certificate
            player.sendMessage("Thank you for supporting Turmoil. We hope you enjoy your bundle.");
        })));
        ItemAction.registerInventory(25649, 1, (((player, item) -> {  //$250 Deal
            player.getItems().deleteItem2(25649, 1);
            player.getItems().addItemUnderAnyCircumstance(25646, 1);  //$100 Deal
            player.getItems().addItemUnderAnyCircumstance(7776, 2);   //Credits
            player.getItems().addItemUnderAnyCircumstance(7775, 1);   //Credits

            player.getItems().addItemUnderAnyCircumstance(13346, 50);  //50x Ultra Mystery Boxes
            player.getItems().addItemUnderAnyCircumstance(12582, 40);  //40x Chamber Of Xeric Boxes
            player.getItems().addItemUnderAnyCircumstance(19891, 30);  //30x Theatre Of Blood Boxes
            player.getItems().addItemUnderAnyCircumstance(12579, 20);  //20x Arbograve Boxes
            player.getItems().addItemUnderAnyCircumstance(12588, 10);   //10x Donation Boxes
            player.getItems().addItemUnderAnyCircumstance(8167, 5);  //5x Nomad Mystery Chests
            player.sendMessage("Thank you for supporting Turmoil. We hope you enjoy your bundle.");
        })));

        ItemAction.registerInventory(25648, 1, (((player, item) -> {  //$750 Deal
            player.getItems().deleteItem2(25648, 1);
            player.getItems().addItemUnderAnyCircumstance(25649, 1);  //$250 deal
            player.getItems().addItemUnderAnyCircumstance(7776, 7);   //Credits
            player.getItems().addItemUnderAnyCircumstance(7775, 1);   //Credits

            player.getItems().addItemUnderAnyCircumstance(33186, 1);  //Mask Of Malar
            player.getItems().addItemUnderAnyCircumstance(33187, 1);  //Chest Of Malar
            player.getItems().addItemUnderAnyCircumstance(33188, 1);  //Bottoms Of Malar
            player.getItems().addItemUnderAnyCircumstance(33183, 1);  //Cape Of Malar
            player.getItems().addItemUnderAnyCircumstance(33149, 1);  //Nox staff
            player.getItems().addItemUnderAnyCircumstance(13681, 1);  //Codex
            player.getItems().addItemUnderAnyCircumstance(19720, 1);  //Occult (or)
            player.getItems().addItemUnderAnyCircumstance(23444, 1);  //Tormented bracelet (or)
            player.getItems().addItemUnderAnyCircumstance(26886, 1);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(11481, 1);  //Inf pots
            player.getItems().addItemUnderAnyCircumstance(22999, 1);  //Inf pots
            player.sendMessage("Thank you for supporting Turmoil. We hope you enjoy your bundle.");
        })));

        ItemAction.registerInventory(25650, 1, (((player, item) -> {  //$999 Deal
            player.getItems().deleteItem2(25650, 1);
            player.getItems().addItemUnderAnyCircumstance(25649, 1);  //£250 Deal
            player.getItems().addItemUnderAnyCircumstance(7776, 15);  //Credits

            player.getItems().addItemUnderAnyCircumstance(20128, 1);  //Hood of darkness
            player.getItems().addItemUnderAnyCircumstance(20131, 1);  //Robe top of darkness
            player.getItems().addItemUnderAnyCircumstance(20137, 1);  //Robe bottom of darkness
            player.getItems().addItemUnderAnyCircumstance(20134, 1);  //Gloves of darkness
            player.getItems().addItemUnderAnyCircumstance(20140, 1);  //Boots of darkness
            player.getItems().addItemUnderAnyCircumstance(27275, 1);  //Tumeken's Shadow
            player.getItems().addItemUnderAnyCircumstance(13681, 1);  //Codex
            player.getItems().addItemUnderAnyCircumstance(19720, 1);  //Occult (or)
            player.getItems().addItemUnderAnyCircumstance(26886, 2);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(11481, 1);  //Inf pots
            player.getItems().addItemUnderAnyCircumstance(22999, 1);  //Inf pots
            player.getItems().addItemUnderAnyCircumstance(11429, 1);  //Inf pots
            player.sendMessage("Thank you for supporting Turmoil. We hope you enjoy your bundle.");
        })));
    }
}
