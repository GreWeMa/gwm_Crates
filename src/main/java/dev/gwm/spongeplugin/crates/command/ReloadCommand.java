package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.library.utils.Language;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ReloadCommand implements CommandExecutor {

    private final Language language;

    public ReloadCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        GWMCrates.getInstance().reload();
        source.sendMessages(language.getTranslation("SUCCESSFULLY_RELOADED", source));
        return CommandResult.success();
    }
}
