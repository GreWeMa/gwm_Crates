package org.gwmdevelopments.sponge_plugin.crates.open_manager;

import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public interface OpenManager extends SuperObject {

    default boolean canOpen(Player player, Manager manager) {
        return true;
    }

    void open(Player player, Manager manager);

    Optional<SoundType> getOpenSound();

    void setOpenSound(Optional<SoundType> openSound);
}
