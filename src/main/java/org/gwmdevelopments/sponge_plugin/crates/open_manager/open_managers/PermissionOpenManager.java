package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class PermissionOpenManager extends OpenManager {

    public static final String TYPE = "PERMISSION";

    private final String permission;
    private final OpenManager openManager1;
    private final OpenManager openManager2;

    public PermissionOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            ConfigurationNode openManager1Node = node.getNode("OPEN_MANAGER1");
            ConfigurationNode openManager2Node = node.getNode("OPEN_MANAGER2");
            if (permissionNode.isVirtual()) {
                throw new IllegalArgumentException("PERMISSION node does not exist!");
            }
            if (openManager1Node.isVirtual()) {
                throw new IllegalArgumentException("OPEN_MANAGER1 node does not exist!");
            }
            if (openManager2Node.isVirtual()) {
                throw new IllegalArgumentException("OPEN_MANAGER2 node does not exist!");
            }
            permission = permissionNode.getString();
            openManager1 = (OpenManager) GWMCratesUtils.createSuperObject(openManager1Node, SuperObjectType.OPEN_MANAGER);
            openManager2 = (OpenManager) GWMCratesUtils.createSuperObject(openManager2Node, SuperObjectType.OPEN_MANAGER);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public PermissionOpenManager(Optional<String> id, Optional<SoundType> openSound,
                                 String permission, OpenManager openManager1, OpenManager openManager2) {
        super(id, openSound);
        this.permission = permission;
        this.openManager1 = openManager1;
        this.openManager2 = openManager2;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void open(Player player, Manager manager) {
        if (player.hasPermission(permission)) {
            openManager1.open(player, manager);
        } else {
            openManager2.open(player, manager);
        }
    }

    public String getPermission() {
        return permission;
    }

    public OpenManager getOpenManager1() {
        return openManager1;
    }

    public OpenManager getOpenManager2() {
        return openManager2;
    }
}
