package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class PermissionKey extends AbstractKey {

    private String permission;

    public PermissionKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            if (permissionNode.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            permission = permissionNode.getString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Permission Key!", e);
        }
    }

    public PermissionKey(String type, Optional<String> id,
                         String permission) {
        super(type, id, true);
        this.permission = permission;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return player.hasPermission(permission) ? 1 : 0;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
