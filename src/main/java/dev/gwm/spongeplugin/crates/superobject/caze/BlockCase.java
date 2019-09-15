package dev.gwm.spongeplugin.crates.superobject.caze;

import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.caze.base.AbstractCase;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.CreatedHologram;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.HologramSettings;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class BlockCase extends AbstractCase {

    public static final String TYPE = "BLOCK";

    private final List<Location<World>> locations;
    private final boolean startPreviewOnLeftClick;
    private final Optional<HologramSettings> hologram;
    private Optional<List<CreatedHologram>> createdHolograms;

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode locationsNode = node.getNode("LOCATIONS");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            if (locationsNode.isVirtual()) {
                throw new IllegalArgumentException("LOCATIONS node does not exist!");
            }
            List<Location<World>> tempLocations = new ArrayList<>();
            if (!locationsNode.isVirtual()) {
                for (ConfigurationNode innerLocationNode : locationsNode.getChildrenList()) {
                    tempLocations.add(GWMLibraryUtils.parseLocation(innerLocationNode));
                }
            }
            if (tempLocations.isEmpty()) {
                throw new IllegalArgumentException("No Locations are configured! At least one Location is required!");
            }
            locations = Collections.unmodifiableList(tempLocations);
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(GWMLibraryUtils.parseHologramSettings(hologramNode,
                        GWMCrates.getInstance().getHologramOffset(),
                        GWMCrates.getInstance().getMultilineHologramsDistance()));
            } else {
                hologram = Optional.empty();
            }
            if (hologram.isPresent()) {
                HologramSettings hologramSettings = hologram.get();
                List<CreatedHologram> tempCreatedHolograms = new ArrayList<>();
                locations.forEach(loc -> tempCreatedHolograms.add(GWMLibraryUtils.createHologram(loc, hologramSettings, true)));
                createdHolograms = Optional.of(Collections.unmodifiableList(tempCreatedHolograms));
            } else {
                createdHolograms = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public BlockCase(Optional<String> id,
                     List<Location<World>> locations, boolean startPreviewOnLeftClick, Optional<HologramSettings> hologram) {
        super(id, true);
        if (locations.isEmpty()) {
            throw new IllegalArgumentException("No Locations are configured! At least one Location is required!");
        }
        this.locations = Collections.unmodifiableList(locations);
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
        this.hologram = hologram;
        if (hologram.isPresent()) {
            HologramSettings hologramSettings = hologram.get();
            List<CreatedHologram> tempCreatedHolograms = new ArrayList<>();
            locations.forEach(loc -> tempCreatedHolograms.add(GWMLibraryUtils.createHologram(loc, hologramSettings, true)));
            createdHolograms = Optional.of(Collections.unmodifiableList(tempCreatedHolograms));
        } else {
            createdHolograms = Optional.empty();
        }
    }

    @Override
    public void shutdown() {
        createdHolograms.ifPresent(createdHolograms -> createdHolograms.forEach(createdHologram -> {
            try {
                createdHologram.getHolograms().forEach(HologramsService.Hologram::remove);
                createdHologram.getUsedTicket().ifPresent(ticket ->
                        ticket.unforceChunk(createdHologram.getCachedLocation().getChunkPosition()));
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Failed to remove hologram!", e);
            }
        }));
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

    public List<Location<World>> getLocations() {
        return locations;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public Optional<HologramSettings> getHologram() {
        return hologram;
    }

    public Optional<List<CreatedHologram>> getCreatedHolograms() {
        return createdHolograms;
    }
}
