package dev.gwm.spongeplugin.crates.command.commands.give;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.Key;
import dev.gwm.spongeplugin.crates.util.Giveable;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class GiveKeyCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.getId();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.<Boolean>getOne(Text.of("force")).orElse(false);
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.command.give.manager." + managerId + ".key")) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.give_others.manager." + managerId + ".key")) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        }
        Key key = manager.getKey();
        if (!(key instanceof Giveable)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_GIVEABLE", src, null,
                    new Pair<>("%SUPER_OBJECT%", key)));
            return CommandResult.success();
        }
        ((Giveable) key).give(player, amount, force);
        if (self) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_KEY", src, null,
                    new Pair<>("%MANAGER%", manager.getName())));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_KEY", src, null,
                    new Pair<>("%MANAGER%", manager.getName()),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
