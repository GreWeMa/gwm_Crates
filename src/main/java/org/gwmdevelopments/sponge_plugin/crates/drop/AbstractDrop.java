package org.gwmdevelopments.sponge_plugin.crates.drop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractDrop extends AbstractSuperObject implements Drop {

    private Optional<BigDecimal> price = Optional.empty();
    private Optional<Currency> sellCurrency = Optional.empty();
    private int level;
    private Optional<ItemStack> dropItem = Optional.empty();
    private Optional<Integer> fakeLevel = Optional.empty();
    private Map<String, Integer> permissionLevels = new HashMap<>();
    private Map<String, Integer> permissionFakeLevels = new HashMap<>();

    public AbstractDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            ConfigurationNode levelNode = node.getNode("LEVEL");
            ConfigurationNode dropItemNode = node.getNode("DROP_ITEM");
            ConfigurationNode fakeLevelNode = node.getNode("FAKE_LEVEL");
            ConfigurationNode permissionLevelsNode = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permissionFakeLevelsNode = node.getNode("PERMISSION_FAKE_LEVELS");
            if (!priceNode.isVirtual()) {
                price = Optional.of(new BigDecimal(priceNode.getString()));
            }
            if (!sellCurrencyNode.isVirtual()) {
                String sellCurrencyName = sellCurrencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new RuntimeException("Economy Service not found, but parameter \"SELL_CURRENCY\" specified!");
                }
                EconomyService economyService = optionalEconomyService.get();
                boolean found = false;
                for (Currency currency : economyService.getCurrencies()) {
                    if (currency.getId().equals(sellCurrencyName)) {
                        sellCurrency = Optional.of(currency);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new RuntimeException("Currency \"" + sellCurrencyName + "\" not found!");
                }
            }
            level = levelNode.getInt(1);
            if (level < 1) {
                GWMCrates.getInstance().getLogger().info("LEVEL value is less than 1! Force set it to 1!");
                level = 1;
            }
            if (!dropItemNode.isVirtual()) {
                dropItem = Optional.of(GWMCratesUtils.parseItem(dropItemNode));
            }
            if (!fakeLevelNode.isVirtual()) {
                fakeLevel = Optional.of(fakeLevelNode.getInt(1));
            }
            if (!permissionLevelsNode.isVirtual()) {
                permissionLevels = permissionLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
            }
            if (!permissionFakeLevelsNode.isVirtual()) {
                permissionFakeLevels = permissionFakeLevelsNode.getValue(new TypeToken<Map<String, Integer>>(){});
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Abstract Drop!", e);
        }
    }

    public AbstractDrop(String type, Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                        int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                        Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels) {
        super(type, id);
        this.price = price;
        this.sellCurrency = sellCurrency;
        this.level = level;
        this.dropItem = dropItem;
        this.fakeLevel = fakeLevel;
        this.permissionLevels = permissionLevels;
        this.permissionFakeLevels = permissionFakeLevels;
    }

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }

    @Override
    public Optional<Currency> getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(Optional<Currency> sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public Optional<ItemStack> getDropItem() {
        return dropItem;
    }

    public void setDropItem(Optional<ItemStack> dropItem) {
        this.dropItem = dropItem;
    }

    @Override
    public Optional<Integer> getFakeLevel() {
        return fakeLevel;
    }

    public void setFakeLevel(Optional<Integer> fakeLevel) {
        this.fakeLevel = fakeLevel;
    }

    @Override
    public Map<String, Integer> getPermissionLevels() {
        return permissionLevels;
    }

    public void setPermissionLevels(Map<String, Integer> permissionLevels) {
        this.permissionLevels = permissionLevels;
    }

    @Override
    public Map<String, Integer> getPermissionFakeLevels() {
        return permissionFakeLevels;
    }

    public void setPermissionFakeLevels(Map<String, Integer> permissionFakeLevels) {
        this.permissionFakeLevels = permissionFakeLevels;
    }
}
