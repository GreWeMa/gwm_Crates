package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class PermissionOpenManager extends OpenManager {

    private String permission;
    private OpenManager openManager1;
    private OpenManager openManager2;

    public PermissionOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permissionNode = node.getNode("PERMISSION");
            ConfigurationNode openManager1Node = node.getNode("OPEN_MANAGER1");
            ConfigurationNode openManager2Node = node.getNode("OPEN_MANAGER2");
            if (permissionNode.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            if (openManager1Node.isVirtual()) {
                throw new RuntimeException("OPEN_MANAGER1 node does not exist!");
            }
            if (openManager2Node.isVirtual()) {
                throw new RuntimeException("OPEN_MANAGER2 node does not exist!");
            }
            permission = permissionNode.getString();
            openManager1 = (OpenManager) GWMCratesUtils.createSuperObject(openManager1Node, SuperObjectType.OPEN_MANAGER);
            openManager2 = (OpenManager) GWMCratesUtils.createSuperObject(openManager2Node, SuperObjectType.OPEN_MANAGER);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Permission Open Manager!", e);
        }
    }

    public PermissionOpenManager(Optional<String> id, Optional<SoundType> openSound,
                                 String permission, OpenManager openManager1, OpenManager openManager2) {
        super("PERMISSION", id, openSound);
        this.permission = permission;
        this.openManager1 = openManager1;
        this.openManager2 = openManager2;
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

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public OpenManager getOpenManager1() {
        return openManager1;
    }

    public void setOpenManager1(OpenManager openManager1) {
        this.openManager1 = openManager1;
    }

    public OpenManager getOpenManager2() {
        return openManager2;
    }

    public void setOpenManager2(OpenManager openManager2) {
        this.openManager2 = openManager2;
    }
}
