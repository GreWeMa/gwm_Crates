package dev.gwm.spongeplugin.crates.superobject.changemode;

import dev.gwm.spongeplugin.crates.superobject.changemode.base.AbstractDecorativeItemsChangeMode;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class RandomDecorativeItemsChangeMode extends AbstractDecorativeItemsChangeMode {

    public static final String TYPE = "RANDOM";

    public RandomDecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
    }

    public RandomDecorativeItemsChangeMode(String id, int changeDelay, List<Integer> ignoredIndices) {
        super(id, changeDelay, ignoredIndices);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public List<ItemStack> shuffle(List<ItemStack> decorativeItems) {
        List<Integer> indicesToShuffle = getIndicesToShuffle(decorativeItems);
        Collections.shuffle(indicesToShuffle);
        for (int i = 0; i + 1 < indicesToShuffle.size(); i += 2) {
            Collections.swap(decorativeItems, indicesToShuffle.get(i), indicesToShuffle.get(i + 1));
        }
        return decorativeItems;
    }
}
