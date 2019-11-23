package dev.gwm.spongeplugin.crates.superobject.changemode.base;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDecorativeItemsChangeMode extends AbstractSuperObject implements DecorativeItemsChangeMode {

    private final int changeDelay;
    private final List<Integer> ignoredIndices;

    public AbstractDecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode changeDelayNode = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignoredIndicesNode = node.getNode("IGNORED_INDICES");
            changeDelay = changeDelayNode.getInt(10);
            if (changeDelay <= 0) {
                throw new IllegalArgumentException("Change Delay is equal to or less than 0!");
            }
            if (!ignoredIndicesNode.isVirtual()) {
                ignoredIndices = Collections.unmodifiableList(ignoredIndicesNode.getList(TypeToken.of(Integer.class)));
            } else {
                ignoredIndices = Collections.emptyList();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractDecorativeItemsChangeMode(String id, int changeDelay, List<Integer> ignoredIndices) {
        super(id);
        if (changeDelay <= 0) {
            throw new IllegalArgumentException("Change Delay is equal to or less than 0!");
        }
        this.changeDelay = changeDelay;
        this.ignoredIndices = Collections.unmodifiableList(ignoredIndices);
    }

    @Override
    public final SuperObjectCategory<DecorativeItemsChangeMode> category() {
        return GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE;
    }

    protected List<Integer> getIndicesToShuffle(List<ItemStack> decorativeItems) {
        List<Integer> ignoredIndices = getIgnoredIndices();
        List<Integer> indicesToShuffle = new ArrayList<>();
        for (int i = 0; i < decorativeItems.size(); i++) {
            if (!ignoredIndices.contains(i)) {
                indicesToShuffle.add(i);
            }
        }
        return indicesToShuffle;
    }

    @Override
    public int getChangeDelay() {
        return changeDelay;
    }

    @Override
    public List<Integer> getIgnoredIndices() {
        return ignoredIndices;
    }
}
