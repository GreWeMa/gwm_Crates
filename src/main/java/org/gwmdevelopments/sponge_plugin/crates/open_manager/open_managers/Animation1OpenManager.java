package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.listener.Animation1Listener;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.AbstractOpenManager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class Animation1OpenManager extends AbstractOpenManager {

    public static Map<Player, Information> PLAYERS_OPENING_ANIMATION1 = new HashMap<>();

    public static final BlockType DEFAULT_FLOOR_BLOCK_TYPE = BlockTypes.NETHER_BRICK;
    public static final BlockType DEFAULT_FENCE_BLOCK_TYPE = BlockTypes.NETHER_BRICK_FENCE;
    public static final BlockType DEFAULT_CRATE_BLOCK_TYPE = BlockTypes.ENDER_CHEST;

    private BlockType floorBlockType = DEFAULT_FLOOR_BLOCK_TYPE;
    private BlockType fenceBlockType = DEFAULT_FENCE_BLOCK_TYPE;
    private BlockType crateBlockType = DEFAULT_CRATE_BLOCK_TYPE;
    private OpenManager openManager = new NoGuiOpenManager(Optional.empty(), Optional.empty());
    private Optional<List<Text>> hologram = Optional.empty();
    private long closeDelay;

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
            }
            if (!fenceBlockTypeNode.isVirtual()) {
                fenceBlockType = fenceBlockTypeNode.getValue(TypeToken.of(BlockType.class));
            }
            if (!crateBlockTypeNode.isVirtual()) {
                crateBlockType = crateBlockTypeNode.getValue(TypeToken.of(BlockType.class));
            }
            if (!openManagerNode.isVirtual()) {
                openManager = (OpenManager) GWMCratesUtils.createSuperObject(openManagerNode, SuperObjectType.OPEN_MANAGER);
            }
            closeDelay = closeDelayNode.getInt(0);
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(hologramNode.getList(TypeToken.of(String.class)).
                        stream().
                        map(TextSerializers.FORMATTING_CODE::deserialize).
                        collect(Collectors.toList()));
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().info("Failed to create Animation1 Open Manager!");
        }
    }

    public Animation1OpenManager(Optional<String> id, Optional<SoundType> openSound, BlockType floorBlockType,
                                 BlockType fenceBlockType, BlockType crateBlockType, OpenManager openManager,
                                 Optional<List<Text>> hologram, int closeDelay) {
        super("ANIMATION1", id, openSound);
        this.floorBlockType = floorBlockType;
        this.fenceBlockType = fenceBlockType;
        this.crateBlockType = crateBlockType;
        this.openManager = openManager;
        this.hologram = hologram;
        this.closeDelay = closeDelay;
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
        GWMLibraryUtils.tryCreateHolograms(location1, hologram,
                GWMCrates.getInstance().getHologramOffset(),
                GWMCrates.getInstance().getMultilineHologramsDistance()).ifPresent(holograms::addAll);
        GWMLibraryUtils.tryCreateHolograms(location2, hologram,
                GWMCrates.getInstance().getHologramOffset(),
                GWMCrates.getInstance().getMultilineHologramsDistance()).
                ifPresent(holograms::addAll);
        GWMLibraryUtils.tryCreateHolograms(location3, hologram,
                GWMCrates.getInstance().getHologramOffset(),
                GWMCrates.getInstance().getMultilineHologramsDistance()).
                ifPresent(holograms::addAll);
        GWMLibraryUtils.tryCreateHolograms(location4, hologram,
                GWMCrates.getInstance().getHologramOffset(),
                GWMCrates.getInstance().getMultilineHologramsDistance()).
                ifPresent(holograms::addAll);
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

    public void setFloorBlockType(BlockType floorBlockType) {
        this.floorBlockType = floorBlockType;
    }

    public BlockType getFenceBlockType() {
        return fenceBlockType;
    }

    public void setFenceBlockType(BlockType fenceBlockType) {
        this.fenceBlockType = fenceBlockType;
    }

    public BlockType getCrateBlockType() {
        return crateBlockType;
    }

    public void setCrateBlockType(BlockType crateBlockType) {
        this.crateBlockType = crateBlockType;
    }

    public OpenManager getOpenManager() {
        return openManager;
    }

    public void setOpenManager(OpenManager openManager) {
        this.openManager = openManager;
    }

    public Optional<List<Text>> getHologram() {
        return hologram;
    }

    public void setHologram(Optional<List<Text>> hologram) {
        this.hologram = hologram;
    }

    public long getCloseDelay() {
        return closeDelay;
    }

    public void setCloseDelay(long closeDelay) {
        this.closeDelay = closeDelay;
    }

    public static class Information {

        private Animation1OpenManager openManager;
        private Manager manager;
        private Map<Location<World>, Boolean> locations;
        private Map<Location<World>, BlockState> originalBlockStates;
        private Set<HologramsService.Hologram> holograms;

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

        public void setOpenManager(Animation1OpenManager openManager) {
            this.openManager = openManager;
        }

        public Manager getManager() {
            return manager;
        }

        public void setManager(Manager manager) {
            this.manager = manager;
        }

        public Map<Location<World>, Boolean> getLocations() {
            return locations;
        }

        public void setLocations(Map<Location<World>, Boolean> locations) {
            this.locations = locations;
        }

        public Map<Location<World>, BlockState> getOriginalBlockStates() {
            return originalBlockStates;
        }

        public void setOriginalBlockStates(Map<Location<World>, BlockState> originalBlockStates) {
            this.originalBlockStates = originalBlockStates;
        }

        public Set<HologramsService.Hologram> getHolograms() {
            return holograms;
        }

        public void setHolograms(Set<HologramsService.Hologram> holograms) {
            this.holograms = holograms;
        }
    }
}
