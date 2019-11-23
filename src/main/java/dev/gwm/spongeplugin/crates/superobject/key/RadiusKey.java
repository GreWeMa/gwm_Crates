package dev.gwm.spongeplugin.crates.superobject.key;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class RadiusKey extends AbstractKey {

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
            //The radius is allowed to be zero, in case the users wants a players to be in some exact location.
            if (radius < 0) {
                throw new IllegalArgumentException("Radius is less than 0!");
            }
            center = GWMLibraryUtils.parseVector3d(centerNode);
            if (!worldNode.isVirtual()) {
                String worldName = worldNode.getString();
                world = Sponge.getServer().getWorld(worldName);
                if (!world.isPresent()) {
                    throw new IllegalArgumentException("World \"" + worldNode + "\" is not found!");
                }
            } else {
                world = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public RadiusKey(String id, boolean doNotWithdraw,
                     double radius, Vector3d center, Optional<World> world) {
        super(id, doNotWithdraw);
        //The radius is allowed to be zero, in case the users wants a players to be in some exact location.
        if (radius < 0) {
            throw new IllegalArgumentException("Radius is less than 0!");
        }
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
