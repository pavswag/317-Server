package io.xeros.content.skills.slayer;

/**
 * Represents a bonus demon slayer contract.
 */
public class DemonSlayerContract {

    private final DemonSlayerMaster.BossTier target;
    private final int amount;
    private boolean completed;

    public DemonSlayerContract(DemonSlayerMaster.BossTier target, int amount) {
        this.target = target;
        this.amount = amount;
    }

    public DemonSlayerMaster.BossTier getTarget() {
        return target;
    }

    public int getAmount() {
        return amount;
    }

    public boolean matches(DemonSlayerMaster.BossTier boss) {
        return target == boss;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }
}
