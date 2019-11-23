package dev.gwm.spongeplugin.crates.superobject.caze;

import de.randombyte.holograms.api.HologramsService;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.CosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.caze.base.AbstractCase;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.CreatedHologram;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.HologramSettings;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public final class BlockCase extends AbstractCase {

    public static final String TYPE = "BLOCK";

    private final List<Location<World>> locations;
    private final boolean startPreviewOnLeftClick;
    private final Optional<HologramSettings> hologram;
    private final Optional<List<CosmeticEffect>> persistentCosmeticEffects;
    private Optional<List<CreatedHologram>> createdHolograms;
    private Optional<List<Task>> cosmeticEffectTasks;

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode locationsNode = node.getNode("LOCATIONS");
            ConfigurationNode startPreviewOnLeftClickNode = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            ConfigurationNode hologramNode = node.getNode("HOLOGRAM");
            ConfigurationNode persistentCosmeticEffectsNode = node.getNode("PERSISTENT_COSMETIC_EFFECTS");
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
            if (!persistentCosmeticEffectsNode.isVirtual()) {
                List<CosmeticEffect> tempPersistentCosmeticEffects = new ArrayList<>();
                for (ConfigurationNode persistentCosmeticEffectNode : persistentCosmeticEffectsNode.getChildrenList()) {
                    tempPersistentCosmeticEffects.add(Sponge.getServiceManager().provide(SuperObjectService.class).get().
                            create(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, persistentCosmeticEffectNode));
                }
                persistentCosmeticEffects = Optional.of(Collections.unmodifiableList(tempPersistentCosmeticEffects));
            } else {
                persistentCosmeticEffects = Optional.empty();
            }
            if (hologram.isPresent()) {
                HologramSettings hologramSettings = hologram.get();
                List<CreatedHologram> tempCreatedHolograms = new ArrayList<>();
                locations.forEach(loc -> tempCreatedHolograms.add(GWMLibraryUtils.createHologram(loc, hologramSettings, true)));
                createdHolograms = Optional.of(Collections.unmodifiableList(tempCreatedHolograms));
            } else {
                createdHolograms = Optional.empty();
            }
            if (persistentCosmeticEffects.isPresent()) {
                List<Task> tempTasks = new ArrayList<>();
                locations.forEach(location ->
                        persistentCosmeticEffects.get().forEach(effect ->
                                tempTasks.add(effect.activate(location.getExtent(), location))));
                cosmeticEffectTasks = Optional.of(Collections.unmodifiableList(tempTasks));
            } else {
                cosmeticEffectTasks = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public BlockCase(String id,
                     List<Location<World>> locations, boolean startPreviewOnLeftClick,
                     Optional<HologramSettings> hologram, Optional<List<CosmeticEffect>> persistentCosmeticEffects) {
        super(id, true);
        if (locations.isEmpty()) {
            throw new IllegalArgumentException("No Locations are configured! At least one Location is required!");
        }
        this.locations = Collections.unmodifiableList(locations);
        this.startPreviewOnLeftClick = startPreviewOnLeftClick;
        this.hologram = hologram;
        this.persistentCosmeticEffects = persistentCosmeticEffects;
        if (hologram.isPresent()) {
            HologramSettings hologramSettings = hologram.get();
            List<CreatedHologram> tempCreatedHolograms = new ArrayList<>();
            locations.forEach(loc -> tempCreatedHolograms.add(GWMLibraryUtils.createHologram(loc, hologramSettings, true)));
            createdHolograms = Optional.of(Collections.unmodifiableList(tempCreatedHolograms));
        } else {
            createdHolograms = Optional.empty();
        }
        if (persistentCosmeticEffects.isPresent()) {
            List<Task> tempTasks = new ArrayList<>();
            locations.forEach(location ->
                    persistentCosmeticEffects.get().forEach(effect ->
                            tempTasks.add(effect.activate(location.getExtent(), location))));
            cosmeticEffectTasks = Optional.of(Collections.unmodifiableList(tempTasks));
        } else {
            cosmeticEffectTasks = Optional.empty();
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
        cosmeticEffectTasks.ifPresent(tasks -> tasks.forEach(Task::cancel));
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        persistentCosmeticEffects.ifPresent(set::addAll);
        return set;
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

    public Optional<List<CosmeticEffect>> getPersistentCosmeticEffects() {
        return persistentCosmeticEffects;
    }

    public Optional<List<CreatedHologram>> getCreatedHolograms() {
        return createdHolograms;
    }

    public Optional<List<Task>> getCosmeticEffectTasks() {
        return cosmeticEffectTasks;
    }
}
