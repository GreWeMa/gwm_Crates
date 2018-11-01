package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.AbstractCase;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class EmptyCase extends AbstractCase {

    public EmptyCase(ConfigurationNode node) {
        super(node);
    }

    public EmptyCase(Optional<String> id) {
        super("EMPTY", id);
    }

    @Override
    public void withdraw(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }
}
