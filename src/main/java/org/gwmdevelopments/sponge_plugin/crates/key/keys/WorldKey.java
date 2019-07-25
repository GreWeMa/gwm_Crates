package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class WorldKey extends Key {

    public static final String TYPE = "WORLD";

    private final World world;

    public WorldKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode worldNode = node.getNode("WORLD");
            if (worldNode.isVirtual()) {
                throw new IllegalArgumentException("WORLD node does not exist!");
            }
            String worldName = worldNode.getString();
            world = Sponge.getServer().getWorld(worldName).
                    orElseThrow(() -> new IllegalArgumentException("WORLD \"" + worldNode + "\" does not exist!"));
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldKey(Optional<String> id, boolean doNotWithdraw,
                    World world) {
        super(id, doNotWithdraw);
        this.world = world;
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
        return player.getLocation().getExtent().equals(world) ? 1 : 0;
    }
}
