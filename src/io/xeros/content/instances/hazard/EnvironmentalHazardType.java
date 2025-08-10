package io.xeros.content.instances.hazard;

public enum EnvironmentalHazardType {
    FIRE_TILE(502),
    POISON_MIST(110),
    CRUMBLING_FLOOR(60),
    DIRECT_HIT(157);

    private final int gfxId;

    EnvironmentalHazardType(int gfxId) {
        this.gfxId = gfxId;
    }

    public int getGfxId() {
        return gfxId;
    }
}
