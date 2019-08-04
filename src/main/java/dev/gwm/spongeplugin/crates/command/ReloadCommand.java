package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ReloadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GWMCrates.getInstance().reload();
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_RELOADED", src, null));
        return CommandResult.success();
    }
}
