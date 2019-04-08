package org.gwmdevelopments.sponge_plugin.crates.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
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
