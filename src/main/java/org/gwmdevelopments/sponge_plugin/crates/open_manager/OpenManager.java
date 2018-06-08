package org.gwmdevelopments.sponge_plugin.crates.open_manager;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public abstract class OpenManager extends SuperObject {

    private Optional<SoundType> openSound = Optional.empty();

    public OpenManager(ConfigurationNode node) {
        super(node);
        ConfigurationNode openSoundNode = node.getNode("OPEN_SOUND");
        try {
            if (!openSoundNode.isVirtual()) {
                openSound = Optional.of(openSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Failed to create Open Manager!", e);
        }
    }

    public OpenManager(String type, Optional<String> id, Optional<SoundType> openSound) {
        super(type, id);
        this.openSound = openSound;
    }

    public boolean canOpen(Player player, Manager manager) {
        return true;
    }

    public abstract void open(Player player, Manager manager);

    public Optional<SoundType> getOpenSound() {
        return openSound;
    }

    public void setOpenSound(Optional<SoundType> openSound) {
        this.openSound = openSound;
    }
}
