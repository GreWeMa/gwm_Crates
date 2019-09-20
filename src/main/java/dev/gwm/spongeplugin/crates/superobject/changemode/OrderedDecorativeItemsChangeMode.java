package dev.gwm.spongeplugin.crates.superobject.changemode;

import dev.gwm.spongeplugin.crates.superobject.changemode.base.AbstractDecorativeItemsChangeMode;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public final class OrderedDecorativeItemsChangeMode extends AbstractDecorativeItemsChangeMode {

    public static final String TYPE = "ORDERED";

    private final boolean right;

    public OrderedDecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode rightNode = node.getNode("RIGHT");
            right = rightNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public OrderedDecorativeItemsChangeMode(String id, int changeDelay, List<Integer> ignoredIndices,
                             boolean right) {
        super(id, changeDelay, ignoredIndices);
        this.right = right;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public List<ItemStack> shuffle(List<ItemStack> decorativeItems) {
        List<Integer> indicesToShuffle = getIndicesToShuffle(decorativeItems);
        if (right) {
            ItemStack previous = decorativeItems.get(0);
            ItemStack temp;
            decorativeItems.set(0, decorativeItems.get(indicesToShuffle.get(indicesToShuffle.size() - 1)));
            for (int i = 1 ; i < indicesToShuffle.size(); i++) {
                temp = previous;
                previous = decorativeItems.get(indicesToShuffle.get(i));
                decorativeItems.set(indicesToShuffle.get(i), temp);
            }
        } else {
            ItemStack previous = decorativeItems.get(indicesToShuffle.get(indicesToShuffle.size() - 1));
            ItemStack temp;
            decorativeItems.set(indicesToShuffle.get(indicesToShuffle.size() - 1), decorativeItems.get(indicesToShuffle.get(0)));
            for (int i = indicesToShuffle.size() - 2 ; i >= 0; i--) {
                temp = previous;
                previous = decorativeItems.get(indicesToShuffle.get(i));
                decorativeItems.set(indicesToShuffle.get(i), temp);
            }
        }
        return decorativeItems;
    }

    public boolean isRight() {
        return right;
    }
}
