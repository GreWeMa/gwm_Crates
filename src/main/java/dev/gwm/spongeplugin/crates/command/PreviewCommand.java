package dev.gwm.spongeplugin.crates.command.commands;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.Preview;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class PreviewCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.preview." + managerId) ||
                    !player.hasPermission("gwm_crates.command.preview." + managerId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.preview_others." + managerId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        }
        Optional<Preview> optionalPreview = manager.getPreview();
        if (!optionalPreview.isPresent()) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_NOT_AVAILABLE", src, null,
                    new Pair<>("%MANAGER%", manager.getName())));
            return CommandResult.success();
        }
        Preview preview = optionalPreview.get();
        preview.preview(player, manager);
        if (self) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_STARTED", src, null,
                    new Pair<>("%MANAGER%", manager.getName())));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("PREVIEW_STARTED_FOR_PLAYER", src, null,
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
