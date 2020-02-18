package dev.gwm.spongeplugin.crates.command.give;

import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public final class GiveEveryoneCaseCommand implements CommandExecutor {

    private final Language language;

    public GiveEveryoneCaseCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.hasAny("f");
        if (!source.hasPermission("gwm_crates.command.give_everyone." + managerId + ".case")) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        Case caze = manager.getCase();
        if (!(caze instanceof Giveable)) {
            source.sendMessages(language.getTranslation("CASE_IS_NOT_GIVEABLE", Arrays.asList(
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id())
            ), source));
            return CommandResult.empty();
        }
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            ((Giveable) caze).give(player, amount, force);
            if (source.equals(player)) {
                source.sendMessages(language.getTranslation("SUCCESSFULLY_GOT_CASE", Arrays.asList(
                        new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                        new ImmutablePair<>("MANAGER_ID", manager.id())
                ), source));
            } else {
                source.sendMessages(language.getTranslation("SUCCESSFULLY_GAVE_CASE", Arrays.asList(
                        new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                        new ImmutablePair<>("MANAGER_ID", manager.id()),
                        new ImmutablePair<>("PLAYER_NAME", player.getName()),
                        new ImmutablePair<>("PLAYER_UUID", player.getUniqueId())
                ), source));
            }
        });
        return CommandResult.success();
    }
}
