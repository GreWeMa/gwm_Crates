package dev.gwm.spongeplugin.crates.superobject.openmanager;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.Set;

public final class PermissionOpenManager extends AbstractOpenManager {

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
            openManager1 = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                    create(GWMCratesSuperObjectCategories.OPEN_MANAGER, openManager1Node);
            openManager2 = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                    create(GWMCratesSuperObjectCategories.OPEN_MANAGER, openManager2Node);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public PermissionOpenManager(String id, Optional<SoundType> openSound,
                                 String permission, OpenManager openManager1, OpenManager openManager2) {
        super(id, openSound);
        this.permission = permission;
        this.openManager1 = openManager1;
        this.openManager2 = openManager2;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(openManager1);
        set.add(openManager2);
        return set;
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
