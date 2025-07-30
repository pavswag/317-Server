package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class ToaInstances {

    public static void startBaba(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.BABA_BOSS_ROOM_BOUNDARY,
                area -> new SoloBaba(area),
                new Position(3808, 5406, 0));
        inst.enter(player);
    }

    public static void startCrondis(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.CRONDIS_BOSS_ROOM_BOUNDARY,
                area -> new SoloCrondis(area),
                new Position(3934, 5280, 0));
        inst.enter(player);
    }

    public static void startApmeken(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.APMEKEN1_BOSS_ROOM_BOUNDARY,
                area -> new SoloApmeken(area),
                new Position(3808, 5280, 0));
        inst.enter(player);
    }

    public static void startAkkha(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.AKKHA_BOSS_ROOM_BOUNDARY,
                area -> new SoloAkkha(area),
                new Position(3680, 5408, 1));
        inst.enter(player);
    }

    public static void startKephri(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.KEPHRI_BOSS_ROOM_BOUNDARY,
                area -> new SoloKephri(area),
                new Position(3550, 5408, 0));
        inst.enter(player);
    }

    public static void startTumekensWarden(Player player) {
        ToaInstance inst = new ToaInstance(
                TombsOfAmascutConstants.TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY,
                area -> new SoloTumekensWarden(area),
                new Position(3816, 5152, 2));
        inst.enter(player);
    }
}
