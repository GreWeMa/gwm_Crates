package dev.gwm.spongeplugin.crates.superobject.changemode.base;

import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public interface DecorativeItemsChangeMode extends SuperObject {

    @Override
    default SuperObjectCategory<DecorativeItemsChangeMode> category() {
        return GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE;
    }

    List<ItemStack> change(List<ItemStack> decorativeItems);

    int getChangeDelay();

    List<Integer> getIgnoredIndices();
}
