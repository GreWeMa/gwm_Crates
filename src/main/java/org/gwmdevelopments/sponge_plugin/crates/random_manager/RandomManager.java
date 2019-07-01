package org.gwmdevelopments.sponge_plugin.crates.random_manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public abstract class RandomManager extends SuperObject {

    public RandomManager(ConfigurationNode node) {
        super(node);
    }

    public RandomManager(Optional<String> id) {
        super(id);
    }

    public abstract Drop choose(Iterable<Drop> iterable, Player player, boolean fake);

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.RANDOM_MANAGER;
    }
}
