package dev.gwm.spongeplugin.crates.changemode;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.SuperObject;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DecorativeItemsChangeMode extends SuperObject {

    private final int changeDelay;
    private final List<Integer> ignoredIndices;

    public DecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode changeDelayNode = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignoredIndicesNode = node.getNode("IGNORED_INDICES");
            changeDelay = changeDelayNode.getInt(10);
            ignoredIndices = ignoredIndicesNode.getList(TypeToken.of(Integer.class), new ArrayList<>());
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public DecorativeItemsChangeMode(Optional<String> id, int changeDelay, List<Integer> ignoredIndices) {
        super(id);
        this.changeDelay = changeDelay;
        this.ignoredIndices = ignoredIndices;
    }

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE;
    }

    public abstract List<ItemStack> shuffle(List<ItemStack> decorativeItems);

    public int getChangeDelay() {
        return changeDelay;
    }

    public List<Integer> getIgnoredIndices() {
        return ignoredIndices;
    }
}
