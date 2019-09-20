package dev.gwm.spongeplugin.crates.superobject.preview.base;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractPreview extends AbstractSuperObject implements Preview {

    private final Optional<List<Drop>> customDrops;

    public AbstractPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode customDropsNode = node.getNode("CUSTOM_DROPS");
            if (!customDropsNode.isVirtual()) {
                List<Drop> tempCustomDrops = new ArrayList<>();
                for (ConfigurationNode customDropNode : customDropsNode.getChildrenList()) {
                    tempCustomDrops.add(Sponge.getServiceManager().provide(SuperObjectsService.class).get().
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

    public AbstractPreview(String id, Optional<List<Drop>> customDrops) {
        super(id);
        this.customDrops = customDrops;
    }

    @Override
    public final SuperObjectCategory<Preview> category() {
        return GWMCratesSuperObjectCategories.PREVIEW;
    }

    public Optional<List<Drop>> getCustomDrops() {
        return customDrops;
    }
}
