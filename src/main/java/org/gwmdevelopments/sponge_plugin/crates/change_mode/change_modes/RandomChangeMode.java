package org.gwmdevelopments.sponge_plugin.crates.change_mode.change_modes;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class RandomChangeMode extends DecorativeItemsChangeMode {

    public static final String TYPE = "RANDOM";

    public RandomChangeMode(ConfigurationNode node) {
        super(node);
    }

    public RandomChangeMode(Optional<String> id, int changeDelay, List<Integer> ignoredIndices) {
        super(id, changeDelay, ignoredIndices);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public List<ItemStack> shuffle(List<ItemStack> decorativeItems) {
        List<Integer> ignoredIndices = getIgnoredIndices();
        List<Integer> indicesToSwap = new ArrayList<>();
        for (int i = 0; i < decorativeItems.size(); i++) {
            if (!ignoredIndices.contains(i)) {
                indicesToSwap.add(i);
            }
        }
        Collections.shuffle(indicesToSwap);
        for (int i = 0; i + 1 < indicesToSwap.size(); i += 2) {
            Collections.swap(decorativeItems, indicesToSwap.get(i), indicesToSwap.get(i + 1));
        }
        return decorativeItems;
    }
}
