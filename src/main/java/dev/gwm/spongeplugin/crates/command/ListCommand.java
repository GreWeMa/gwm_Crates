package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {

    private final Language language;

    public ListCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        String formattedManagers = GWMCratesUtils.formatManagers(GWMCratesUtils.getManagersStream().collect(Collectors.toList()));
        source.sendMessages(language.getTranslation("MANAGERS_LIST",
                new Pair<>("MANAGERS", formattedManagers),
                source));
        return CommandResult.success();
    }
}
