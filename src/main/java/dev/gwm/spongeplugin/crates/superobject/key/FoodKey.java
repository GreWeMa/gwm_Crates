package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public final class FoodKey extends GiveableKey {

    public static final String TYPE = "FOOD";

    private final int food;

    public FoodKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode foodNode = node.getNode("FOOD");
            if (foodNode.isVirtual()) {
                throw new IllegalArgumentException("FOOD node does not exist!");
            }
            food = foodNode.getInt();
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public FoodKey(Optional<String> id, boolean doNotWithdraw,
                   Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                   int food) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.food = food;
    }

    @Override
    public String type() {
        return TYPE;
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
        return player.get(Keys.FOOD_LEVEL).orElse(0) / food;
    }

    public int getFood() {
        return food;
    }
}
