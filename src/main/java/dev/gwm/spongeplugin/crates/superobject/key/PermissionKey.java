package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class PermissionKey extends AbstractKey {

    public static final String TYPE = "PERMISSION";

    private final String permission;

    public PermissionKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            if (permissionNode.isVirtual()) {
                throw new IllegalArgumentException("PERMISSION node does not exist!");
            }
            permission = permissionNode.getString();
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public PermissionKey(Optional<String> id,
                         String permission) {
        super(id, true);
        this.permission = permission;
    }

    @Override
    public String type() {
        return TYPE;
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
}
