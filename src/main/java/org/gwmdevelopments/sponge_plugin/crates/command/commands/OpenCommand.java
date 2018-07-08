package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.UUID;

public class OpenCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("COMMAND_EXECUTABLE_ONLY_BY_PLAYER"));
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
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
            return CommandResult.success();
        }
        long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
        if (delay > 0L) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CRATE_OPEN_DELAY",
                    new Pair<>("%TIME%", GWMCratesUtils.millisToString(delay))));
            return CommandResult.success();
        }
        if (!openManager.canOpen(player, manager)) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_MANAGER"));
            return CommandResult.success();
        }
        if (caze.get(player) < 1) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_CASE"));
            return CommandResult.success();
        }
        if (key.get(player) < 1) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_KEY"));
            return CommandResult.success();
        }
        caze.add(player, -1);
        key.add(player, -1);
        GWMCratesUtils.updateCrateOpenDelay(uuid);
        openManager.open(player, manager);
        return CommandResult.success();
    }
}
