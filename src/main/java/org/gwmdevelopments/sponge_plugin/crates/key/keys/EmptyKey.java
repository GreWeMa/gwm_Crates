package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class EmptyKey extends Key {

    public EmptyKey(ConfigurationNode node) {
        super(node);
    }

    public EmptyKey(Optional<String> id) {
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
