package org.gwmdevelopments.sponge_plugin.crates.util;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.List;
import java.util.Optional;

public class DecorativeDropChangeRunnable implements Runnable {

    private Player player;
    private Container container;
    private OrderedInventory ordered;
    private List<ItemStack> decorativeItems;
    private DecorativeItemsChangeMode decorativeItemsChangeMode;
    private List<Integer> decorativeItemsIndices;

    public DecorativeDropChangeRunnable(Player player, Container container, OrderedInventory ordered,
                                        List<ItemStack> decorativeItems,
                                        DecorativeItemsChangeMode decorativeItemsChangeMode,
                                        List<Integer> decorativeItemsIndices) {
        this.player = player;
        this.container = container;
        this.ordered = ordered;
        this.decorativeItems = decorativeItems;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
        this.decorativeItemsIndices = decorativeItemsIndices;
    }

    @Override
    public void run() {
        Optional<Container> openInventory = player.getOpenInventory();
        if (openInventory.isPresent() && openInventory.get().equals(container)) {
            decorativeItems = decorativeItemsChangeMode.shuffle(decorativeItems);
            int index = 0;
            for (int i = 0; i < decorativeItemsIndices.size(); i++, index++) {
                if (index == decorativeItems.size()) {
                    index = 0;
                }
                ordered.getSlot(new SlotIndex(decorativeItemsIndices.get(i))).get().
                        set(decorativeItems.get(index));
            }
            Sponge.getScheduler().createTaskBuilder().
                    delayTicks(decorativeItemsChangeMode.getChangeDelay()).
                    execute(this).
                    submit(GWMCrates.getInstance());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public OrderedInventory getOrdered() {
        return ordered;
    }

    public void setOrdered(OrderedInventory ordered) {
        this.ordered = ordered;
    }

    public List<ItemStack> getDecorativeItems() {
        return decorativeItems;
    }

    public void setDecorativeItems(List<ItemStack> decorativeItems) {
        this.decorativeItems = decorativeItems;
    }

    public DecorativeItemsChangeMode getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public void setDecorativeItemsChangeMode(DecorativeItemsChangeMode decorativeItemsChangeMode) {
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }
}
