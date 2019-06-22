package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.listener.Animation1Listener;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.HologramSettings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public final class Animation1OpenManager extends OpenManager {

    public static final String TYPE = "ANIMATION1";

    public static final Map<Player, Information> PLAYERS_OPENING_ANIMATION1 = new HashMap<>();

    public static final BlockType DEFAULT_FLOOR_BLOCK_TYPE = BlockTypes.NETHER_BRICK;
    public static final BlockType DEFAULT_FENCE_BLOCK_TYPE = BlockTypes.NETHER_BRICK_FENCE;
    public static final BlockType DEFAULT_CRATE_BLOCK_TYPE = BlockTypes.ENDER_CHEST;

    private final BlockType floorBlockType;
    private final BlockType fenceBlockType;
    private final BlockType crateBlockType;
    private final OpenManager openManager;
    private final Optional<HologramSettings> hologram;
    private final long closeDelay;

    public Animation1OpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode floorBlockTypeNode = node.getNode("FLOOR_BLOCK_TYPE");
            ConfigurationNode fenceBlockTypeNode = node.getNode("FENCE_BLOCK_TYPE");
            ConfigurationNode crateBlockTypeNode = node.getNode("CRATE_BLOCK_TYPE");
            ConfigurationNode openManagerNode = node.getNode("OPEN_MANAGER");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            if (!floorBlockTypeNode.isVirtual()) {
                floorBlockType = floorBlockTypeNode.getValue(TypeToken.of(BlockType.class));
            } else {
                floorBlockType = DEFAULT_FLOOR_BLOCK_TYPE;
            }
            if (!fenceBlockTypeNode.isVirtual()) {
                fenceBlockType = fenceBlockTypeNode.getValue(TypeToken.of(BlockType.class));
            } else {
                fenceBlockType = DEFAULT_FENCE_BLOCK_TYPE;
            }
            if (!crateBlockTypeNode.isVirtual()) {
                crateBlockType = crateBlockTypeNode.getValue(TypeToken.of(BlockType.class));
            } else {
                crateBlockType = DEFAULT_CRATE_BLOCK_TYPE;
            }
            if (!openManagerNode.isVirtual()) {
                openManager = (OpenManager) GWMCratesUtils.createSuperObject(openManagerNode, SuperObjectType.OPEN_MANAGER);
            } else {
                openManager = new NoGuiOpenManager(Optional.empty(), Optional.empty());
            }
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(GWMLibraryUtils.parseHologramSettings(hologramNode,
                        GWMCrates.getInstance().getHologramOffset(),
                        GWMCrates.getInstance().getMultilineHologramsDistance()));
            } else {
                hologram = Optional.empty();
            }
            closeDelay = closeDelayNode.getInt(0);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public Animation1OpenManager(Optional<String> id, Optional<SoundType> openSound, BlockType floorBlockType,
                                 BlockType fenceBlockType, BlockType crateBlockType, OpenManager openManager,
                                 Optional<HologramSettings> hologram, int closeDelay) {
        super(id, openSound);
        this.floorBlockType = floorBlockType;
        this.fenceBlockType = fenceBlockType;
        this.crateBlockType = crateBlockType;
        this.openManager = openManager;
        this.hologram = hologram;
        this.closeDelay = closeDelay;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent openEvent = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Location<World> location = player.getLocation();
        Vector3i position = location.getBlockPosition();
        World world = location.getExtent();
        int positionX = position.getX();
        int positionY = position.getY();
        int positionZ = position.getZ();
        HashMap<Location<World>, BlockState> originalBlockStates = new HashMap<>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location<World> targetLocation = new Location<>(
                            world, positionX + x, positionY + y, positionZ + z);
                    BlockState blockState = targetLocation.getBlock();
                    originalBlockStates.put(targetLocation, blockState);
                    location.setBlockType(BlockTypes.AIR, BlockChangeFlags.NONE);
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                new Location<>(world, positionX + x, positionY - 1, positionZ + z).
                        setBlockType(floorBlockType, BlockChangeFlags.NONE);
                if (z == 2 || z == -2 || x == 2 || x == -2) {
                    new Location<>(world, positionX + x, positionY , positionZ + z).
                            setBlockType(fenceBlockType, BlockChangeFlags.NONE);
                }
            }
        }
        HashSet<HologramsService.Hologram> holograms = new HashSet<>();
        Location<World> location1 = new Location<>(world, positionX + 2, positionY, positionZ);
        location1.setBlock(BlockState.builder().
                        blockType(crateBlockType).
                        add(Keys.DIRECTION, Direction.WEST).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location2 = new Location<>(world, positionX - 2, positionY, positionZ);
        location2.setBlock(BlockState.builder().
                        blockType(crateBlockType).
                        add(Keys.DIRECTION, Direction.EAST).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location3 = new Location<>(world, positionX, positionY, positionZ + 2);
        location3.setBlock(BlockState.builder().
                        blockType(crateBlockType).
                        add(Keys.DIRECTION, Direction.NORTH).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location4 = new Location<>(world, positionX, positionY, positionZ - 2);
        location4.setBlock(BlockState.builder().
                        blockType(crateBlockType).
                        add(Keys.DIRECTION, Direction.SOUTH).
                        build(),
                BlockChangeFlags.NONE);
        hologram.ifPresent(hg -> {
            GWMLibraryUtils.createHologram(location1, hg).ifPresent(holograms::addAll);
            GWMLibraryUtils.createHologram(location2, hg).ifPresent(holograms::addAll);
            GWMLibraryUtils.createHologram(location3, hg).ifPresent(holograms::addAll);
            GWMLibraryUtils.createHologram(location4, hg).ifPresent(holograms::addAll);
        });
        getOpenSound().ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
        PLAYERS_OPENING_ANIMATION1.put(player, new Information(this, manager,
                new HashMap<Location<World>, Boolean>(){{
                    put(location1, false);
                    put(location2, false);
                    put(location3, false);
                    put(location4, false);
                }}, originalBlockStates, holograms));
    }

    @Override
    public boolean canOpen(Player player, Manager manager) {
        return !PLAYERS_OPENING_ANIMATION1.containsKey(player) &&
                !Animation1Listener.OPENED_PLAYERS.containsKey(player) &&
                !containsNearPlayers(player);
    }

    @Override
    public void shutdown() {
        PLAYERS_OPENING_ANIMATION1.values().stream().
                filter(info -> info.getOpenManager() == this).
                flatMap(info -> info.getHolograms().stream()).
                forEach(HologramsService.Hologram::remove);
    }

    private boolean containsNearPlayers(Player player) {
        for (Player p : PLAYERS_OPENING_ANIMATION1.keySet()) {
            if (p.getLocation().getPosition().distance(player.getLocation().getPosition()) < 5) {
                return true;
            }
        }
        for (Player p : Animation1Listener.OPENED_PLAYERS.keySet()) {
            if (p.getLocation().getPosition().distance(player.getLocation().getPosition()) < 5) {
                return true;
            }
        }
        return false;
    }

    public BlockType getFloorBlockType() {
        return floorBlockType;
    }

    public BlockType getFenceBlockType() {
        return fenceBlockType;
    }

    public BlockType getCrateBlockType() {
        return crateBlockType;
    }

    public OpenManager getOpenManager() {
        return openManager;
    }

    public Optional<HologramSettings> getHologram() {
        return hologram;
    }

    public long getCloseDelay() {
        return closeDelay;
    }

    public static class Information {

        private final Animation1OpenManager openManager;
        private final Manager manager;
        private final Map<Location<World>, Boolean> locations;
        private final Map<Location<World>, BlockState> originalBlockStates;
        private final Set<HologramsService.Hologram> holograms;

        public Information(Animation1OpenManager openManager, Manager manager,
                           Map<Location<World>, Boolean> locations, Map<Location<World>, BlockState> originalBlockStates,
                           Set<HologramsService.Hologram> holograms) {
            this.openManager = openManager;
            this.manager = manager;
            this.locations = locations;
            this.originalBlockStates = originalBlockStates;
            this.holograms = holograms;
        }

        public Animation1OpenManager getOpenManager() {
            return openManager;
        }

        public Manager getManager() {
            return manager;
        }

        public Map<Location<World>, Boolean> getLocations() {
            return locations;
        }

        public Map<Location<World>, BlockState> getOriginalBlockStates() {
            return originalBlockStates;
        }

        public Set<HologramsService.Hologram> getHolograms() {
            return holograms;
        }
    }
}
