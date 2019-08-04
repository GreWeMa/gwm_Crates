package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.Giveable;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public final class MultipleAmountKey extends GiveableKey {

    public static final String TYPE = "MULTIPLE-AMOUNT";

    private final Key childKey;
    private final int amount;

    public MultipleAmountKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childKeyNode = node.getNode("CHILD_KEY");
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            if (childKeyNode.isVirtual()) {
                throw new IllegalArgumentException("CHILD_KEY node does not exist!");
            }
            childKey = (Key) GWMCratesUtils.createSuperObject(childKeyNode, SuperObjectType.KEY);
            amount = amountNode.getInt(1);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public MultipleAmountKey(Optional<String> id, boolean doNotWithdraw,
                             Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                             Key childKey, int amount) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.childKey = childKey;
        this.amount = amount;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            childKey.withdraw(player, amount * this.amount, force);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            if (childKey instanceof Giveable) {
                ((Giveable) childKey).give(player, amount * this.amount, force);
            }
        }
    }

    @Override
    public int get(Player player) {
        return childKey.get(player) / amount;
    }

    public Key getChildKey() {
        return childKey;
    }

    public int getAmount() {
        return amount;
    }
}
