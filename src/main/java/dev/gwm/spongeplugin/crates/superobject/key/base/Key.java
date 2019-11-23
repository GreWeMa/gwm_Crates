package dev.gwm.spongeplugin.crates.superobject.key.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.entity.living.player.Player;

public interface Key extends SuperObject {

    @Override
    default SuperObjectCategory<Key> category() {
        return GWMCratesSuperObjectCategories.KEY;
    }

    void withdraw(Player player, int amount, boolean force);

    int get(Player player);

    boolean isDoNotWithdraw();
}
