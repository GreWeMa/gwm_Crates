package org.gwmdevelopments.sponge_plugin.crates.drop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
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

public abstract class Drop extends SuperObject implements Giveable {

    private final Optional<BigDecimal> price;
    private final Optional<Currency> sellCurrency;
    private final int level;
    private final Optional<ItemStack> dropItem;
    private final Optional<Integer> fakeLevel;
    private final Map<String, Integer> permissionLevels;
    private final Map<String, Integer> permissionFakeLevels;
    private final Optional<String> customName;

    public Drop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            ConfigurationNode levelNode = node.getNode("LEVEL");
            ConfigurationNode dropItemNode = node.getNode("DROP_ITEM");
            ConfigurationNode fakeLevelNode = node.getNode("FAKE_LEVEL");
            ConfigurationNode permissionLevelsNode = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permissionFakeLevelsNode = node.getNode("PERMISSION_FAKE_LEVELS");
            ConfigurationNode customNameNode = node.getNode("CUSTOM_NAME");
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
            level = levelNode.getInt(1);
            if (level < 1) {
                throw new IllegalArgumentException("LEVEL is less than 1!");
            }
            if (!dropItemNode.isVirtual()) {
                dropItem = Optional.of(GWMLibraryUtils.parseItem(dropItemNode));
            } else {
                dropItem = Optional.empty();
            }
            if (!fakeLevelNode.isVirtual()) {
                fakeLevel = Optional.of(fakeLevelNode.getInt(1));
            } else {
                fakeLevel = Optional.empty();
            }
            if (!permissionLevelsNode.isVirtual()) {
                permissionLevels = permissionLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
            } else {
                permissionLevels = new HashMap<>();
            }
            if (!permissionFakeLevelsNode.isVirtual()) {
                permissionFakeLevels = permissionFakeLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
            } else {
                permissionFakeLevels = new HashMap<>();
            }
            if (!customNameNode.isVirtual()) {
                customName = Optional.of(customNameNode.getString());
            } else {
                customName = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public Drop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                Optional<String> customName) {
        super(id);
        this.price = price;
        this.sellCurrency = sellCurrency;
        this.level = level;
        this.dropItem = dropItem;
        this.fakeLevel = fakeLevel;
        this.permissionLevels = permissionLevels;
        this.permissionFakeLevels = permissionFakeLevels;
        this.customName = customName;
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

    public int getLevel() {
        return level;
    }

    public Optional<ItemStack> getDropItem() {
        return dropItem;
    }

    public Optional<Integer> getFakeLevel() {
        return fakeLevel;
    }

    public Map<String, Integer> getPermissionLevels() {
        return permissionLevels;
    }

    public Map<String, Integer> getPermissionFakeLevels() {
        return permissionFakeLevels;
    }

    public Optional<String> getCustomName() {
        return customName;
    }
}
