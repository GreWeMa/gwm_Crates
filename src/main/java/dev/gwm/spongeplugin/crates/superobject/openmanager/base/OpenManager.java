package dev.gwm.spongeplugin.crates.superobject.openmanager.base;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public interface OpenManager extends SuperObject {

    @Override
    default SuperObjectCategory<OpenManager> category() {
        return GWMCratesSuperObjectCategories.OPEN_MANAGER;
    }

    default boolean canOpen(Player player, Manager manager) {
        return true;
    }

    void open(Player player, Manager manager);

    Optional<SoundType> getOpenSound();
}
