package dev.gwm.spongeplugin.crates.superobject.openmanager.base;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundType;

import java.util.Optional;

public abstract class AbstractOpenManager extends AbstractSuperObject implements OpenManager {

    private final Optional<SoundType> openSound;

    public AbstractOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode openSoundNode = node.getNode("OPEN_SOUND");
            if (!openSoundNode.isVirtual()) {
                openSound = Optional.of(openSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                openSound = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractOpenManager(String id, Optional<SoundType> openSound) {
        super(id);
        this.openSound = openSound;
    }

    @Override
    public final SuperObjectCategory<OpenManager> category() {
        return GWMCratesSuperObjectCategories.OPEN_MANAGER;
    }

    @Override
    public Optional<SoundType> getOpenSound() {
        return openSound;
    }
}
