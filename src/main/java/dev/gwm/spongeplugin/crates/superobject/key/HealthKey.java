package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
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
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public HealthKey(Optional<String> id, boolean doNotWithdraw,
                     Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                     double health) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
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
