package dev.gwm.spongeplugin.crates.superobject.caze;

import dev.gwm.spongeplugin.crates.superobject.caze.base.AbstractCase;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

public final class EmptyCase extends AbstractCase {

    public static final String TYPE = "EMPTY";

    public EmptyCase(ConfigurationNode node) {
        super(node);
    }

    public EmptyCase(String id) {
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
