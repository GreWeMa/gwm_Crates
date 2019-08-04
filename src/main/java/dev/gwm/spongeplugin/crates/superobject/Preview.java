package dev.gwm.spongeplugin.crates.preview;

import dev.gwm.spongeplugin.crates.superobject.Drop;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.manager.Manager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.SuperObject;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Preview extends SuperObject {

    private final Optional<List<Drop>> customDrops;

    public Preview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode customDropsNode = node.getNode("CUSTOM_DROPS");
            if (!customDropsNode.isVirtual()) {
                List<Drop> tempCustomDrops = new ArrayList<>();
                for (ConfigurationNode customDropNode : customDropsNode.getChildrenList()) {
                    tempCustomDrops.add((Drop) GWMCratesUtils.createSuperObject(customDropNode, SuperObjectType.DROP));
                }
                customDrops = Optional.of(Collections.unmodifiableList(tempCustomDrops));
            } else {
                customDrops = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public Preview(Optional<String> id, Optional<List<Drop>> customDrops) {
        super(id);
        this.customDrops = customDrops;
    }

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.PREVIEW;
    }

    public abstract void preview(Player player, Manager manager);

    public Optional<List<Drop>> getCustomDrops() {
        return customDrops;
    }
}
