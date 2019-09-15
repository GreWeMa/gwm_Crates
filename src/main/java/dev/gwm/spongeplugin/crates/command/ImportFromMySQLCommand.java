package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.sql.SQLException;

public class ImportFromMySQLCommand implements CommandExecutor {

    private final Language language;

    public ImportFromMySQLCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        boolean async = args.hasAny("a");
        source.sendMessages(language.getTranslation("STARTING_IMPORT_FROM_MYSQL", source));
        if (async) {
            GWMCratesUtils.asyncImportFromMySQL();
            return CommandResult.success();
        } else {
            try {
                long time = GWMCratesUtils.importFromMySQL();
                source.sendMessages(language.getTranslation("IMPORT_FROM_MYSQL_SUCCESSFUL",
                        new Pair<>("TIME", GWMCratesUtils.millisToString(time)),
                        source));
                return CommandResult.success();
            } catch (SQLException e) {
                source.sendMessages(language.getTranslation("IMPORT_FROM_MYSQL_FAILED", source));
                GWMCrates.getInstance().getLogger().error("Import from MySQL failed!", e);
                return CommandResult.empty();
            }
        }
    }
}
