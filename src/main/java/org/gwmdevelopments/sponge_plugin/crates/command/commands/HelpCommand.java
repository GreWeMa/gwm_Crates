package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

public class HelpCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessages(GWMCrates.getInstance().getLanguage().getTextList("HELP_MESSAGE",
                new Pair<>("%VERSION%", GWMCrates.VERSION.toString())));
        {
            WorldProperties properties = Sponge.getServer().getWorld("world").get().getProperties();
            Sponge.getServer().getBroadcastChannel().send(Text.of("World time: " + properties.getWorldTime() + " (" + properties.getWorldTime() % 24000 + ")"));
        }
        return CommandResult.success();
    }
}
