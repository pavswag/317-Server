package io.xeros.util.discord;

import io.xeros.Server;

public class StringUtil {

    public static int randomElement(int[] array) {
        return array[(int) (Server.random.get().nextDouble() * array.length)];
    }

    public static String kOrMil(int amount) {
        if (amount < 100_000)
            return String.valueOf(amount);
        if (amount < 10_000_000)
            return amount / 1_000 + "K";
        else
            return amount / 1_000_000 + "M";
    }

    public static String capitalizeJustFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

}
