package org.gwmdevelopments.sponge_plugin.crates.change_mode.change_modes;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.AbstractDecorativeItemsChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderedChangeMode extends AbstractDecorativeItemsChangeMode {

    private boolean right;

    public OrderedChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode rightNode = node.getNode("RIGHT");
            right = rightNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Ordered Decorative Items Change Mode!", e);
        }
    }

    public OrderedChangeMode(Optional<String> id, int changeDelay, List<Integer> ignoredIndices,
                             boolean right) {
        super("ORDERED", id, changeDelay, ignoredIndices);
        this.right = right;
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
        if (right) {
            ItemStack previous = decorativeItems.get(0);
            ItemStack temp;
            decorativeItems.set(0, decorativeItems.get(indicesToSwap.get(indicesToSwap.size() - 1)));
            for (int i = 1 ; i < indicesToSwap.size(); i++) {
                temp = previous;
                previous = decorativeItems.get(indicesToSwap.get(i));
                decorativeItems.set(indicesToSwap.get(i), temp);
            }
        } else {
            ItemStack previous = decorativeItems.get(indicesToSwap.get(indicesToSwap.size() - 1));
            ItemStack temp;
            decorativeItems.set(indicesToSwap.get(indicesToSwap.size() - 1), decorativeItems.get(indicesToSwap.get(0)));
            for (int i = indicesToSwap.size() - 2 ; i >= 0; i--) {
                temp = previous;
                previous = decorativeItems.get(indicesToSwap.get(i));
                decorativeItems.set(indicesToSwap.get(i), temp);
            }
        }
        return decorativeItems;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
