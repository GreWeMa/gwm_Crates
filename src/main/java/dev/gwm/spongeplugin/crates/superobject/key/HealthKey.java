package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class HealthKey extends GiveableKey {

    public static final String TYPE = "HEALTH";

    private final double health;

    public HealthKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode healthNode = node.getNode("HEALTH");
            if (healthNode.isVirtual()) {
                throw new IllegalArgumentException("HEALTH node does not exist!");
            }
            health = healthNode.getDouble();
            if (health <= 0) {
                throw new IllegalArgumentException("Health is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public HealthKey(String id, boolean doNotWithdraw,
                     GiveableData giveableData, boolean doNotAdd,
                     double health) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (health <= 0) {
            throw new IllegalArgumentException("Health is equal to or less than 0!");
        }
        this.health = health;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            double value = player.get(Keys.HEALTH).orElse(0.0D) - (health * amount);
            if (value < 0) {
                value = 0;
            }
            player.offer(Keys.HEALTH, value);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            double value = player.get(Keys.HEALTH).orElse(0.0D) + (health * amount);
            double max = player.get(Keys.MAX_HEALTH).orElse(20.0D);
            if (value > max) {
                value = max;
            }
            player.offer(Keys.HEALTH, value);
        }
    }

    @Override
    public int get(Player player) {
        return (int) (player.get(Keys.HEALTH).orElse(0.0D) / health);
    }

    public double getHealth() {
        return health;
    }
}
