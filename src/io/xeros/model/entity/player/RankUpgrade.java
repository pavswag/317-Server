package io.xeros.model.entity.player;

enum RankUpgrade {
    KRILLIN(Right.DONATOR, 20),
    GOTEN(Right.SUPER_DONATOR, 50),
    GOHAN(Right.GREAT_DONATOR,100),
    CELL(Right.EXTREME_DONATOR, 250),
    VEGETA(Right.MAJOR_DONATOR, 500),
    GOKU(Right.SUPREME_DONATOR, 750),
    GOGETTA(Right.GILDED_DONATOR, 1000),
    GOGETTA_SS(Right.PLATINUM_DONATOR, 1500),
    GOGETTA_SS2(Right.APEX_DONATOR, 2000),
    SS_BROLY(Right.ALMIGHTY_DONATOR, 15000);

    /**
     * The rights that will be appended if upgraded
     */
    public final Right rights;

    /**
     * The amount required for the upgrade
     */
    public final int amount;

    RankUpgrade(Right rights, int amount) {
        this.rights = rights;
        this.amount = amount;
    }
}
