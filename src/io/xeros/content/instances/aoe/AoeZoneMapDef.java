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

    public static class Group {
        public static class Box {
            private int x1;
            private int y1;
            private int x2;
            private int y2;

            public int getX1() { return x1; }
            public int getY1() { return y1; }
            public int getX2() { return x2; }
            public int getY2() { return y2; }
        }

        private int npcId;
        private Box box;
        private int count = 1;
        private int respawnTicks = 50;
        private int agroRadius = 6;
        private int wanderRadius = 4;

        public int getNpcId() { return npcId; }
        public Box getBox() { return box; }
        public int getCount() { return Math.max(1, count); }
        public int getRespawnTicks() { return Math.max(1, respawnTicks); }
        public int getAgroRadius() { return Math.max(1, agroRadius); }
        public int getWanderRadius() { return Math.max(0, wanderRadius); }
    }

    private String id;
    private Source source;
    private Target target;
    private Spawn spawn;
    private List<Group> groups = Collections.emptyList();

    public String getId() { return id; }
    public Source getSource() { return source; }
    public Target getTarget() { return target; }
    public Spawn getSpawn() { return spawn; }
    public List<Group> getGroups() { return groups == null ? Collections.emptyList() : groups; }
}

