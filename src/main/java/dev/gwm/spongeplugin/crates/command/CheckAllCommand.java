package dev.gwm.spongeplugin.crates.command;

import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public final class CheckAllCommand implements CommandExecutor {

    private final Language language;

    public CheckAllCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Player player = args.<Player>getOne(Text.of("player")).get();
        boolean self = source.equals(player);
        if (self) {
            if (!source.hasPermission("gwm_crates.command.check_all")) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        } else {
            if (!source.hasPermission("gwm_crates.command.check_all_others")) {
                source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
                return CommandResult.empty();
            }
        }
        GWMCratesUtils.getManagersStream().forEach(manager -> {
            int caseAmount = manager.getCase().get(player);
            int keyAmount = manager.getKey().get(player);
            source.sendMessages(language.getTranslation("CHECK_ALL_MANAGER_INFORMATION", Arrays.asList(
                    new ImmutablePair<>("PLAYER_NAME", player.getName()),
                    new ImmutablePair<>("PLAYER_UUID", player.getUniqueId()),
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id()),
                    new ImmutablePair<>("CASE_AMOUNT", caseAmount),
                    new ImmutablePair<>("KEY_AMOUNT", keyAmount)
            ), source));
        });
        return CommandResult.success();
    }
}
