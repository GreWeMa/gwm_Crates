package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CheckCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.command.check." + managerId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.check_others." + managerId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        }
        int caseAmount = manager.getCase().get(player);
        int keyAmount = manager.getKey().get(player);
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("CHECK_MANAGER_INFORMATION", src, null,
                new Pair<>("%PLAYER%", player.getName()),
                new Pair<>("%MANAGER_NAME%", manager.getName()),
                new Pair<>("%MANAGER_ID%", manager.getId()),
                new Pair<>("%CASE_AMOUNT%", caseAmount),
                new Pair<>("%KEY_AMOUNT%", keyAmount)));
        return CommandResult.success();
    }
}
