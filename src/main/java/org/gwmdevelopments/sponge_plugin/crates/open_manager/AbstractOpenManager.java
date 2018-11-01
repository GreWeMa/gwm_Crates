package org.gwmdevelopments.sponge_plugin.crates.open_manager;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;
import org.spongepowered.api.effect.sound.SoundType;

import java.util.Optional;

public abstract class AbstractOpenManager extends AbstractSuperObject implements OpenManager {

    private Optional<SoundType> openSound = Optional.empty();

    public AbstractOpenManager(ConfigurationNode node) {
        super(node);
        ConfigurationNode openSoundNode = node.getNode("OPEN_SOUND");
        try {
            if (!openSoundNode.isVirtual()) {
                openSound = Optional.of(openSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Failed to create Abstract Open Manager!", e);
        }
    }

    public AbstractOpenManager(String type, Optional<String> id, Optional<SoundType> openSound) {
        super(type, id);
        this.openSound = openSound;
    }

    @Override
    public Optional<SoundType> getOpenSound() {
        return openSound;
    }

    @Override
    public void setOpenSound(Optional<SoundType> openSound) {
        this.openSound = openSound;
    }
}
