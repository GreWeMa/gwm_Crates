package dev.gwm.spongeplugin.crates.superobject.changemode.base;

import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public interface DecorativeItemsChangeMode extends SuperObject {

    @Override
    default SuperObjectCategory<DecorativeItemsChangeMode> category() {
        return GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE;
    }

    List<ItemStack> shuffle(List<ItemStack> decorativeItems);

    int getChangeDelay();

    List<Integer> getIgnoredIndices();
}
