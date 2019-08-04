package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class SaveCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GWMCrates.getInstance().save();
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_SAVED", src, null));
        return CommandResult.success();
    }
}
