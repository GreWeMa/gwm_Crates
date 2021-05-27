package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.openmanager.CasinoOpenManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Container;

public final class CasinoOpenManagerListener {

    @Listener(order = Order.LATE)
    public void cancelClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (CasinoOpenManager.CASINO_GUI_CONTAINERS.containsKey(container)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.LATE)
    public void cancelClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        if (!CasinoOpenManager.SHOWN_GUI.contains(container) &&
                CasinoOpenManager.CASINO_GUI_CONTAINERS.containsKey(container) &&
                CasinoOpenManager.CASINO_GUI_CONTAINERS.get(container).getKey().isForbidClose()) {
            event.setCancelled(true);
        }
    }
}
