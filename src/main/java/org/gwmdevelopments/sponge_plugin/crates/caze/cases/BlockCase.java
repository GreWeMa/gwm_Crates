package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BlockCase extends Case {

    public static final String TYPE = "BLOCK";

    private final Location<World> location;
    private final Optional<List<Text>> hologram;
    private final boolean startPreviewOnLeftClick;
    private Optional<List<HologramsService.Hologram>> createdHolograms;

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode locationNode = node.getNode("LOCATION");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (locationNode.isVirtual()) {
                throw new IllegalArgumentException("LOCATION node does not exist!");
            }
            location = GWMLibraryUtils.parseBlockLocation(locationNode);
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(Collections.unmodifiableList(hologramNode.getList(TypeToken.of(String.class)).
                        stream().
                        map(TextSerializers.FORMATTING_CODE::deserialize).
                        collect(Collectors.toList())));
            } else {
                hologram = Optional.empty();
            }
            createdHolograms = GWMLibraryUtils.tryCreateHolograms(location, hologram,
                    GWMCrates.getInstance().getHologramOffset(), GWMCrates.getInstance().getMultilineHologramsDistance());
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public BlockCase(Optional<String> id,
                     Location<World> location, Optional<List<Text>> hologram, boolean startPreviewOnLeftClick,
                     Optional<List<HologramsService.Hologram>> createdHolograms) {
        super(id, true);
        this.location = location;
        this.hologram = hologram;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
        this.createdHolograms = createdHolograms;
    }

    @Override
    public void shutdown() {
        createdHolograms.ifPresent(holograms -> holograms.forEach(hologram -> {
            hologram.getLocation().getExtent().
                    loadChunk(hologram.getLocation().getChunkPosition(), true);
            hologram.remove();
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

    public Location<World> getLocation() {
        return location;
    }

    public Optional<List<Text>> getHologram() {
        return hologram;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public Optional<List<HologramsService.Hologram>> getCreatedHolograms() {
        return createdHolograms;
    }
}
