package org.gwmdevelopments.sponge_plugin.crates.command.commands.give;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class GiveDropCommand implements CommandExecutor  {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        String dropId = args.<String>getOne(Text.of("drop")).get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean self = src.equals(player);
        Optional<Drop> optionalDrop = manager.getDropById(dropId);
        if (!optionalDrop.isPresent()) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("DROP_NOT_EXIST",
                    new Pair<>("%DROP%", dropId)));
            return CommandResult.success();
        }
        Drop drop = optionalDrop.get();
        if (self) {
            if (!player.hasPermission("gwm_crates.command.give.manager." + managerId + ".drop." + dropId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.give_others.manager." + managerId + ".drop." + dropId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        }
        drop.give(player, amount);
        if (self) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_DROP",
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%DROP%", dropId)));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_DROP",
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%DROP%", dropId),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
