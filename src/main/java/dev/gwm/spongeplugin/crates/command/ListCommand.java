package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
                new ImmutablePair<>("MANAGERS", formattedManagers),
                source));
        return CommandResult.success();
    }
}
