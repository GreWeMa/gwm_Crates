package org.gwmdevelopments.sponge_plugin.crates.change_mode;

import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public interface DecorativeItemsChangeMode extends SuperObject {

    List<ItemStack> shuffle(List<ItemStack> decorativeItems);

    int getChangeDelay();

    List<Integer> getIgnoredIndices();
}
