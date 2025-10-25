package io.xeros.content.instances.aoe;

import java.util.Collections;
import java.util.List;

/**
 * Data transfer object describing how to build a dynamic AOE instance.
 */
public class AoeZoneMapDef {

    public static class Source {
        private int fromX;
        private int fromY;
        private int width;
        private int height;
        private int z;

        public int getFromX() { return fromX; }
        public int getFromY() { return fromY; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getZ() { return z; }
    }

    public static class Target {
        private int baseX;
        private int baseY;
        private int z;

        public int getBaseX() { return baseX; }
        public int getBaseY() { return baseY; }
        public int getZ() { return z; }
    }

    public static class Spawn {
        private int x;
        private int y;
        private int z;

        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
    }

    public static class Npc {
        private int id;
        private int x;
        private int y;
        private int z;
        private int radius;
        private boolean walk = true;

        public int getId() { return id; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public int getRadius() { return radius; }
        public boolean isWalk() { return walk; }
    }

    private String id;
    private Source source;
    private Target target;
    private Spawn spawn;
    private List<Npc> npcs = Collections.emptyList();

    public String getId() { return id; }
    public Source getSource() { return source; }
    public Target getTarget() { return target; }
    public Spawn getSpawn() { return spawn; }
    public List<Npc> getNpcs() { return npcs == null ? Collections.emptyList() : npcs; }
}

