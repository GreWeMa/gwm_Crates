package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.Case;
import dev.gwm.spongeplugin.crates.superobject.Key;
import dev.gwm.spongeplugin.crates.superobject.OpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class OpenCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", src, null));
            return CommandResult.success();
        }
        Player player = (Player) src;
        UUID uuid = player.getUniqueId();
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Case caze = manager.getCase();
        Key key = manager.getKey();
        OpenManager openManager = manager.getOpenManager();
        if (!player.hasPermission("gwm_crates.open." + managerId) ||
                !player.hasPermission("gwm_crates.command.open." + managerId)) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
        if (delay > 0L) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CRATE_OPEN_DELAY", src, null,
                    new Pair<>("%TIME%", GWMCratesUtils.millisToString(delay))));
            return CommandResult.success();
        }
        if (!openManager.canOpen(player, manager)) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_MANAGER", src, null));
            return CommandResult.success();
        }
        if (caze.get(player) < 1) {
            GWMCratesUtils.sendCaseMissingMessage(src, manager);
            return CommandResult.success();
        }
        if (key.get(player) < 1) {
            GWMCratesUtils.sendKeyMissingMessage(src, manager);
            return CommandResult.success();
        }
        caze.withdraw(player, 1, false);
        key.withdraw(player, 1, false);
        GWMCratesUtils.updateCrateOpenDelay(uuid);
        openManager.open(player, manager);
        return CommandResult.success();
    }
}
