package dev.gwm.spongeplugin.crates.superobject.preview.base;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPreview extends AbstractSuperObject implements Preview {

    private final boolean showEmptyDrops;
    private final Optional<List<Drop>> customDrops;

    public AbstractPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode showEmptyDropsNode = node.getNode("SHOW_EMPTY_DROPS");
            ConfigurationNode customDropsNode = node.getNode("CUSTOM_DROPS");
            showEmptyDrops = showEmptyDropsNode.getBoolean(false);
            if (!customDropsNode.isVirtual()) {
                List<Drop> tempCustomDrops = new ArrayList<>();
                for (ConfigurationNode customDropNode : customDropsNode.getChildrenList()) {
                    tempCustomDrops.add(Sponge.getServiceManager().provide(SuperObjectService.class).get().
                            create(GWMCratesSuperObjectCategories.DROP, customDropNode));
                }
                customDrops = Optional.of(Collections.unmodifiableList(tempCustomDrops));
            } else {
                customDrops = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractPreview(String id, boolean showEmptyDrops, Optional<List<Drop>> customDrops) {
        super(id);
        this.showEmptyDrops = showEmptyDrops;
        this.customDrops = customDrops;
    }

    public List<Drop> getDrops(Manager manager) {
        Stream<Drop> stream = customDrops.orElse(manager.getDrops()).
                stream().
                filter(Drop::isShowInPreview);
        if (!showEmptyDrops) {
            stream = stream.filter(drop -> drop.getDropItem().isPresent());
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public final SuperObjectCategory<Preview> category() {
        return GWMCratesSuperObjectCategories.PREVIEW;
    }

    @Override
    public boolean isShowEmptyDrops() {
        return showEmptyDrops;
    }

    public Optional<List<Drop>> getCustomDrops() {
        return customDrops;
    }
}
