package org.gwmdevelopments.sponge_plugin.crates.command.commands.give;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

public class GiveCaseCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.command.give.manager." + managerId + ".case")) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.give_others.manager." + managerId + ".case")) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        }
        manager.getCase().give(player, amount);
        if (self) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_CASE",
                    new Pair<>("%MANAGER%", manager.getName())));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_CASE",
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
