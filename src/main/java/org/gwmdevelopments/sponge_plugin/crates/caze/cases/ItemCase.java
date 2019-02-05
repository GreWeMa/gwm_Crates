package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.GiveableCase;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemCase extends GiveableCase {

    private ItemStack item;
    private boolean startPreviewOnLeftClick;

    public ItemCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode item_node = node.getNode("ITEM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (item_node.isVirtual()) {
                throw new IllegalArgumentException("ITEM node does not exist!");
            }
            item = GWMLibraryUtils.parseItem(item_node);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Item Case!", e);
        }
    }

    public ItemCase(Optional<String> id, boolean doNotWithdraw,
                    Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                    ItemStack item, boolean startPreviewOnLeftClick) {
        super("ITEM", id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.item = item;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
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

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public void setStartPreviewOnLeftClick(boolean startPreviewOnLeftClick) {
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
    }
}
