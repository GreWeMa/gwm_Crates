package dev.gwm.spongeplugin.crates.superobject.preview.base;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;

public interface Preview extends SuperObject {

    @Override
    default SuperObjectCategory<Preview> category() {
        return GWMCratesSuperObjectCategories.PREVIEW;
    }

    void preview(Player player, Manager manager);

    Optional<List<Drop>> getCustomDrops();
}
