package dev.gwm.spongeplugin.crates.command.give;

import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class GiveEveryoneKeyCommand implements CommandExecutor {

    private final Language language;

    public GiveEveryoneKeyCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.hasAny("f");
        if (!source.hasPermission("gwm_crates.command.give_everyone." + managerId + ".key")) {
            source.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", source));
            return CommandResult.empty();
        }
        Key key = manager.getKey();
        if (!(key instanceof Giveable)) {
            source.sendMessages(language.getTranslation("KEY_IS_NOT_GIVEABLE", Arrays.asList(
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.id())
            ), source));
            return CommandResult.empty();
        }
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            ((Giveable) key).give(player, amount, force);
            if (source.equals(player)) {
                source.sendMessages(language.getTranslation("SUCCESSFULLY_GOT_KEY", Arrays.asList(
                        new Pair<>("MANAGER_NAME", manager.getName()),
                        new Pair<>("MANAGER_ID", manager.id())
                ), source));
            } else {
                source.sendMessages(language.getTranslation("SUCCESSFULLY_GAVE_KEY", Arrays.asList(
                        new Pair<>("MANAGER_NAME", manager.getName()),
                        new Pair<>("MANAGER_ID", manager.id()),
                        new Pair<>("PLAYER_NAME", player.getName()),
                        new Pair<>("PLAYER_UUID", player.getUniqueId())
                ), source));
            }
        });
        return CommandResult.success();
    }
}
