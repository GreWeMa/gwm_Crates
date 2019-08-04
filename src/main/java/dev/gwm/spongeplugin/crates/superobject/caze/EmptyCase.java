package dev.gwm.spongeplugin.crates.caze;

import dev.gwm.spongeplugin.crates.superobject.Case;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class EmptyCase extends Case {

    public static final String TYPE = "EMPTY";

    public EmptyCase(ConfigurationNode node) {
        super(node);
    }

    public EmptyCase(Optional<String> id) {
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
