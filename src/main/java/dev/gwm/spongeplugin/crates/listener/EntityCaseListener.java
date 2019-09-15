package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.caze.EntityCase;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class EntityCaseListener {

    private final Language language;

    public EntityCaseListener(Language language) {
        this.language = language;
    }

    @Listener(order = Order.LATE)
    public void openEntityCase(InteractEntityEvent event, @First Player player) {
        UUID uuid = player.getUniqueId();
        Entity entity = event.getTargetEntity();
        GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof EntityCase &&
                        ((EntityCase) manager.getCase()).getEntityUuids().contains(entity.getUniqueId())).
                findFirst().
                ifPresent(manager -> {
                    event.setCancelled(true);
                    if (event instanceof InteractEntityEvent.Secondary.MainHand) {
                        if (!player.hasPermission("gwm_crates.open." + manager.getId())) {
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
                    } else if (event instanceof InteractEntityEvent.Primary.MainHand) {
                        Optional<Preview> optionalPreview = manager.getPreview();
                        if (!optionalPreview.isPresent()) {
                            GWMCratesUtils.sendPreviewNotAvailableMessage(player, manager);
                            return;
                        }
                        Preview preview = optionalPreview.get();
                        if (!player.hasPermission("gwm_crates.preview." + manager.getId())) {
                            GWMCratesUtils.sendNoPermissionToPreviewMessage(player, manager);
                            return;
                        }
                        preview.preview(player, manager);
                        player.sendMessages(language.getTranslation("PREVIEW_STARTED", Arrays.asList(
                                new Pair<>("MANAGER_NAME", manager.getName()),
                                new Pair<>("MANAGER_ID", manager.getId())
                        ), player));
                    }
                });
    }
}
