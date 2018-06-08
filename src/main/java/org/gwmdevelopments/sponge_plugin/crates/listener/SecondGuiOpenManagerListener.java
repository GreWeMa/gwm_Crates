package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.SecondOpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SecondGuiOpenManagerListener {

    public static final Set<Container> SHOWN_GUI = new HashSet<>();

    @Listener(order = Order.LATE)
    public void controlClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (SHOWN_GUI.contains(container)) {
            event.setCancelled(true);
            return;
        }
        if (SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container)) {
            event.setCancelled(true);
            Optional<Player> optionalPlayer = event.getCause().first(Player.class);
            if (!optionalPlayer.isPresent()) {
                return;
            }
            Player player = optionalPlayer.get();
            OrderedInventory ordered = GWMCratesUtils.castToOrdered(container.iterator().next());
            Pair<SecondOpenManager, Manager> pair = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
            SecondOpenManager openManager = pair.getKey();
            Manager manager = pair.getValue();
            for (SlotTransaction transaction : event.getTransactions()) {
                Slot slot = transaction.getSlot();
                if (GWMCratesUtils.isFirstInventory(container, slot)) {
                    SHOWN_GUI.add(container);
                    Drop drop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, false);
                    ItemStack dropItem = drop.getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1));
                    Sponge.getScheduler().createTaskBuilder().delayTicks(1).execute(() -> slot.set(dropItem)).
                            submit(GWMCrates.getInstance());
                    if (openManager.isShowOtherDrops()) {
                        Sponge.getScheduler().createTaskBuilder().delayTicks(openManager.getShowOtherDropsDelay()).execute(() -> {
                            for (Slot next : ordered.<Slot>slots()) {
                                if (!Objects.equals(next.getProperty(SlotIndex.class, "slotindex").get().getValue(),
                                        slot.getProperty(SlotIndex.class, "slotindex").get().getValue())) {
                                    next.set(GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true).getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1)));
                                }
                            }
                        }).submit(GWMCrates.getInstance());
                    }
                    Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.SECONDS).execute(() -> drop.apply(player)).submit(GWMCrates.getInstance());
                    openManager.getClickSound().ifPresent(click_sound -> player.playSound(click_sound, player.getLocation().getPosition(), 1.));
                    PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, drop);
                    Sponge.getEventManager().post(openedEvent);
                    Sponge.getScheduler().createTaskBuilder().delayTicks(openManager.getCloseDelay()).execute(() -> {
                        Optional<Container> optionalOpenInventory = player.getOpenInventory();
                        if (optionalOpenInventory.isPresent() && container.equals(optionalOpenInventory.get())) {
                            player.closeInventory();
                        }
                        SHOWN_GUI.remove(container);
                        SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
                    }).submit(GWMCrates.getInstance());
                    return;
                }
            }
        }
    }

    @Listener(order = Order.LATE)
    public void controlClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        Optional<Player> optional_player = event.getCause().first(Player.class);
        if (!optional_player.isPresent()) return;
        Player player = optional_player.get();
        if (SecondOpenManager.SECOND_GUI_INVENTORIES.containsKey(container) &&
                !SHOWN_GUI.contains(container)) {
            Pair<SecondOpenManager, Manager> pair = SecondOpenManager.SECOND_GUI_INVENTORIES.get(container);
            SecondOpenManager open_manager = pair.getKey();
            Manager manager = pair.getValue();
            if (open_manager.isForbidClose()) {
                event.setCancelled(true);
            } else if (open_manager.isGiveRandomOnClose()) {
                Drop drop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, false);
                drop.apply(player);
                PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager, drop);
                Sponge.getEventManager().post(opened_event);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            } else {
                PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager, null);
                Sponge.getEventManager().post(opened_event);
                SecondOpenManager.SECOND_GUI_INVENTORIES.remove(container);
            }
        }
    }
}
