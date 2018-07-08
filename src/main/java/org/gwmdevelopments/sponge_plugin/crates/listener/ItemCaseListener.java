package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.ItemCase;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.Optional;
import java.util.UUID;

public class ItemCaseListener {

    @Listener(order = Order.LATE)
    public void openItemCase(InteractItemEvent.Secondary.MainHand event, @First Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack item = event.getItemStack().createStack();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof ItemCase)) {
                continue;
            }
            ItemStack caseItem = ((ItemCase) caze).getItem();
            if (ItemStackComparators.IGNORE_SIZE.compare(item, caseItem) != 0) {
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
            caze.add(player, -1);
            key.add(player, -1);
            GWMCratesUtils.updateCrateOpenDelay(uuid);
            manager.getOpenManager().open(player, manager);
            break;
        }
    }


    @Listener(order = Order.LATE)
    public void startItemCasePreview(InteractItemEvent.Primary.MainHand event, @First Player player) {
        ItemStack item = event.getItemStack().createStack();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof ItemCase)) {
                continue;
            }
            ItemStack caseItem = ((ItemCase) caze).getItem();
            if (ItemStackComparators.IGNORE_SIZE.compare(item, caseItem) != 0) {
                continue;
            }
            event.setCancelled(true);
            if (!((ItemCase) caze).isStartPreviewOnLeftClick()) {
                return;
            }
            Optional<Preview> optionalPreview = manager.getPreview();
            if (!optionalPreview.isPresent()) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_NOT_AVAILABLE",
                        new Pair<>("%MANAGER%", manager.getName())));
                return;
            }
            Preview preview = optionalPreview.get();
            if (!player.hasPermission("gwm_crates.preview." + manager.getId())) {
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
