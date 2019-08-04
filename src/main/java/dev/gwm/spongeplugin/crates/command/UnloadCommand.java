package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class UnloadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        if (!src.hasPermission("gwm_crates.command.unload." + managerId)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        try {
            manager.shutdown();
            GWMCrates.getInstance().getCreatedManagers().remove(manager);
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_UNLOADED", src, null,
                    new Pair<>("%MANAGER_ID%", managerId)));
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Failed to unload manager!", e);
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("MANAGER_UNLOAD_FAILED", src, null,
                    new Pair<>("%MANAGER_ID%", managerId)));
        }
        return CommandResult.success();
    }
}
