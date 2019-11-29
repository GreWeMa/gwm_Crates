package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenedCrateEvent;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.SecondOpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.Collections;
import java.util.Optional;

public class SecondOpenManagerListener {

    @Listener(order = Order.LATE)
    public void controlClick(ClickInventoryEvent event, @First Player player) {
        Container container = event.getTargetInventory();
        if (!SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container)) return;
        event.setCancelled(true);
        SecondOpenManager.Data data = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
        SecondOpenManager openManager = data.getOpenManager();
        if (data.isOpened()) return;
        Manager manager = data.getManager();
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(container.iterator().next());
        for (SlotTransaction transaction : event.getTransactions()) {
            Slot slot = transaction.getSlot();
            int slotIndex = slot.getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
            if (!GWMCratesUtils.isFirstInventory(container, slot) || data.getOpenedIndices().containsKey(slotIndex)) continue;
            openManager.getClickSound().ifPresent(clickSound ->
                    player.playSound(clickSound, player.getLocation().getPosition(), 1.));
            Drop drop = data.openIndex(slotIndex, player);
            ItemStack dropItem = drop.getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1));
            Sponge.getScheduler().createTaskBuilder().
                    delayTicks(1).
                    execute(() -> {
                        slot.set(dropItem);
                        drop.give(player, 1);
                    }).
                    submit(GWMCrates.getInstance());
            if (data.isOpened()) {
                if (openManager.isShowOtherDrops()) {
                    Sponge.getScheduler().createTaskBuilder().
                            delayTicks(openManager.getShowOtherDropsDelay()).
                            execute(() -> {
                                for (Slot next : ordered.<Slot>slots()) {
                                    int nextSlotIndex = next.getProperty(SlotIndex.class, "slotindex").get().getValue();
                                    if (!data.getOpenedIndices().containsKey(nextSlotIndex)) {
                                        next.set(((Drop) manager.getRandomManager().
                                                choose(manager.getDrops(), player, true)).
                                                getDropItem().
                                                orElse(ItemStack.of(ItemTypes.NONE)));
                                    }
                                }
                            }).submit(GWMCrates.getInstance());
                }
                PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, data.getOpenedIndices().values());
                Sponge.getEventManager().post(openedEvent);
                Sponge.getScheduler().createTaskBuilder().
                        delayTicks(openManager.getCloseDelay()).
                        execute(() -> {
                            Optional<Container> optionalOpenInventory = player.getOpenInventory();
                            if (optionalOpenInventory.isPresent() && container.equals(optionalOpenInventory.get())) {
                                player.closeInventory();
                            }
                            SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
                        }).submit(GWMCrates.getInstance());
            }
            return;
        }
    }

    @Listener(order = Order.LATE)
    public void controlClose(InteractInventoryEvent.Close event, @First Player player) {
        Container container = event.getTargetInventory();
        if (!SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container)) return;
        SecondOpenManager.Data data = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
        if (!data.isOpened()) {
            SecondOpenManager openManager = data.getOpenManager();
            Manager manager = data.getManager();
            if (openManager.isForbidClose()) {
                event.setCancelled(true);
            } else if (openManager.isGiveRandomOnClose()) {
                Drop drop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, false);
                drop.give(player, 1);
                PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, Collections.singletonList(drop));
                Sponge.getEventManager().post(openedEvent);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            } else {
                PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, Collections.emptyList());
                Sponge.getEventManager().post(openedEvent);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            }
        }
    }
}
