package dev.gwm.spongeplugin.crates.utils;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.changemode.base.DecorativeItemsChangeMode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DecorativeItemsChangeRunnable implements Runnable {

    private final Player player;
    private final Container container;
    private final OrderedInventory ordered;
    private final DecorativeItemsChangeMode decorativeItemsChangeMode;
    private final List<Integer> decorativeItemsIndices;
    private List<ItemStack> decorativeItems;

    public DecorativeItemsChangeRunnable(Player player, Container container, OrderedInventory ordered,
                                         DecorativeItemsChangeMode decorativeItemsChangeMode,
                                         List<Integer> decorativeItemsIndices,
                                         List<ItemStack> decorativeItems) {
        this.player = player;
        this.container = container;
        this.ordered = ordered;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
        this.decorativeItemsIndices = decorativeItemsIndices;
        //Defensive copying
        this.decorativeItems = new ArrayList<>(decorativeItems);
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

    public Container getContainer() {
        return container;
    }

    public OrderedInventory getOrdered() {
        return ordered;
    }

    public DecorativeItemsChangeMode getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public List<Integer> getDecorativeItemsIndices() {
        return decorativeItemsIndices;
    }

    public List<ItemStack> getDecorativeItems() {
        return Collections.unmodifiableList(decorativeItems);
    }
}
