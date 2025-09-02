package io.xeros.content.instances.aoe;

import io.xeros.model.items.GameItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks items gained during an AOE tier run.
 */
public class AoeRewardTracker {

    private final Map<Integer, Long> items = new LinkedHashMap<>();

    public void add(GameItem item) {
        items.merge(item.getId(), (long) item.getAmount(), Long::sum);
    }

    public void addAll(List<GameItem> list) {
        if (list != null) {
            list.forEach(this::add);
        }
    }

    public List<GameItem> snapshot() {
        List<GameItem> list = new ArrayList<>();
        items.forEach((id, amt) -> list.add(new GameItem(id, amt.intValue())));
        return list;
    }

    public void clear() {
        items.clear();
    }
}
