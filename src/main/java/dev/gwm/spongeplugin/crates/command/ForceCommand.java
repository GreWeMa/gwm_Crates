package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.OpenManager;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ForceCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.command.force." + managerId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.force_others." + managerId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        }
        OpenManager openManager = manager.getOpenManager();
        if (!openManager.canOpen(player, manager)) {
            if (self) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("CAN_NOT_OPEN_MANAGER", src, null));
                return CommandResult.success();
            } else {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("PLAYER_CAN_NOT_OPEN_MANAGER", src, null,
                        new Pair<>("%PLAYER%", player.getName())));
                return CommandResult.success();
            }
        }
        openManager.open(player, manager);
        if (!self) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("CRATE_FORCE_OPENED_FOR_PLAYER", src, null,
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
