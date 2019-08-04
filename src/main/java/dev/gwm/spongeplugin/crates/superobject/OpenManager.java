package dev.gwm.spongeplugin.crates.open_manager;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.manager.Manager;
import dev.gwm.spongeplugin.crates.util.SuperObject;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public abstract class OpenManager extends SuperObject {

    private final Optional<SoundType> openSound;

    public OpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode openSoundNode = node.getNode("OPEN_SOUND");
            if (!openSoundNode.isVirtual()) {
                openSound = Optional.of(openSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                openSound = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public OpenManager(Optional<String> id, Optional<SoundType> openSound) {
        super(id);
        this.openSound = openSound;
    }

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.OPEN_MANAGER;
    }

    public boolean canOpen(Player player, Manager manager) {
        return true;
    }

    public abstract void open(Player player, Manager manager);

    public Optional<SoundType> getOpenSound() {
        return openSound;
    }
}
