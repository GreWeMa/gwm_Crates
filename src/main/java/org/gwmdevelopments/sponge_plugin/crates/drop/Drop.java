package org.gwmdevelopments.sponge_plugin.crates.drop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.GiveableSuperObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Drop extends GiveableSuperObject {

    private int level;
    private Optional<ItemStack> dropItem = Optional.empty();
    private Optional<Integer> fakeLevel = Optional.empty();
    private Map<String, Integer> permissionLevels = new HashMap<String, Integer>();
    private Map<String, Integer> permissionFakeLevels = new HashMap<String, Integer>();

    public Drop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode levelNode = node.getNode("LEVEL");
            ConfigurationNode dropItemNode = node.getNode("DROP_ITEM");
            ConfigurationNode fakeLevelNode = node.getNode("FAKE_LEVEL");
            ConfigurationNode permissionLevelsNode = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permissionFakeLevelsNode = node.getNode("PERMISSION_FAKE_LEVELS");
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
            throw new RuntimeException("Failed to create Drop!", e);
        }
    }

    public Drop(String type, Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels) {
        super(type, id, price, sellCurrency);
        this.level = level;
        this.dropItem = dropItem;
        this.fakeLevel = fakeLevel;
        this.permissionLevels = permissionLevels;
        this.permissionFakeLevels = permissionFakeLevels;
    }

    public abstract void apply(Player player);

    @Override
    public void give(Player player, int amount) {
        for (int i = 0 ; i < amount; i++) {
            apply(player);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Optional<ItemStack> getDropItem() {
        return dropItem;
    }

    public void setDropItem(Optional<ItemStack> dropItem) {
        this.dropItem = dropItem;
    }

    public Optional<Integer> getFakeLevel() {
        return fakeLevel;
    }

    public void setFakeLevel(Optional<Integer> fakeLevel) {
        this.fakeLevel = fakeLevel;
    }

    public Map<String, Integer> getPermissionLevels() {
        return permissionLevels;
    }

    public void setPermissionLevels(Map<String, Integer> permissionLevels) {
        this.permissionLevels = permissionLevels;
    }

    public Map<String, Integer> getPermissionFakeLevels() {
        return permissionFakeLevels;
    }

    public void setPermissionFakeLevels(Map<String, Integer> permissionFakeLevels) {
        this.permissionFakeLevels = permissionFakeLevels;
    }
}
