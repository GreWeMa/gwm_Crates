package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.io.File;

public class LoadCommand implements CommandExecutor {

    private final Language language;

    public LoadCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        String path = args.<String>getOne(Text.of("path")).get();
        File file = new File(GWMCrates.getInstance().getManagersDirectory(), path);
        if (!file.exists()) {
            source.sendMessages(language.getTranslation("FILE_IS_NOT_FOUND",
                    new ImmutablePair<>("PATH", GWMCratesUtils.getManagerRelativePath(file)),
                    source));
            return CommandResult.empty();
        }
        try {
            GWMCratesUtils.loadManager(file, true);
            source.sendMessages(language.getTranslation("MANAGER_LOADED",
                    new ImmutablePair<>("PATH", GWMCratesUtils.getManagerRelativePath(file)),
                    source));
            return CommandResult.success();
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().error("Failed to load a manager!", e);
            source.sendMessages(language.getTranslation("MANAGER_LOAD_FAILED",
                    new ImmutablePair<>("PATH", file.getAbsolutePath()),
                    source));
            return CommandResult.empty();
        }
    }
}
