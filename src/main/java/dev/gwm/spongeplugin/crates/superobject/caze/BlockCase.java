package dev.gwm.spongeplugin.crates.caze;

import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Case;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.library.utils.CreatedHologram;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.HologramSettings;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class BlockCase extends Case {

    public static final String TYPE = "BLOCK";

    private final List<Location<World>> locations;
    private final Optional<HologramSettings> hologram;
    private final boolean startPreviewOnLeftClick;
    private Optional<List<CreatedHologram>> createdHolograms;

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode locationNode = node.getNode("LOCATION");
            ConfigurationNode locationsNode = node.getNode("LOCATIONS");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            List<Location<World>> tempLocations = new ArrayList<>();
            if (!locationsNode.isVirtual()) {
                for (ConfigurationNode innerLocationNode : locationsNode.getChildrenList()) {
                    tempLocations.add(GWMLibraryUtils.parseLocation(innerLocationNode));
                }
            }
            //Backwards compatibility
            else if (!locationNode.isVirtual()) {
                GWMCrates.getInstance().getLogger().warn("[BACKWARD COMPATIBILITY] LOCATIONS node does not exist! Trying to use LOCATION node!");
                tempLocations.add(GWMLibraryUtils.parseLocation(locationNode));
            } else {
                throw new IllegalArgumentException("None of LOCATIONS and LOCATION nodes exist!");
            }
            if (tempLocations.isEmpty()) {
                throw new IllegalArgumentException("No locations are configured! At least one location is required!");
            }
            locations = Collections.unmodifiableList(tempLocations);
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(GWMLibraryUtils.parseHologramSettings(hologramNode,
                        GWMCrates.getInstance().getHologramOffset(),
                        GWMCrates.getInstance().getMultilineHologramsDistance()));
            } else {
                hologram = Optional.empty();
            }
            if (hologram.isPresent()) {
                HologramSettings hgs = hologram.get();
                List<CreatedHologram> tempCreatedHolograms = new ArrayList<>();
                locations.forEach(loc -> tempCreatedHolograms.add(GWMLibraryUtils.createHologram(loc, hgs, true)));
                createdHolograms = Optional.of(Collections.unmodifiableList(tempCreatedHolograms));
            } else {
                createdHolograms = Optional.empty();
            }
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public BlockCase(Optional<String> id,
                     List<Location<World>> locations, Optional<HologramSettings> hologram, boolean startPreviewOnLeftClick,
                     Optional<List<CreatedHologram>> createdHolograms) {
        super(id, true);
        this.locations = locations;
        this.hologram = hologram;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
        this.createdHolograms = createdHolograms;
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

    public Optional<HologramSettings> getHologram() {
        return hologram;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public Optional<List<CreatedHologram>> getCreatedHolograms() {
        return createdHolograms;
    }
}
