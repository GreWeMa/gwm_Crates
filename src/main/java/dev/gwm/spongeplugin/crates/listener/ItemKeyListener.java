package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.key.ItemKey;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class ItemKeyListener {

    @Listener
    public void cancelItemKeyPlacing(ChangeBlockEvent.Place event) {
        event.getContext().get(EventContextKeys.USED_ITEM).
                map(ItemStackSnapshot::createStack).ifPresent(item -> {
            if (GWMCratesUtils.getManagersStream().
                    anyMatch(manager -> manager.getKey() instanceof ItemKey &&
                            ItemStackComparators.IGNORE_SIZE.compare(item, ((ItemKey) manager.getKey()).getItem()) == 0)) {
                event.setCancelled(true);
            }
        });
    }
}
