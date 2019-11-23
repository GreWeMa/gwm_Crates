package dev.gwm.spongeplugin.crates.superobject.caze.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.entity.living.player.Player;

public interface Case extends SuperObject {

    @Override
    default SuperObjectCategory<Case> category() {
        return GWMCratesSuperObjectCategories.CASE;
    }

    void withdraw(Player player, int amount, boolean force);

    int get(Player player);

    boolean isDoNotWithdraw();
}
