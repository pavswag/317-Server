package io.xeros.content.items.aoeweapons;

import lombok.Getter;

@Getter
public enum AoeWeapons {

    BOW_1(28688, 2, 25, 4, 426,570, "ranged"),
    BOW_2(20997, 3, 55, 4, 426, 593, "ranged"),
    BOW_3(33058, 4, 90, 4, 426, 551, "ranged"),
    BOW_4(30152, 4, 120, 4, 426, 554, "ranged"),
    BOW_5(30205, 5, 140, 4, 426, 597, "ranged"),
    BOW_GOD(30203, 9, 250, 4, 426, 2140, "ranged"),

    CUSTOM_MOD_MARK_SWORD(30350, 9, 880, 4, 1133, 2305, "melee"),
    MELEE_1(28534, 2, 25, 4, 7514, 1432, "melee"),
    MELEE_2(22325, 4, 55, 4, 8290, 1432, "melee"),
    MELEE_3(25736, 4, 95, 4, 8290, 1432, "melee"),
    MELEE_4(25739, 4, 125, 4, 8290, 1432, "melee"),
    MELEE_5(33184, 4, 145, 4, 8290, 1433, "melee"),
    MELEE_GOD(30304, 9, 255, 4, 8290, 1433, "melee"),
    WRAITH(33431, 4, 185, 4, 8290, 1433, "melee"),
    WRAITHSTAFF(33433, 4, 185, 4, 8290, 1433, "melee"),
    WRAITHBOW(33434, 4, 185, 4, 8290, 1433, "melee"),

    STAFF_1(33169, 2, 25, 4, 812, 2224, "mage"),
    STAFF_2(33170, 3, 55, 4, 812, 2225, "mage"),
    STAFF_3(33171, 4, 90, 4, 812, 551, "mage"),
    STAFF_4(33172, 4, 140, 3, 812, 554, "mage"),
    STAFF_5(33174, 5, 170, 3, 812, 597, "mage"),
    STAFF_GOD(33173, 6, 260, 3, 812, 2140, "mage"),
    ;

    public final String style;
    public int ID;
    public int Size;
    public int DMG;
    public int Delay;
    public int anim;
    public int gfx;
    AoeWeapons(int ID, int Size, int DMG, int Delay, int anim, int gfx, String style) {
        this.ID = ID;
        this.Size = Size;
        this.DMG = DMG;
        this.Delay = Delay;
        this.anim = anim;
        this.gfx = gfx;
        this.style = style;
    }


}
