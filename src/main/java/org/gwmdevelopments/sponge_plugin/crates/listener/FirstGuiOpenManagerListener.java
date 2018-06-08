package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.FirstOpenManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Container;

public class FirstGuiOpenManagerListener {

    @Listener(order = Order.LATE)
    public void cancelClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (FirstOpenManager.FIRST_GUI_CONTAINERS.containsKey(container)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.LATE)
    public void cancelClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        if (!FirstOpenManager.SHOWN_GUI.contains(container) &&
                FirstOpenManager.FIRST_GUI_CONTAINERS.containsKey(container) &&
                FirstOpenManager.FIRST_GUI_CONTAINERS.get(container).getKey().isForbidClose()) {
            event.setCancelled(true);
        }
    }
}
