package org.gwmdevelopments.sponge_plugin.crates.change_mode;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DecorativeItemsChangeMode extends SuperObject {

    private int changeDelay;
    private List<Integer> ignoredIndices;

    public DecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode changeDelayNode = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignoredIndicesNode = node.getNode("IGNORED_INDICES");
            changeDelay = changeDelayNode.getInt(10);
            ignoredIndices = ignoredIndicesNode.getList(TypeToken.of(Integer.class), new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Decorative Items Change Mode!", e);
        }
    }

    public DecorativeItemsChangeMode(String type, Optional<String> id, int changeDelay, List<Integer> ignoredIndices) {
        super(type, id);
        this.changeDelay = changeDelay;
        this.ignoredIndices = ignoredIndices;
    }

    public abstract List<ItemStack> shuffle(List<ItemStack> decorative_items);

    public int getChangeDelay() {
        return changeDelay;
    }

    public void setChangeDelay(int changeDelay) {
        this.changeDelay = changeDelay;
    }

    public List<Integer> getIgnoredIndices() {
        return ignoredIndices;
    }

    public void setIgnoredIndices(List<Integer> ignoredIndices) {
        this.ignoredIndices = ignoredIndices;
    }
}
