package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.io.File;

public class LoadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        String path = args.<String>getOne(Text.of("path")).get();
        File file = new File(GWMCrates.getInstance().getManagersDirectory(), path);
        if (!file.exists()) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("FILE_NOT_FOUND", src, null,
                    new Pair<>("%PATH%", file.getAbsolutePath())));
            return CommandResult.success();
        }
        try {
            GWMCratesUtils.loadManager(file, true);
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_LOADED", src, null,
                    new Pair<>("%PATH%", file.getAbsolutePath())));
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Failed to load manager!", e);
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_LOAD_FAILED", src, null,
                    new Pair<>("%PATH%", file.getAbsolutePath())));
        }
        return CommandResult.success();
    }
}
