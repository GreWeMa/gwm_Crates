package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.superobject.caze.BlockCase;
import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class BlockCaseListener {

    private final Language language;

    public BlockCaseListener(Language language) {
        this.language = language;
    }

    @Listener(order = Order.LATE)
    public void openBlockCase(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        UUID uuid = player.getUniqueId();
        Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
        if (!optionalLocation.isPresent()) {
            return;
        }
        Location<World> location = optionalLocation.get();
        GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof BlockCase &&
                        ((BlockCase) manager.getCase()).getLocations().contains(location)).
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
                    key.withdraw(player, 1, false);
                    GWMCratesUtils.updateCrateOpenDelay(uuid);
                    manager.getOpenManager().open(player, manager);
                });
    }

    @Listener(order = Order.LATE)
    public void startBlockCasePreview(InteractBlockEvent.Primary.MainHand event, @First Player player) {
        Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
        if (!optionalLocation.isPresent()) {
            return;
        }
        Location<World> location = optionalLocation.get();
        GWMCratesUtils.getManagersStream().
                filter(manager -> manager.getCase() instanceof BlockCase &&
                        ((BlockCase) manager.getCase()).getLocations().contains(location)).
                findFirst().
                ifPresent(manager -> {
                    String managerId = manager.id();
                    Case caze = manager.getCase();
                    event.setCancelled(true);
                    if (!((BlockCase) caze).isStartPreviewOnLeftClick()) {
                        return;
                    }
                    Optional<Preview> optionalPreview = manager.getPreview();
                    if (!optionalPreview.isPresent()) {
                        GWMCratesUtils.sendPreviewNotAvailableMessage(player, manager);
                        return;
                    }
                    Preview preview = optionalPreview.get();
                    if (!player.hasPermission("gwm_crates.preview." + managerId)) {
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
