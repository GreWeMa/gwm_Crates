package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public final class ItemKey extends GiveableKey {

    public static final String TYPE = "ITEM";

    private final ItemStack item;

    public ItemKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode itemNode = node.getNode("ITEM");
            if (itemNode.isVirtual()) {
                throw new IllegalArgumentException("ITEM node does not exist!");
            }
            item = GWMLibraryUtils.parseItem(itemNode);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public ItemKey(Optional<String> id, boolean doNotWithdraw,
                   Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                   ItemStack item) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.item = item;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            GWMCratesUtils.removeItemStack(player, item, amount);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            GWMCratesUtils.addItemStack(player, item, amount);
        }
    }

    @Override
    public int get(Player player) {
        return GWMCratesUtils.getItemStackAmount(player, item);
    }

    public ItemStack getItem() {
        return item.copy();
    }
}
