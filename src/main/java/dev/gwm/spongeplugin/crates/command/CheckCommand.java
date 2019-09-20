package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class CheckCommand implements CommandExecutor {

    private final Language language;

    public CheckCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = source.equals(player);
        if (self) {
            if (!source.hasPermission("gwm_crates.command.check." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.check_others." + managerId)) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        int caseAmount = manager.getCase().get(player);
        int keyAmount = manager.getKey().get(player);
        source.sendMessages(language.getTranslation("CHECK_MANAGER_INFORMATION", Arrays.asList(
                new Pair<>("PLAYER_NAME", player.getName()),
                new Pair<>("PLAYER_UUID", player.getUniqueId()),
                new Pair<>("MANAGER_NAME", manager.getName()),
                new Pair<>("MANAGER_ID", manager.id()),
                new Pair<>("CASE_AMOUNT", caseAmount),
                new Pair<>("KEY_AMOUNT", keyAmount)
        ), source));
        return CommandResult.success();
    }
}
