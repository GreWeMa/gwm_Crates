package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.caze.EntityCase;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class EntityCaseListener {

    private final Language language;

    public EntityCaseListener(Language language) {
        this.language = language;
    }

    @Listener
    public void cancelOffhandEvent(InteractEntityEvent.Primary.OffHand event, @First Player player) {
        findManager(event.getTargetEntity().getUniqueId()).
                ifPresent(manager -> event.setCancelled(true));
    }

    @Listener
    public void cancelOffhandEvent(InteractEntityEvent.Secondary.OffHand event, @First Player player) {
        findManager(event.getTargetEntity().getUniqueId()).
                ifPresent(manager -> event.setCancelled(true));
    }

    @Listener(order = Order.LATE)
    public void openEntityCase(InteractEntityEvent.Secondary.MainHand event, @First Player player) {
        UUID uuid = player.getUniqueId();
        findManager(event.getTargetEntity().getUniqueId()).ifPresent(manager -> {
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
            key.withdraw(player, 1, false);
            GWMCratesUtils.updateCrateOpenDelay(uuid);
            openManager.open(player, manager);
        });
    }

    @Listener(order = Order.LATE)
    public void previewEntityCase(InteractEntityEvent.Primary.MainHand event, @First Player player) {
        findManager(event.getTargetEntity().getUniqueId()).ifPresent(manager -> {
            event.setCancelled(true);
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

    private Optional<Manager> findManager(UUID entityUuid) {
        return GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof EntityCase &&
                        ((EntityCase) manager.getCase()).getEntityUuids().contains(entityUuid)).
                findFirst();
    }
}
