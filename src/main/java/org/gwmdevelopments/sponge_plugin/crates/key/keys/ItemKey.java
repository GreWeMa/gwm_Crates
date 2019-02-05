package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemKey extends GiveableKey {

    private ItemStack item;

    public ItemKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode itemNode = node.getNode("ITEM");
            if (itemNode.isVirtual()) {
                throw new IllegalArgumentException("ITEM node does not exist!");
            }
            item = GWMLibraryUtils.parseItem(itemNode);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Item Key!", e);
        }
    }

    public ItemKey(Optional<String> id, boolean doNotWithdraw,
                   Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                   ItemStack item) {
        super("ITEM", id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.item = item;
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
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
