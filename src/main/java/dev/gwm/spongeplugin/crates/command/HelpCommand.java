package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class HelpCommand implements CommandExecutor {

    private final Language language;

    public HelpCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        source.sendMessages(language.getTranslation("HELP_MESSAGE",
                new Pair<>("VERSION", GWMCrates.VERSION.toString()),
                source));
        return CommandResult.success();
    }
}
