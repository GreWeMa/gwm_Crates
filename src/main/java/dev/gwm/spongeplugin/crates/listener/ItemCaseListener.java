package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.caze.ItemCase;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ItemCaseListener {

    private final Language language;

    public ItemCaseListener(Language language) {
        this.language = language;
    }

    @Listener(order = Order.LATE)
    public void openItemCase(InteractItemEvent.Secondary event, @First Player player) {
        openItemCase(event, player, event.getItemStack().createStack());
    }

    @Listener(order = Order.LATE)
    public void openItemCase(InteractBlockEvent.Secondary event, @First Player player) {
        event.getContext().get(EventContextKeys.USED_ITEM).
                ifPresent(item -> openItemCase(event, player, item.createStack()));
    }

    private void openItemCase(Cancellable event, Player player, ItemStack item) {
        UUID uuid = player.getUniqueId();
        GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof ItemCase &&
                        ItemStackComparators.IGNORE_SIZE.compare(item, ((ItemCase) manager.getCase()).getItem()) == 0).
                findFirst().
                ifPresent(manager -> {
                    event.setCancelled(true);
                    if (!player.hasPermission("gwm_crates.open." + manager.id())) {
                        GWMCratesUtils.sendNoPermissionToOpenMessage(player, manager);
                        return;
                    }
                    long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
                    if (delay > 0L) {
                        GWMCratesUtils.sendCrateDelayMessage(player, manager, delay);
                        return;
                    }
                    OpenManager openManager = manager.getOpenManager();
                    if (!openManager.canOpen(player, manager)) {
                        GWMCratesUtils.sendCannotOpenMessage(player, manager);
                        return;
                    }
                    Key key = manager.getKey();
                    if (key.get(player) < 1) {
                        GWMCratesUtils.sendKeyMissingMessage(player, manager);
                        return;
                    }
                    manager.getCase().withdraw(player, 1, false);
                    key.withdraw(player, 1, false);
                    GWMCratesUtils.updateCrateOpenDelay(uuid);
                    manager.getOpenManager().open(player, manager);
                });
    }


    @Listener(order = Order.LATE)
    public void previewItemCase(InteractItemEvent.Primary event, @First Player player) {
        previewItemCase(event, player, event.getItemStack().createStack());
    }

    @Listener
    public void previewItemCase(InteractBlockEvent.Primary event, @First Player player) {
        event.getContext().get(EventContextKeys.USED_ITEM).
                ifPresent(item -> previewItemCase(event, player, item.createStack()));
    }

    private void previewItemCase(Cancellable event, Player player, ItemStack item) {
        GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof ItemCase &&
                        ItemStackComparators.IGNORE_SIZE.compare(item, ((ItemCase) manager.getCase()).getItem()) == 0).
                findFirst().
                ifPresent(manager -> {
                    event.setCancelled(true);
                    if (!((ItemCase) manager.getCase()).isStartPreviewOnLeftClick()) {
                        return;
                    }
                    Optional<Preview> optionalPreview = manager.getPreview();
                    if (!optionalPreview.isPresent()) {
                        GWMCratesUtils.sendPreviewNotAvailableMessage(player, manager);
                        return;
                    }
                    Preview preview = optionalPreview.get();
                    if (!player.hasPermission("gwm_crates.preview." + manager.id())) {
                        GWMCratesUtils.sendNoPermissionToPreviewMessage(player, manager);
                        return;
                    }
                    preview.preview(player, manager);
                    player.sendMessages(language.getTranslation("PREVIEW_STARTED", Arrays.asList(
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    ), player));
                });
    }
}
