package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.gui.GWMCratesGUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GUICommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            GWMCratesGUI.initialize();
        } catch (Exception e) {
            src.sendMessage(Text.builder("GWMCratesGUI already initialized (or some exception happened)!").color(TextColors.RED).build());
            GWMCrates.getInstance().getLogger().warn("Failed to initialize GWMCratesGUI!", e);
        }
        return CommandResult.success();
    }
}
