package org.gwmdevelopments.sponge_plugin.crates.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.GiveableSuperObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class Key extends GiveableSuperObject {

    public Key(ConfigurationNode node) {
        super(node);
    }

    public Key(String type, Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency) {
        super(type, id, price, sellCurrency);
    }

    public abstract void add(Player player, int amount);

    public abstract int get(Player player);

    @Override
    public void give(Player player, int amount) {
        add(player, amount);
    }
}
