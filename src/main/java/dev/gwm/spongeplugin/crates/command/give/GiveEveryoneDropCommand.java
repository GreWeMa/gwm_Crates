package dev.gwm.spongeplugin.crates.command.commands.give;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class GiveEveryoneDropCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        String dropId = args.<String>getOne(Text.of("drop")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        Optional<Drop> optionalDrop = manager.getDropById(dropId);
        if (!optionalDrop.isPresent()) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("DROP_NOT_EXIST", src, null,
                    new Pair<>("%DROP%", dropId)));
            return CommandResult.success();
        }
        Drop drop = optionalDrop.get();
        if (!src.hasPermission("gwm_crates.command.give_everyone.manager." + managerId + ".drop." + dropId)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            drop.give(player, amount);
            if (src.equals(player)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_DROP", src, null,
                        new Pair<>("%MANAGER%", manager.getName()),
                        new Pair<>("%DROP%", dropId)));
            } else {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_DROP", src, null,
                        new Pair<>("%MANAGER%", manager.getName()),
                        new Pair<>("%DROP%", dropId),
                        new Pair<>("%PLAYER%", player.getName())));
            }
        });
        return CommandResult.success();
    }
}
