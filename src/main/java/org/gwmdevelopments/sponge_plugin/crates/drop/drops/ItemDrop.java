package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public final class ItemDrop extends Drop {

    public static final String TYPE = "ITEM";

    private final ItemStack item;

    public ItemDrop(ConfigurationNode node) {
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

    public ItemDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                    int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                    Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                    Optional<String> customName,
                    ItemStack item) {
        super(id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels, customName);
        this.item = item;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        GWMCratesUtils.addItemStack(player, item, amount * item.getQuantity());
    }

    @Override
    public Optional<ItemStack> getDropItem() {
        Optional<ItemStack> superDropItem = super.getDropItem();
        return superDropItem.isPresent() ? superDropItem : Optional.of(item.copy());
    }

    public ItemStack getItem() {
        return item.copy();
    }
}
