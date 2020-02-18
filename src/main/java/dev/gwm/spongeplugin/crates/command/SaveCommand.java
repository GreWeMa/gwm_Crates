package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.util.Language;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public final class SaveCommand implements CommandExecutor {

    private final Language language;

    public SaveCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        GWMCrates.getInstance().save();
        source.sendMessages(language.getTranslation("SUCCESSFULLY_SAVED", source));
        return CommandResult.success();
    }
}
