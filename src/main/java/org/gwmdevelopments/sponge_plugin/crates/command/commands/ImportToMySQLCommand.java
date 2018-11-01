package org.gwmdevelopments.sponge_plugin.crates.command.commands;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.sql.SQLException;

public class ImportToMySQLCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        boolean async = args.hasAny("a");
        src.sendMessage(GWMCrates.getInstance().getLanguage().getText("STARTING_IMPORT_TO_MYSQL"));
        if (async) {
            GWMCratesUtils.asyncImportToMySQL();
        } else {
            try {
                long time = GWMCratesUtils.importToMySQL();
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("IMPORT_TO_MYSQL_SUCCESSFUL",
                        new Pair<>("%TIME%", GWMCratesUtils.millisToString(time))));
            } catch (SQLException e) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("IMPORT_TO_MYSQL_FAILED"));
                GWMCrates.getInstance().getLogger().warn("Async import to MySQL failed!", e);
            }
        }
        return CommandResult.success();
    }
}
