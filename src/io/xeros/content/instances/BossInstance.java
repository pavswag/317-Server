package io.xeros.content.instances;

import io.xeros.content.bosses.Cerberus;
import io.xeros.content.bosses.Kraken;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.bosses.bryophyta.Bryophyta;
import io.xeros.content.bosses.dukesucellus.DukeSucellus;
import io.xeros.content.bosses.grotesqueguardians.GrotesqueInstance;
import io.xeros.content.bosses.hydra.AlchemicalHydra;
import io.xeros.content.bosses.nightmare.NightmareInstance;
import io.xeros.content.bosses.obor.OborInstance;
import io.xeros.content.bosses.zulrah.Zulrah;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.function.Consumer;

/**
 * Represents a boss instance that can be unlocked and started.
 */
public enum BossInstance {
    ZULRAH("Zulrah", 5_000_000, p -> new Zulrah(p).initialize()),
    KRAKEN("Kraken", 2_000_000, Kraken::init),
    CERBERUS("Cerberus", 3_000_000, Cerberus::init),
    VORKATH("Vorkath", 4_000_000, p -> Vorkath.enterInstance(p, p.getIndex() * 4)),
    HYDRA("Alchemical Hydra", 6_000_000, p -> new AlchemicalHydra(p)),
    GROTESQUE_GUARDIANS("Grotesque Guardians", 4_000_000, p -> { GrotesqueInstance i = new GrotesqueInstance(); i.enter(p); }),
    OBOR("Obor", 1_000_000, p -> new OborInstance().begin(p)),
    BRYOPHYTA("Bryophyta", 1_000_000, p -> new Bryophyta().enter(p)),
    DUKE_SUCELLUS("Duke Sucellus", 8_000_000, p -> { DukeSucellus inst = new DukeSucellus(p, Boundary.DUKE_INSTANCE); DukeSucellus.enter(p, inst); }),
    NIGHTMARE("Nightmare", 10_000_000, p -> { NightmareInstance inst = new NightmareInstance(false); inst.add(p); inst.countdown(); });

    private final String name;
    private final long cost;
    private final Consumer<Player> starter;

    BossInstance(String name, long cost, Consumer<Player> starter) {
        this.name = name;
        this.cost = cost;
        this.starter = starter;
    }

    public String getName() {
        return name;
    }

    public long getCost() {
        return cost;
    }

    public void start(Player player) {
        starter.accept(player);
    }
}

