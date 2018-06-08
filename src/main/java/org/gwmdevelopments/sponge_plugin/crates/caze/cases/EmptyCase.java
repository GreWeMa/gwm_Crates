package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class EmptyCase extends Case {

    public EmptyCase(ConfigurationNode node) {
        super(node);
    }

    public EmptyCase(Optional<String> id) {
        super("EMPTY", id, Optional.empty(), Optional.empty());
    }

    @Override
    public void add(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }
}
