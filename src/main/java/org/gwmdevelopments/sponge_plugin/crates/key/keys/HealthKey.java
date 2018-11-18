package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class HealthKey extends GiveableKey {

    private double health;

    public HealthKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode healthNode = node.getNode("HEALTH");
            if (healthNode.isVirtual()) {
                throw new RuntimeException("HEALTH node does not exist!");
            }
            health = healthNode.getDouble();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Health Key!", e);
        }
    }

    public HealthKey(String type, Optional<String> id, boolean doNotWithdraw,
                     Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                     double health) {
        super(type, id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.health = health;
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
            double value = player.get(Keys.HEALTH).orElse(0.D) + (health * amount);
            double max = player.get(Keys.MAX_HEALTH).orElse(20.0D);
            if (value > max) {
                value = max;
            }
            player.offer(Keys.HEALTH, value);
        }
    }

    @Override
    public int get(Player player) {
        return player.get(Keys.HEALTH).orElse(0.0D) >= health ? 1 : 0;
    }
}
