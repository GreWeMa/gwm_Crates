package dev.gwm.spongeplugin.crates.superobject.drop.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.Randomable;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public interface Drop extends SuperObject, Giveable, Randomable {

    @Override
    default SuperObjectCategory<Drop> category() {
        return GWMCratesSuperObjectCategories.DROP;
    }

    void give(Player player, int amount);

    @Override
    default void give(Player player, int amount, boolean force) {
        give(player, amount);
    }

    Optional<ItemStack> getDropItem();

    Optional<String> getCustomName();

    boolean isShowInPreview();
}
