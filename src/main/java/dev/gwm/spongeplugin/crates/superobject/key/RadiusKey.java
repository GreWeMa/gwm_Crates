package dev.gwm.spongeplugin.crates.superobject.keys;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class RadiusKey extends Key {

    public static final String TYPE = "RADIUS";

    private final double radius;
    private final Vector3d center;
    private final Optional<World> world;

    public RadiusKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode radiusNode = node.getNode("RADIUS");
            ConfigurationNode centerNode = node.getNode("CENTER");
            ConfigurationNode worldNode = node.getNode("WORLD");
            if (radiusNode.isVirtual()) {
                throw new IllegalArgumentException("RADIUS node does not exist!");
            }
            if (centerNode.isVirtual()) {
                throw new IllegalArgumentException("CENTER node does not exist!");
            }
            radius = radiusNode.getDouble();
            center = GWMLibraryUtils.parseVector3d(centerNode);
            if (!worldNode.isVirtual()) {
                String worldName = worldNode.getString();
                world = Sponge.getServer().getWorld(worldName);
                if (!world.isPresent()) {
                    throw new IllegalArgumentException("WORLD \"" + worldNode + "\" does not exist!");
                }
            } else {
                world = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public RadiusKey(Optional<String> id, boolean doNotWithdraw,
                     double radius, Vector3d center, Optional<World> world) {
        super(id, doNotWithdraw);
        this.radius = radius;
        this.center = center;
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
        Location<World> location = player.getLocation();
        if (world.isPresent() && !location.getExtent().equals(world.get())) {
            return 0;
        }
        return location.getPosition().distance(center) <= radius ? 1 : 0;
    }
}
