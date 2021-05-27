package dev.gwm.spongeplugin.crates.superobject.key;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class BoundariesKey extends AbstractKey {

    public static final String TYPE = "BOUNDARIES";

    private final AABB aabb;
    private final Optional<World> world;

    public BoundariesKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode firstCornerNode = node.getNode("FIRST_CORNER");
            ConfigurationNode secondCornerNode = node.getNode("SECOND_CORNER");
            ConfigurationNode worldNode = node.getNode("WORLD");
            if (firstCornerNode.isVirtual()) {
                throw new IllegalArgumentException("FIRST_CORNER node does not exist!");
            }
            if (secondCornerNode.isVirtual()) {
                throw new IllegalArgumentException("SECOND_CORNER node does not exist!");
            }
            Vector3d firstCorner = GWMLibraryUtils.parseVector3d(firstCornerNode);
            Vector3d secondCorner = GWMLibraryUtils.parseVector3d(secondCornerNode);
            aabb = new AABB(firstCorner, secondCorner);
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

    public BoundariesKey(String id, boolean doNotWithdraw,
                         AABB aabb, Optional<World> world) {
        super(id, doNotWithdraw);
        this.aabb = aabb;
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
        return aabb.contains(location.getPosition()) ? 1 : 0;
    }
}
