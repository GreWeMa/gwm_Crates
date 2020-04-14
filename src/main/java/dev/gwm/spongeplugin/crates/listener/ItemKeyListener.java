package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.key.ItemKey;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

public class ItemKeyListener {

    @Listener
    public void cancelItemKeyPlacing(ChangeBlockEvent.Place event) {
        event.getContext().get(EventContextKeys.USED_ITEM).
                map(ItemStackSnapshot::createStack).
                flatMap(this::findManager).
                ifPresent(manager -> event.setCancelled(true));
    }

    private Optional<Manager> findManager(ItemStack item) {
        return GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getKey() instanceof ItemKey &&
                        ItemStackComparators.IGNORE_SIZE.compare(item, ((ItemKey) manager.getKey()).getItem()) == 0).
                findFirst();
    }
}
