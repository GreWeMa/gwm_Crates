package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.BlockCase;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class BlockCaseListener {

    @Listener(order = Order.LATE)
    public void openBlockCase(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        UUID uuid = player.getUniqueId();
        Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
        if (!optionalLocation.isPresent()) {
            return;
        }
        Location<World> location = optionalLocation.get();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof BlockCase)) {
                continue;
            }
            Location<World> blockCaseLocation = ((BlockCase) caze).getLocation();
            if (!blockCaseLocation.equals(location)) {
                continue;
            }
            event.setCancelled(true);
            if (!player.hasPermission("gwm_crates.open." + manager.getId())) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return;
            }
            long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
            if (delay > 0L) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CRATE_OPEN_DELAY",
                        new Pair<>("%TIME%", GWMCratesUtils.millisToString(delay))));
                return;
            }
            OpenManager openManager = manager.getOpenManager();
            if (!openManager.canOpen(player, manager)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_MANAGER"));
                return;
            }
            Key key = manager.getKey();
            if (key.get(player) < 1) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_KEY"));
                return;
            }
            key.withdraw(player, 1, false);
            GWMCratesUtils.updateCrateOpenDelay(uuid);
            manager.getOpenManager().open(player, manager);
            break;
        }
    }

    @Listener(order = Order.LATE)
    public void startBlockCasePreview(InteractBlockEvent.Primary.MainHand event, @First Player player) {
        Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
        if (!optionalLocation.isPresent()) {
            return;
        }
        Location<World> location = optionalLocation.get();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            String manager_id = manager.getId();
            Case caze = manager.getCase();
            if (!(caze instanceof BlockCase)) {
                continue;
            }
            Location<World> blockCaseLocation = ((BlockCase) caze).getLocation();
            if (!blockCaseLocation.equals(location)) {
                continue;
            }
            event.setCancelled(true);
            if (!((BlockCase) caze).isStartPreviewOnLeftClick()) {
                return;
            }
            Optional<Preview> optionalPreview = manager.getPreview();
            if (!optionalPreview.isPresent()) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_NOT_AVAILABLE",
                        new Pair<>("%MANAGER%", manager.getName())));
                return;
            }
            Preview preview = optionalPreview.get();
            if (!player.hasPermission("gwm_crates.preview." + manager_id)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return;
            }
            preview.preview(player, manager);
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_STARTED",
                    new Pair<>("%MANAGER%", manager.getName())));
            break;
        }
    }
}
