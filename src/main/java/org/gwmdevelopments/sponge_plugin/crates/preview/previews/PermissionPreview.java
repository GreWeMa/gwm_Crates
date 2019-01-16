package org.gwmdevelopments.sponge_plugin.crates.preview.previews;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.preview.AbstractPreview;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class PermissionPreview extends AbstractPreview {

    private String permission;
    private Preview preview1;
    private Preview preview2;

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
            throw new SSOCreationException("Failed to create Permission Preview!", e);
        }
    }

    public PermissionPreview(Optional<String> id,
                             String permission, Preview preview1, Preview preview2) {
        super("PERMISSION", id);
        this.permission = permission;
        this.preview1 = preview1;
        this.preview2 = preview2;
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

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Preview getPreview1() {
        return preview1;
    }

    public void setPreview1(Preview preview1) {
        this.preview1 = preview1;
    }

    public Preview getPreview2() {
        return preview2;
    }

    public void setPreview2(Preview preview2) {
        this.preview2 = preview2;
    }
}
