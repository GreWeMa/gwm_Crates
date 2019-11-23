package dev.gwm.spongeplugin.crates.superobject.drop.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.IDefaultRandombles;
import dev.gwm.spongeplugin.library.util.DefaultRandomableData;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.GiveableData;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractDrop extends AbstractSuperObject implements Drop,
        IDefaultRandombles {

    private final Optional<Currency> saleCurrency;
    private final Optional<BigDecimal> price;
    private final Optional<ItemStack> dropItem;
    private final Optional<String> customName;
    private final boolean showInPreview;
    //LevelRandomable
    private final int level;
    private final Optional<Integer> fakeLevel;
    private final Map<String, Integer> permissionLevels;
    private final Map<String, Integer> fakePermissionLevels;
    //WeightRandomable
    private final long weight;
    private final Optional<Long> fakeWeight;
    private final Map<String, Long> permissionWeights;
    private final Map<String, Long> fakePermissionWeights;

    public AbstractDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode dropItemNode = node.getNode("DROP_ITEM");
            ConfigurationNode customNameNode = node.getNode("CUSTOM_NAME");
            ConfigurationNode showInPreviewNode = node.getNode("SHOW_IN_PREVIEW");
            GiveableData giveableData = new GiveableData(node);
            saleCurrency = giveableData.getSaleCurrency();
            price = giveableData.getPrice();
            if (!dropItemNode.isVirtual()) {
                dropItem = Optional.of(GWMLibraryUtils.parseItem(dropItemNode));
            } else {
                dropItem = Optional.empty();
            }
            if (!customNameNode.isVirtual()) {
                customName = Optional.of(customNameNode.getString());
            } else {
                customName = Optional.empty();
            }
            showInPreview = showInPreviewNode.getBoolean(true);
            DefaultRandomableData defaultRandomableData = new DefaultRandomableData(node);
            level = defaultRandomableData.getLevel();
            fakeLevel = defaultRandomableData.getFakeLevel();
            permissionLevels = defaultRandomableData.getPermissionLevels();
            fakePermissionLevels = defaultRandomableData.getFakePermissionLevels();
            weight = defaultRandomableData.getWeight();
            fakeWeight = defaultRandomableData.getFakeWeight();
            permissionWeights = defaultRandomableData.getPermissionWeights();
            fakePermissionWeights = defaultRandomableData.getFakePermissionWeights();
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractDrop(String id,
                        GiveableData giveableData,
                        Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                        DefaultRandomableData defaultRandomableData) {
        super(id);
        this.saleCurrency = giveableData.getSaleCurrency();
        this.price = giveableData.getPrice();
        this.dropItem = dropItem;
        this.customName = customName;
        this.showInPreview = showInPreview;
        level = defaultRandomableData.getLevel();
        fakeLevel = defaultRandomableData.getFakeLevel();
        permissionLevels = defaultRandomableData.getPermissionLevels();
        fakePermissionLevels = defaultRandomableData.getFakePermissionLevels();
        weight = defaultRandomableData.getWeight();
        fakeWeight = defaultRandomableData.getFakeWeight();
        permissionWeights = defaultRandomableData.getPermissionWeights();
        fakePermissionWeights = defaultRandomableData.getFakePermissionWeights();
    }

    @Override
    public final SuperObjectCategory<Drop> category() {
        return GWMCratesSuperObjectCategories.DROP;
    }

    @Override
    public final void give(Player player, int amount, boolean force) {
        give(player, amount);
    }

    @Override
    public List<? extends AbstractDrop> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Currency> getSaleCurrency() {
        return saleCurrency;
    }

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    @Override
    public Optional<ItemStack> getDropItem() {
        return dropItem;
    }

    @Override
    public Optional<String> getCustomName() {
        return customName;
    }

    @Override
    public boolean isShowInPreview() {
        return showInPreview;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Optional<Integer> getFakeLevel() {
        return fakeLevel;
    }

    @Override
    public Map<String, Integer> getPermissionLevels() {
        return permissionLevels;
    }

    @Override
    public Map<String, Integer> getFakePermissionLevels() {
        return fakePermissionLevels;
    }

    @Override
    public long getWeight() {
        return weight;
    }

    @Override
    public Optional<Long> getFakeWeight() {
        return fakeWeight;
    }

    @Override
    public Map<String, Long> getPermissionWeights() {
        return permissionWeights;
    }

    @Override
    public Map<String, Long> getFakePermissionWeights() {
        return fakePermissionWeights;
    }
}
