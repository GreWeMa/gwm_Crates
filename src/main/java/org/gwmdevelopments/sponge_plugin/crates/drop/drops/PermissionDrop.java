package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class PermissionDrop extends Drop {

    private String permission;
    private Drop drop1;
    private Drop drop2;

    public PermissionDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            ConfigurationNode drop1Node = node.getNode("DROP1");
            ConfigurationNode drop2Node = node.getNode("DROP2");
            if (permissionNode.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            if (drop1Node.isVirtual()) {
                throw new RuntimeException("DROP1 node does not exist!");
            }
            if (drop2Node.isVirtual()) {
                throw new RuntimeException("DROP2 node does not exist!");
            }
            permission = permissionNode.getString();
            drop1 = (Drop) GWMCratesUtils.createSuperObject(drop1Node, SuperObjectType.DROP);
            drop2 = (Drop) GWMCratesUtils.createSuperObject(drop2Node, SuperObjectType.DROP);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Permission Drop!", e);
        }
    }

    public PermissionDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                          int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                          Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                          String permission, Drop drop1, Drop drop2) {
        super("PERMISSION", id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
        this.permission = permission;
        this.drop1 = drop1;
        this.drop2 = drop2;
    }

    @Override
    public void apply(Player player) {
        if (player.hasPermission(permission)) {
            drop1.apply(player);
        } else {
            drop2.apply(player);
        }
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Drop getDrop1() {
        return drop1;
    }

    public void setDrop1(Drop drop1) {
        this.drop1 = drop1;
    }

    public Drop getDrop2() {
        return drop2;
    }

    public void setDrop2(Drop drop2) {
        this.drop2 = drop2;
    }
}
