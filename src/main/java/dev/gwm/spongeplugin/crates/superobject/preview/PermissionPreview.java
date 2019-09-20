package dev.gwm.spongeplugin.crates.superobject.preview;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.AbstractPreview;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class PermissionPreview extends AbstractPreview {

    public static final String TYPE = "PREVIEW";

    private final String permission;
    private final Preview preview1;
    private final Preview preview2;

    public PermissionPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            ConfigurationNode preview1Node = node.getNode("PREVIEW1");
            ConfigurationNode preview2Node = node.getNode("PREVIEW2");
            if (permissionNode.isVirtual()) {
                throw new IllegalArgumentException("PERMISSION node does not exist!");
            }
            if (preview1Node.isVirtual()) {
                throw new IllegalArgumentException("PREVIEW1 node does not exist!");
            }
            if (preview2Node.isVirtual()) {
                throw new IllegalArgumentException("PREVIEW2 node does not exist!");
            }
            permission = permissionNode.getString();
            preview1 = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                    create(GWMCratesSuperObjectCategories.PREVIEW, preview1Node);
            preview2 = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                    create(GWMCratesSuperObjectCategories.PREVIEW, preview2Node);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public PermissionPreview(String id, Optional<List<Drop>> customDrops,
                             String permission, Preview preview1, Preview preview2) {
        super(id, customDrops);
        this.permission = permission;
        this.preview1 = preview1;
        this.preview2 = preview2;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(preview1);
        set.add(preview2);
        return set;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void preview(Player player, Manager manager) {
        if (player.hasPermission(permission)) {
            preview1.preview(player, manager);
        } else {
            preview2.preview(player, manager);
        }
    }

    public String getPermission() {
        return permission;
    }

    public Preview getPreview1() {
        return preview1;
    }

    public Preview getPreview2() {
        return preview2;
    }
}
