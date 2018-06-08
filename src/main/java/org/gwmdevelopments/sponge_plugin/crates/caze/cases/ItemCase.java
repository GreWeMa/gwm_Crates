package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemCase extends Case {

    private ItemStack item;
    private boolean startPreviewOnLeftClick;

    public ItemCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode item_node = node.getNode("ITEM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (item_node.isVirtual()) {
                throw new RuntimeException("ITEM node does not exist!");
            }
            item = GWMCratesUtils.parseItem(item_node);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Item Case!", e);
        }
    }

    public ItemCase(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                    ItemStack item, boolean startPreviewOnLeftClick) {
        super("ITEM", id, price, sellCurrency);
        this.item = item;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
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
