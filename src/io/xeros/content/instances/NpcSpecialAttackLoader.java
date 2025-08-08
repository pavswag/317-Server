package io.xeros.content.instances;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads special attack definitions from json and allows lookup by name.
 */
public class NpcSpecialAttackLoader {
    private static final Map<String, NpcSpecialAttack> SPECIALS = new HashMap<>();
    private static final Path FILE = Path.of("resources/boss_instance_special_attacks.json");

    static {
        if (Files.exists(FILE)) {
            try {
                List<NpcSpecialAttack> list = new Gson().fromJson(Files.newBufferedReader(FILE),
                        new TypeToken<List<NpcSpecialAttack>>() {}.getType());
                for (NpcSpecialAttack sa : list) {
                    SPECIALS.put(sa.getName(), sa);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<NpcSpecialAttack> getAll(List<String> names) {
        List<NpcSpecialAttack> list = new ArrayList<>();
        if (names == null) {
            return list;
        }
        for (String name : names) {
            NpcSpecialAttack sa = SPECIALS.get(name);
            if (sa != null) {
                list.add(sa.copy());
            }
        }
        return list;
    }
}
