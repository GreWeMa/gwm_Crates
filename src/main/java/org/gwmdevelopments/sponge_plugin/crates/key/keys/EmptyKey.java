package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class EmptyKey extends AbstractKey {

    public EmptyKey(ConfigurationNode node) {
        super(node);
    }

    public EmptyKey(Optional<String> id) {
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
