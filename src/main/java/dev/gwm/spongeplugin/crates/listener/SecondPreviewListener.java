package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.preview.SecondGuiPreview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.List;
import java.util.Optional;

public final class SecondPreviewListener {

    @Listener(order = Order.LATE)
    public void cancelClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (SecondGuiPreview.SECOND_GUI_CONTAINERS.containsKey(container)) {
            event.setCancelled(true);
            Optional<Slot> optionalSlot = event.getSlot();
            if (!optionalSlot.isPresent()) return;
            Slot slot = optionalSlot.get();
            //Is this the right way to retrieve the clicked item?
            //No idea, but unless (until?) someone finds it working incorrectly, this is fine.
            //Related: https://forums.spongepowered.org/t/clickinventoryevent-get-clicked-item/37607
            ItemStack item = event.getTransactions().get(0).getOriginal().createStack();
            int position = slot.getInventoryProperty(SlotIndex.class).get().getValue();
            SecondGuiPreview.Information information = SecondGuiPreview.SECOND_GUI_CONTAINERS.get(container);
            final int rows = information.getInventoryRows();
            final int size = rows * 9;
            final int prevPageItemIndex = size - 9;
            final int nextPageItemIndex = size - 1;
            if (information.getPreview().isEnablePages()) {
                final int dropsAmount = information.getPreview().getDrops(information.getManager()).size();
                final int takenSpace = ((information.getPage() + 1) * (size - 2));
                if (position == prevPageItemIndex &&
                        ItemStackComparators.DEFAULT.compare(item, information.getPreview().getPreviousPageItem()) == 0 &&
                        information.getPage() != 0) {
                    information.setPage(information.getPage() - 1);
                    goToPage(information, container);
                } else if (position == nextPageItemIndex &&
                        ItemStackComparators.DEFAULT.compare(item, information.getPreview().getNextPageItem()) == 0 &&
                        dropsAmount > takenSpace) {
                    information.setPage(information.getPage() + 1);
                    goToPage(information, container);
                }
            }
        }
    }

    private void goToPage(SecondGuiPreview.Information information, Container container) {
        final int page = information.getPage();
        SecondGuiPreview preview = information.getPreview();
        Manager manager = information.getManager();
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(container.transform(Inventory::first));
        List<Drop> drops = preview.getDrops(manager);
        final int rows = information.getInventoryRows();
        preview.fillInventory(ordered, drops, rows, page);
    }

    @Listener(order = Order.LATE)
    public void manageClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        if (SecondGuiPreview.SECOND_GUI_CONTAINERS.containsKey(container)) {
            SecondGuiPreview.SECOND_GUI_CONTAINERS.remove(container);
        }
    }
}
