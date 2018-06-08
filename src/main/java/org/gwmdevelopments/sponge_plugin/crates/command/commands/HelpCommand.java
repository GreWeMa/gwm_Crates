package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.command.GWMCratesCommandUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class HelpCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessages(GWMCrates.getInstance().getLanguage().getTextList("HELP_MESSAGE",
                new Pair<>("%VERSION%", GWMCrates.VERSION.toString())));
        return CommandResult.success();
    }
}
