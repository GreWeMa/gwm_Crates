package dev.gwm.spongeplugin.crates.superobject.openmanager;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenCrateEvent;
import dev.gwm.spongeplugin.crates.listener.Animation1Listener;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.CreatedHologram;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.HologramSettings;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public final class Animation1OpenManager extends AbstractOpenManager {

    public static final String TYPE = "ANIMATION1";

    public static final Map<Player, Information> PLAYERS_OPENING_ANIMATION1 = new HashMap<>();

    public static final BlockState DEFAULT_FLOOR_BLOCK = BlockState.builder().
            blockType(BlockTypes.NETHER_BRICK).
            build();
    public static final BlockState DEFAULT_FENCE_BLOCK = BlockState.builder().
            blockType(BlockTypes.NETHER_BRICK_FENCE).
            build();
    public static final BlockState DEFAULT_CRATE_BLOCK = BlockState.builder().
            blockType(BlockTypes.ENDER_CHEST).
            build();

    private final BlockState floorBlock;
    private final BlockState fenceBlock;
    private final BlockState crateBlock;
    private final OpenManager openManager;
    private final Optional<HologramSettings> hologram;
    private final long closeDelay;

    public Animation1OpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode floorBlockTypeNode = node.getNode("FLOOR_BLOCK");
            ConfigurationNode fenceBlockTypeNode = node.getNode("FENCE_BLOCK");
            ConfigurationNode crateBlockTypeNode = node.getNode("CRATE_BLOCK");
            ConfigurationNode openManagerNode = node.getNode("OPEN_MANAGER");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            if (!floorBlockTypeNode.isVirtual()) {
                floorBlock = floorBlockTypeNode.getValue(TypeToken.of(BlockState.class));
            } else {
                floorBlock = DEFAULT_FLOOR_BLOCK;
            }
            if (!fenceBlockTypeNode.isVirtual()) {
                fenceBlock = fenceBlockTypeNode.getValue(TypeToken.of(BlockState.class));
            } else {
                fenceBlock = DEFAULT_FENCE_BLOCK;
            }
            if (!crateBlockTypeNode.isVirtual()) {
                crateBlock = crateBlockTypeNode.getValue(TypeToken.of(BlockState.class));
            } else {
                crateBlock = DEFAULT_CRATE_BLOCK;
            }
            if (!openManagerNode.isVirtual()) {
                openManager = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                        create(GWMCratesSuperObjectCategories.OPEN_MANAGER, openManagerNode);
            } else {
                openManager = GWMCratesUtils.DEFAULT_OPEN_MANAGER;
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
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public Animation1OpenManager(String id,
                                 Optional<SoundType> openSound,
                                 BlockState floorBlock, BlockState fenceBlock, BlockState crateBlock,
                                 OpenManager openManager, Optional<HologramSettings> hologram, int closeDelay) {
        super(id, openSound);
        this.floorBlock = floorBlock;
        this.fenceBlock = fenceBlock;
        this.crateBlock = crateBlock;
        this.openManager = openManager;
        this.hologram = hologram;
        this.closeDelay = closeDelay;
    }

    @Override
    public void shutdown() {
        PLAYERS_OPENING_ANIMATION1.values().stream().
                filter(info -> info.getOpenManager() == this).
                flatMap(info -> info.getHolograms().stream()).
                forEach(createdHologram -> createdHologram.getHolograms().
                        forEach(HologramsService.Hologram::remove));
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(openManager);
        return set;
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
                        setBlock(floorBlock, BlockChangeFlags.NONE);
                if (z == 2 || z == -2 || x == 2 || x == -2) {
                    new Location<>(world, positionX + x, positionY , positionZ + z).
                            setBlock(fenceBlock, BlockChangeFlags.NONE);
                }
            }
        }
        HashSet<CreatedHologram> holograms = new HashSet<>();
        Location<World> location1 = new Location<>(world, positionX + 2, positionY, positionZ);
        location1.setBlock(BlockState.builder().
                        from(crateBlock).
                        add(Keys.DIRECTION, Direction.WEST).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location2 = new Location<>(world, positionX - 2, positionY, positionZ);
        location2.setBlock(BlockState.builder().
                        from(crateBlock).
                        add(Keys.DIRECTION, Direction.EAST).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location3 = new Location<>(world, positionX, positionY, positionZ + 2);
        location3.setBlock(BlockState.builder().
                        from(crateBlock).
                        add(Keys.DIRECTION, Direction.NORTH).
                        build(),
                BlockChangeFlags.NONE);
        Location<World> location4 = new Location<>(world, positionX, positionY, positionZ - 2);
        location4.setBlock(BlockState.builder().
                        from(crateBlock).
                        add(Keys.DIRECTION, Direction.SOUTH).
                        build(),
                BlockChangeFlags.NONE);
        hologram.ifPresent(hg -> {
            holograms.add(GWMLibraryUtils.createHologram(location1, hg, false));
            holograms.add(GWMLibraryUtils.createHologram(location2, hg, false));
            holograms.add(GWMLibraryUtils.createHologram(location3, hg, false));
            holograms.add(GWMLibraryUtils.createHologram(location4, hg, false));
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

    public BlockState getFloorBlock() {
        return floorBlock;
    }

    public BlockState getFenceBlock() {
        return fenceBlock;
    }

    public BlockState getCrateBlock() {
        return crateBlock;
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
        private final Set<CreatedHologram> holograms;

        public Information(Animation1OpenManager openManager, Manager manager,
                           Map<Location<World>, Boolean> locations, Map<Location<World>, BlockState> originalBlockStates,
                           Set<CreatedHologram> holograms) {
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

        public Set<CreatedHologram> getHolograms() {
            return holograms;
        }
    }
}
