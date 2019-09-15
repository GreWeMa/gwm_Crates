package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.preview.FirstGuiPreview;
import dev.gwm.spongeplugin.crates.superobject.preview.SecondGuiPreview;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Container;

public class PreviewListener {

    @Listener(order = Order.LATE)
    public void cancelClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (FirstGuiPreview.FIRST_GUI_CONTAINERS.containsKey(container) ||
                SecondGuiPreview.SECOND_GUI_CONTAINERS.containsKey(container)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.LATE)
    public void manageClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        if (FirstGuiPreview.FIRST_GUI_CONTAINERS.containsKey(container)) {
            FirstGuiPreview.FIRST_GUI_CONTAINERS.remove(container);
        } else if (SecondGuiPreview.SECOND_GUI_CONTAINERS.containsKey(container)) {
            SecondGuiPreview.SECOND_GUI_CONTAINERS.remove(container);
        }
    }
}
