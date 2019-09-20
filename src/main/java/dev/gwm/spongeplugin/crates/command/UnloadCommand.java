package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class UnloadCommand implements CommandExecutor {

    private final Language language;

    public UnloadCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        if (!source.hasPermission("gwm_crates.command.unload." + managerId)) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        try {
            Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                    shutdownCreatedSuperObject(manager);
            source.sendMessages(language.getTranslation("MANAGER_UNLOADED", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", managerId)
            ), source));
            return CommandResult.success();
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().error("Failed to unload a manager!", e);
            source.sendMessages(language.getTranslation("MANAGER_UNLOAD_FAILED", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", managerId)
            ), source));
            return CommandResult.empty();
        }
    }
}
