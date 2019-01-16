package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class FoodKey extends GiveableKey {

    private int food;

    public FoodKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode foodNode = node.getNode("FOOD");
            if (foodNode.isVirtual()) {
                throw new IllegalArgumentException("FOOD node does not exist!");
            }
            food = foodNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Food Key!", e);
        }
    }

    public FoodKey(String type, Optional<String> id, boolean doNotWithdraw,
                   Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                   int food) {
        super(type, id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.food = food;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            int value = player.get(Keys.FOOD_LEVEL).orElse(0) - (food * amount);
            if (value < 0) {
                value = 0;
            }
            player.offer(Keys.FOOD_LEVEL, value);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            int value = player.get(Keys.FOOD_LEVEL).orElse(0) + (food * amount);
            if (value > 20) {
                value = 20;
            }
            player.offer(Keys.FOOD_LEVEL, value);
        }
    }

    @Override
    public int get(Player player) {
        return player.get(Keys.FOOD_LEVEL).orElse(0) >= food ? 1 : 0;
    }
}
