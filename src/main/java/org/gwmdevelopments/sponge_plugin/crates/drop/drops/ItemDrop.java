package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.AbstractDrop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class ItemDrop extends AbstractDrop {

    private ItemStack item;

    public ItemDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode itemNode = node.getNode("ITEM");
            if (itemNode.isVirtual()) {
                throw new IllegalArgumentException("ITEM node does not exist!");
            }
            item = GWMCratesUtils.parseItem(itemNode);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Item Drop!", e);
        }
    }

    public ItemDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                    int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                    Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                    ItemStack item) {
        super("ITEM", id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
        this.item = item;
    }

    @Override
    public void give(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            player.getInventory().offer(item.copy());
        }
    }

    @Override
    public Optional<ItemStack> getDropItem() {
        Optional<ItemStack> superDropItem = super.getDropItem();
        return superDropItem.isPresent() ? superDropItem : Optional.of(item.copy());
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
