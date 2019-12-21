package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.GWMCratesMySqlUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.sql.SQLException;

public class ExportToMySQLCommand implements CommandExecutor {

    private final Language language;

    public ExportToMySQLCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        boolean async = args.hasAny("a");
        source.sendMessages(language.getTranslation("STARTING_EXPORT_TO_MYSQL", source));
        if (async) {
            GWMCratesMySqlUtils.asyncExportData(GWMCrates.getInstance().getDataSource().get());
            return CommandResult.success();
        } else {
            try {
                long time = GWMCratesMySqlUtils.exportData(GWMCrates.getInstance().getDataSource().get());
                source.sendMessages(language.getTranslation("EXPORT_TO_MYSQL_SUCCESSFUL",
                        new ImmutablePair<>("TIME", GWMCratesUtils.millisToString(time)),
                        source));
                return CommandResult.success();
            } catch (SQLException e) {
                source.sendMessages(language.getTranslation("EXPORT_TO_MYSQL_FAILED", source));
                GWMCrates.getInstance().getLogger().error("Export to MySQL failed!", e);
                return CommandResult.empty();
            }
        }
    }
}
