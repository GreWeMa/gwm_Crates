package dev.gwm.spongeplugin.crates.superobject.caze;

import dev.gwm.spongeplugin.crates.superobject.caze.base.GiveableCase;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemCase extends GiveableCase {

    public static final String TYPE = "ITEM";

    private final ItemStack item;
    private final boolean startPreviewOnLeftClick;

    public ItemCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode itemNode = node.getNode("ITEM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (itemNode.isVirtual()) {
                throw new IllegalArgumentException("ITEM node does not exist!");
            }
            item = GWMLibraryUtils.parseItem(itemNode);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ItemCase(String id, boolean doNotWithdraw,
                    GiveableData giveableData, boolean doNotAdd,
                    ItemStack item, boolean startPreviewOnLeftClick) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        this.item = item;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
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

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }
}
