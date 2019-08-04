package dev.gwm.spongeplugin.crates.drop.drops;

import dev.gwm.spongeplugin.crates.drop.Drop;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public final class PermissionDrop extends Drop {

    public static final String TYPE = "PERMISSION";

    private final String permission;
    private final Drop drop1;
    private final Drop drop2;

    public PermissionDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            ConfigurationNode drop1Node = node.getNode("DROP1");
            ConfigurationNode drop2Node = node.getNode("DROP2");
            if (permissionNode.isVirtual()) {
                throw new IllegalArgumentException("PERMISSION node does not exist!");
            }
            if (drop1Node.isVirtual()) {
                throw new IllegalArgumentException("DROP1 node does not exist!");
            }
            if (drop2Node.isVirtual()) {
                throw new IllegalArgumentException("DROP2 node does not exist!");
            }
            permission = permissionNode.getString();
            drop1 = (Drop) GWMCratesUtils.createSuperObject(drop1Node, SuperObjectType.DROP);
            drop2 = (Drop) GWMCratesUtils.createSuperObject(drop2Node, SuperObjectType.DROP);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public PermissionDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview, Optional<Integer> level, Optional<Integer> fakeLevel, Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels, Optional<Long> weight, Optional<Long> fakeWeight, Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights,
                          String permission, Drop drop1, Drop drop2) {
        super(id, price, sellCurrency, dropItem, customName, showInPreview, level, fakeLevel, permissionLevels, permissionFakeLevels, weight, fakeWeight, permissionWeights, permissionFakeWeights);
        this.permission = permission;
        this.drop1 = drop1;
        this.drop2 = drop2;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        if (player.hasPermission(permission)) {
            drop1.give(player, amount);
        } else {
            drop2.give(player, amount);
        }
    }

    public String getPermission() {
        return permission;
    }

    public Drop getDrop1() {
        return drop1;
    }

    public Drop getDrop2() {
        return drop2;
    }
}
