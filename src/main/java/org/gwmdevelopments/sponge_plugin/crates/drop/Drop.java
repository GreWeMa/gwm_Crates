package org.gwmdevelopments.sponge_plugin.crates.drop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.random_managers.LevelRandomManager;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.random_managers.WeightRandomManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Drop extends SuperObject implements Giveable,
        LevelRandomManager.LevelRandomable,
        WeightRandomManager.WeightRandomable {

    private final Optional<BigDecimal> price;
    private final Optional<Currency> sellCurrency;
    private final Optional<ItemStack> dropItem;
    private final Optional<String> customName;
    private final boolean showInPreview;
    private final Optional<Integer> level;
    private final Optional<Integer> fakeLevel;
    private final Map<String, Integer> permissionLevels;
    private final Map<String, Integer> permissionFakeLevels;
    private final Optional<Long> weight;
    private final Optional<Long> fakeWeight;
    private final Map<String, Long> permissionWeights;
    private final Map<String, Long> permissionFakeWeights;

    public Drop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            ConfigurationNode dropItemNode = node.getNode("DROP_ITEM");
            ConfigurationNode customNameNode = node.getNode("CUSTOM_NAME");
            ConfigurationNode showInPreviewNode = node.getNode("SHOW_IN_PREVIEW");
            ConfigurationNode levelNode = node.getNode("LEVEL");
            ConfigurationNode fakeLevelNode = node.getNode("FAKE_LEVEL");
            ConfigurationNode permissionLevelsNode = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permissionFakeLevelsNode = node.getNode("PERMISSION_FAKE_LEVELS");
            ConfigurationNode weightNode = node.getNode("WEIGHT");
            ConfigurationNode fakeWeightNode = node.getNode("FAKE_WEIGHT");
            ConfigurationNode permissionWeightsNode = node.getNode("PERMISSION_WEIGHTS");
            ConfigurationNode permissionFakeWeightsNode = node.getNode("PERMISSION_FAKE_WEIGHTS");
            if (!priceNode.isVirtual()) {
                price = Optional.of(new BigDecimal(priceNode.getString()));
            } else {
                price = Optional.empty();
            }
            if (!sellCurrencyNode.isVirtual()) {
                String sellCurrencyId = sellCurrencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new IllegalArgumentException("Economy Service not found, but parameter \"SELL_CURRENCY\" specified!");
                }
                sellCurrency = GWMCratesUtils.getCurrencyById(optionalEconomyService.get(), sellCurrencyId);
                if (!sellCurrency.isPresent()) {
                    throw new IllegalArgumentException("Currency \"" + sellCurrencyId + "\" not found!");
                }
            } else {
                sellCurrency = Optional.empty();
            }
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
            if (!levelNode.isVirtual()) {
                int tempLevel = levelNode.getInt(1);
                if (tempLevel < 1) {
                    throw new IllegalArgumentException("LEVEL is less than 1!");
                }
                level = Optional.of(tempLevel);
            } else {
                level = Optional.empty();
            }
            if (!fakeLevelNode.isVirtual()) {
                int tempFakeLevel = fakeLevelNode.getInt(1);
                if (tempFakeLevel < 1) {
                    throw new IllegalArgumentException("FAKE_LEVEL is less than 1!");
                }
                fakeLevel = Optional.of(tempFakeLevel);
            } else {
                fakeLevel = Optional.empty();
            }
            if (!permissionLevelsNode.isVirtual()) {
                permissionLevels = permissionLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
                if (permissionLevels.values().stream().anyMatch(i -> i < 1)) {
                    throw new IllegalArgumentException("PERMISSION_LEVELS contains level less than 1!");
                }
            } else {
                permissionLevels = new HashMap<>();
            }
            if (!permissionFakeLevelsNode.isVirtual()) {
                permissionFakeLevels = permissionFakeLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
                if (permissionFakeLevels.values().stream().anyMatch(i -> i < 1)) {
                    throw new IllegalArgumentException("PERMISSION_FAKE_LEVELS contains level less than 1!");
                }
            } else {
                permissionFakeLevels = new HashMap<>();
            }
            if (!weightNode.isVirtual()) {
                long tempWeight = weightNode.getLong(1L);
                if (tempWeight < 1) {
                    throw new IllegalArgumentException("WEIGHT is less than 1!");
                }
                weight = Optional.of(tempWeight);
            } else {
                weight = Optional.empty();
            }
            if (!fakeWeightNode.isVirtual()) {
                long tempFakeWeight = fakeWeightNode.getLong(1L);
                if (tempFakeWeight < 1) {
                    throw new IllegalArgumentException("FAKE_WEIGHT is less than 1!");
                }
                fakeWeight = Optional.of(tempFakeWeight);
            } else {
                fakeWeight = Optional.empty();
            }
            if (!permissionWeightsNode.isVirtual()) {
                permissionWeights = permissionWeightsNode.getValue(new TypeToken<Map<String, Long>>(){});
                if (permissionWeights.values().stream().anyMatch(i -> i < 1)) {
                    throw new IllegalArgumentException("PERMISSION_WEIGHTS contains level less than 1!");
                }
            } else {
                permissionWeights = new HashMap<>();
            }
            if (!permissionFakeWeightsNode.isVirtual()) {
                permissionFakeWeights = permissionFakeWeightsNode.getValue(new TypeToken<Map<String, Long>>(){});
                if (permissionFakeWeights.values().stream().anyMatch(i -> i < 1)) {
                    throw new IllegalArgumentException("PERMISSION_FAKE_WEIGHTS contains level less than 1!");
                }
            } else {
                permissionFakeWeights = new HashMap<>();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public Drop(Optional<String> id,
                Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem,
                Optional<String> customName, boolean showInPreview,
                Optional<Integer> level, Optional<Integer> fakeLevel,
                Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                Optional<Long> weight, Optional<Long> fakeWeight,
                Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights) {
        super(id);
        this.price = price;
        this.sellCurrency = sellCurrency;
        this.dropItem = dropItem;
        this.customName = customName;
        this.showInPreview = showInPreview;
        this.level = level;
        this.fakeLevel = fakeLevel;
        this.permissionLevels = permissionLevels;
        this.permissionFakeLevels = permissionFakeLevels;
        this.weight = weight;
        this.fakeWeight = fakeWeight;
        this.permissionWeights = permissionWeights;
        this.permissionFakeWeights = permissionFakeWeights;
    }

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.DROP;
    }

    public abstract void give(Player player, int amount);

    @Override
    public final void give(Player player, int amount, boolean force) {
        give(player, amount);
    }

    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public Optional<Currency> getSellCurrency() {
        return sellCurrency;
    }

    public Optional<ItemStack> getDropItem() {
        return dropItem;
    }

    public Optional<String> getCustomName() {
        return customName;
    }

    public boolean isShowInPreview() {
        return showInPreview;
    }

    @Override
    public Optional<Integer> getLevel() {
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
    public Map<String, Integer> getPermissionFakeLevels() {
        return permissionFakeLevels;
    }

    @Override
    public Optional<Long> getWeight() {
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
    public Map<String, Long> getPermissionFakeWeights() {
        return permissionFakeWeights;
    }
}
