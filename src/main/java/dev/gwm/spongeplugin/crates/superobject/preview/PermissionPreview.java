package dev.gwm.spongeplugin.crates.superobject.previews;

import dev.gwm.spongeplugin.crates.superobject.Drop;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.Preview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;

public final class PermissionPreview extends Preview {

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
            preview1 = (Preview) GWMCratesUtils.createSuperObject(preview1Node, SuperObjectType.PREVIEW);
            preview2 = (Preview) GWMCratesUtils.createSuperObject(preview2Node, SuperObjectType.PREVIEW);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public PermissionPreview(Optional<String> id, Optional<List<Drop>> customDrops,
                             String permission, Preview preview1, Preview preview2) {
        super(id, customDrops);
        this.permission = permission;
        this.preview1 = preview1;
        this.preview2 = preview2;
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
