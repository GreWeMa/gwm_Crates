package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class WorldKey extends Key {

    public static final String TYPE = "WORLD";

    private final List<World> worlds;

    public WorldKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode worldsNode = node.getNode("WORLDS");
            List<World> tempWorlds = new ArrayList<>();
            for (ConfigurationNode worldNode : worldsNode.getChildrenList()) {
                tempWorlds.add(Sponge.getServer().getWorld(worldNode.getString()).
                        orElseThrow(() -> new IllegalArgumentException("WORLD \"" + worldNode + "\" does not exist!")));
            }
            if (tempWorlds.isEmpty()) {
                throw new IllegalArgumentException("No world are configured! At least one world is required!");
            }
            worlds = Collections.unmodifiableList(tempWorlds);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldKey(Optional<String> id, boolean doNotWithdraw,
                    List<World> worlds) {
        super(id, doNotWithdraw);
        this.worlds = worlds;
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
        return worlds.contains(player.getLocation().getExtent()) ? 1 : 0;
    }

    public List<World> getWorlds() {
        return worlds;
    }
}
