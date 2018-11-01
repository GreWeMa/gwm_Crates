package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.AbstractCase;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlockCase extends AbstractCase {

    private Location<World> location;
    private Optional<List<Text>> hologram = Optional.empty();
    private boolean startPreviewOnLeftClick = false;
    private Optional<List<HologramsService.Hologram>> createdHolograms = Optional.empty();

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode locationNode = node.getNode("LOCATION");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (locationNode.isVirtual()) {
                throw new RuntimeException("LOCATION node does not exist!");
            }
            location = GWMLibraryUtils.parseLocation(locationNode);
            if (!hologramNode.isVirtual()) {
                hologram = Optional.of(hologramNode.getList(TypeToken.of(String.class)).
                        stream().
                        map(TextSerializers.FORMATTING_CODE::deserialize).
                        collect(Collectors.toList()));
            }
            createdHolograms = GWMLibraryUtils.tryCreateHolograms(location, hologram,
                    GWMCrates.getInstance().getHologramOffset(), GWMCrates.getInstance().getMultilineHologramsDistance());
            startPreviewOnLeftClick = startPreviewOnLeftClickNode.getBoolean(false);
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Failed to create Block Case!", e);
        }
    }

    public BlockCase(Optional<String> id,
                     Location<World> location, Optional<List<Text>> hologram, boolean startPreviewOnLeftClick,
                     Optional<List<HologramsService.Hologram>> createdHolograms) {
        super("BLOCK", id);
        this.location = location;
        this.hologram = hologram;
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
        this.createdHolograms = createdHolograms;
    }

    @Override
    public void withdraw(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }

    public Location<World> getLocation() {
        return location;
    }

    public void setLocation(Location<World> location) {
        this.location = location;
    }

    public Optional<List<Text>> getHologram() {
        return hologram;
    }

    public void setHologram(Optional<List<Text>> hologram) {
        this.hologram = hologram;
    }

    public boolean isStartPreviewOnLeftClick() {
        return startPreviewOnLeftClick;
    }

    public void setStartPreviewOnLeftClick(boolean startPreviewOnLeftClick) {
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
    }

    public Optional<List<HologramsService.Hologram>> getCreatedHolograms() {
        return createdHolograms;
    }

    public void setCreatedHolograms(Optional<List<HologramsService.Hologram>> createdHolograms) {
        this.createdHolograms = createdHolograms;
    }
}
