package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.Preview;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Optional;

public class PreviewCommand implements CommandExecutor {

    private final Language language;

    public PreviewCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = source.equals(player);
        if (self) {
            if (!source.hasPermission("gwm_crates.preview." + managerId)) {
                GWMCratesUtils.sendNoPermissionToPreviewMessage(source, manager);
                return CommandResult.empty();
            }
            if (!source.hasPermission("gwm_crates.command.preview." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.preview_others." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        Optional<Preview> optionalPreview = manager.getPreview();
        if (!optionalPreview.isPresent()) {
            GWMCratesUtils.sendPreviewNotAvailableMessage(source, manager);
            return CommandResult.empty();
        }
        Preview preview = optionalPreview.get();
        preview.preview(player, manager);
        if (self) {
            source.sendMessages(language.getTranslation("PREVIEW_STARTED", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.getId())
            ), source));
        } else {
            source.sendMessages(language.getTranslation("PREVIEW_STARTED_FOR_PLAYER", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.getId()),
                    new Pair<>("PLAYER_NAME", player.getName()),
                    new Pair<>("PLAYER_UUID", player.getUniqueId())
            ), source));
        }
        return CommandResult.success();
    }
}
