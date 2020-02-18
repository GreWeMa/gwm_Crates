package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public final class HelpCommand implements CommandExecutor {

    private final Language language;

    public HelpCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        source.sendMessages(language.getTranslation("HELP_MESSAGE",
                new ImmutablePair<>("VERSION", GWMCrates.VERSION.toString()),
                source));
        return CommandResult.success();
    }
}
