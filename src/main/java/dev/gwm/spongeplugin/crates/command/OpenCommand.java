package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class OpenCommand implements CommandExecutor {

    private final Language language;

    public OpenCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        UUID uuid = player.getUniqueId();
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        Case caze = manager.getCase();
        Key key = manager.getKey();
        OpenManager openManager = manager.getOpenManager();
        if (!player.hasPermission("gwm_crates.open." + managerId)) {
            GWMCratesUtils.sendNoPermissionToOpenMessage(source, manager);
            return CommandResult.empty();
        }
        if (!player.hasPermission("gwm_crates.command.open." + managerId)) {
            player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
            return CommandResult.empty();
        }
        long delay = GWMCratesUtils.getCrateOpenDelay(uuid);
        if (delay > 0L) {
            GWMCratesUtils.sendCrateDelayMessage(source, manager, delay);
            return CommandResult.empty();
        }
        if (!openManager.canOpen(player, manager)) {
            GWMCratesUtils.sendCannotOpenMessage(source, manager);
            return CommandResult.empty();
        }
        if (caze.get(player) < 1) {
            GWMCratesUtils.sendCaseMissingMessage(source, manager);
            return CommandResult.empty();
        }
        if (key.get(player) < 1) {
            GWMCratesUtils.sendKeyMissingMessage(source, manager);
            return CommandResult.empty();
        }
        caze.withdraw(player, 1, false);
        key.withdraw(player, 1, false);
        GWMCratesUtils.updateCrateOpenDelay(uuid);
        openManager.open(player, manager);
        return CommandResult.success();
    }
}
