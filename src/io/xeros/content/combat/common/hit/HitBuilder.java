package io.xeros.content.combat.common.hit;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.content.combat.formula.rework.MeleeCombatFormula;
import io.xeros.content.combat.formula.rework.RangeCombatFormula;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Data
public class HitBuilder {

    @Nonnull final Entity attacker, defender;
    final CombatType combatType;
    Hitmark hitmark;
    int damage, delay;
    boolean accurate, checkNegatedDamage, immune;
    @Nonnull private Consumer<HitBuilder> delayedExecutionConsumer;

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
    }

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType, boolean checkNegatedDamage) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
        this.checkNegatedDamage = checkNegatedDamage;
    }

    public HitBuilder(@NotNull final Entity attacker, @NotNull final Entity defender, int damage, CombatType combatType, boolean checkNegatedDamage, boolean isImmune) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.combatType = combatType;
        this.checkNegatedDamage = checkNegatedDamage;
        this.immune = isImmune;
    }

    public HitBuilder setAnimation(int id) {
        final Animation animation = new Animation(id);
        this.attacker.startAnimation(animation);
        return this;
    }

    public HitBuilder setStartGraphic(int id, int speed, int height) {
        final Graphic graphic = new Graphic(id, speed, height);
        this.attacker.startGraphic(graphic);
        return this;
    }

    public HitBuilder setEndGraphic(int id, int speed, int height) {
        Graphic graphic = new Graphic(id, speed, height);
        this.defender.startGraphic(graphic);
        return this;
    }


    final boolean isAccurate() {
        var roll = Server.random.get().nextDouble();
        double accurate = 0.0D;
        if (this.combatType == null) return true;
        if (this.combatType.equals(CombatType.RANGE)) {
            accurate = RangeCombatFormula.STANDARD.getAccuracy(this.attacker, this.defender, 1, 1);
        } else if (this.combatType.equals(CombatType.MAGE)) {
            accurate = MagicCombatFormula.STANDARD.getAccuracy(this.attacker, this.defender, 1, 1);
        } else if (this.combatType.equals(CombatType.MELEE)) {
            accurate = MeleeCombatFormula.get().getAccuracy(this.attacker, this.defender, 1, 1);
        }
        return accurate >= roll;
    }

    public HitBuilder submit() {
        Player player = (Player) this.defender;
        if (this.checkNegatedDamage && this.damage >= 2) this.negate();
        this.setAccurate(this.isAccurate());
        if (this.immune) this.immunity();
        else if (!this.accurate && this.damage <= 0) this.block();
        else if (this.accurate && this.damage > 0) this.setHitmark(Hitmark.HIT);
        else this.block();
        if (player.inGodmode()) this.block();
        if (!this.checkNegatedDamage && player.prayerActive[CombatPrayer.PROTECT_FROM_MAGIC] && this.combatType.equals(CombatType.MAGE)) {
            this.damage = (int) (this.damage * 0.80);
        }
        if (!this.checkNegatedDamage && player.prayerActive[CombatPrayer.PROTECT_FROM_MELEE] && this.combatType.equals(CombatType.MELEE)) {
            this.damage = (int) (this.damage * 0.80);
        }
        if (!this.checkNegatedDamage && player.prayerActive[CombatPrayer.PROTECT_FROM_RANGED] && this.combatType.equals(CombatType.RANGE)) {
            this.damage = (int) (this.damage * 0.80);
        }
        this.defender.appendDamage(this.attacker, this.damage, this.hitmark);
        return this;
    }

    final void block() {
        this.setDamage(0);
        this.setAccurate(false);
        this.setHitmark(Hitmark.MISS);
    }

    final void immunity() {
        this.setDamage(0);
        this.setAccurate(false);
        this.setHitmark(Hitmark.MISS);
    }

    final void negate() {
        if (this.damage > 2) {
            var damage = this.damage / 2;
            this.setDamage(damage);
        }
    }

    public final void execute() {
        delayedExecutionConsumer = consume();
        delayedExecutionConsumer.accept(this);
    }

    public final HitBuilder consume(Consumer<HitBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    final Consumer<HitBuilder> consume() {
        return hitBuilder -> CycleEventHandler.getSingleton().addEvent(hitBuilder.getAttacker(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                hitBuilder.submit();
                container.stop();
            }
        }, this.delay + 1);
    }

    public final void executeUnderCondition(BooleanSupplier condition) {
        delayedExecutionConsumer = ConsumeUnderCondition(condition);
        delayedExecutionConsumer.accept(this);
    }

    final Consumer<HitBuilder> ConsumeUnderCondition(BooleanSupplier condition) {
        return hitBuilder -> CycleEventHandler.getSingleton().addEvent(hitBuilder.getAttacker(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (condition.getAsBoolean()) {
                    hitBuilder.submit();
                }
                container.stop();
            }
        }, this.delay + 1);
    }
}
