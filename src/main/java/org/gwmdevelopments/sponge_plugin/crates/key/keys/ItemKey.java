package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemKey extends Key {

    private ItemStack item;

    public ItemKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode itemNode = node.getNode("ITEM");
            if (itemNode.isVirtual()) {
                throw new RuntimeException("ITEM node does not exist!");
            }
            item = GWMCratesUtils.parseItem(itemNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Item Key!", e);
        }
    }

    public ItemKey(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                   ItemStack item) {
        super("ITEM", id, price, sellCurrency);
        this.item = item;
    }

    @Override
    public void add(Player player, int amount) {
        GWMCratesUtils.addItemStack(player, item, amount);
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
