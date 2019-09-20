package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.DefaultRandomableData;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public final class ItemDrop extends AbstractDrop {

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
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ItemDrop(String id,
                    GiveableData giveableData,
                    Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                    DefaultRandomableData defaultRandomableData,
                    ItemStack item) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
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
