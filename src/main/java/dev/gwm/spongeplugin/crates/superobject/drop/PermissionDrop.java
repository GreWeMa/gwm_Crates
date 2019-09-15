package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.DefaultRandomableData;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public final class PermissionDrop extends AbstractDrop {

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
            drop1 = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                    create(GWMCratesSuperObjectCategories.DROP, drop1Node);
            drop2 = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                    create(GWMCratesSuperObjectCategories.DROP, drop2Node);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public PermissionDrop(Optional<String> id,
                          GiveableData giveableData,
                          Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                          DefaultRandomableData defaultRandomableData,
                          String permission, Drop drop1, Drop drop2) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        this.permission = permission;
        this.drop1 = drop1;
        this.drop2 = drop2;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(drop1);
        set.add(drop2);
        return set;
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
