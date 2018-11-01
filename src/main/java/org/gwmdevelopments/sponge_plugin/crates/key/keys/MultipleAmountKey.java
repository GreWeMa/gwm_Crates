package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class MultipleAmountKey extends GiveableKey {

    private Key childKey;
    private int amount;

    public MultipleAmountKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childKeyNode = node.getNode("CHILD_KEY");
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            if (childKeyNode.isVirtual()) {
                throw new RuntimeException("CHILD_KEY node does not exist!");
            }
            childKey = (Key) GWMCratesUtils.createSuperObject(childKeyNode, SuperObjectType.KEY);
            amount = amountNode.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Multiple Amount Key!", e);
        }
    }

    public MultipleAmountKey(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                             AbstractKey childKey, int amount) {
        super("TIMED", id, price, sellCurrency);
        this.childKey = childKey;
        this.amount = amount;
    }

    @Override
    public void withdraw(Player player, int amount) {
        childKey.withdraw(player, amount * this.amount);
    }

    @Override
    public void give(Player player, int amount) {
        if (childKey instanceof Giveable) {
            ((Giveable) childKey).give(player, amount * this.amount);
        }
    }

    @Override
    public int get(Player player) {
        return childKey.get(player) / amount;
    }

    public Key getChildKey() {
        return childKey;
    }

    public void setChildKey(Key childKey) {
        this.childKey = childKey;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
