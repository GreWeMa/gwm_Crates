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
        super("EMPTY", id, true);
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return 1;
    }
}
