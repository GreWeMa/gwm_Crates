package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

public final class EmptyKey extends AbstractKey {

    public static final String TYPE = "EMPTY";

    public EmptyKey(ConfigurationNode node) {
        super(node);
    }

    public EmptyKey(String id) {
        super(id, true);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        return 1;
    }
}
