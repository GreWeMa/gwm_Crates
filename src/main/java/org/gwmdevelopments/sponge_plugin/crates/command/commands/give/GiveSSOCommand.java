package org.gwmdevelopments.sponge_plugin.crates.command.commands.give;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

//Saved Super Object
public class GiveSSOCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        SuperObject sso = args.<SuperObject>getOne(Text.of("sso")).get();
        String ssoId = sso.getId().get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean self = src.equals(player);
        if (!(sso instanceof Giveable)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_GIVEABLE",
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
            return CommandResult.success();
        }
        Giveable giveable = (Giveable) sso;
        if (self) {
            if (!player.hasPermission("gwm_crates.command.give.sso." + ssoId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.give_others.sso." + ssoId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                return CommandResult.success();
            }
        }
        giveable.give(player, amount);
        if (self) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_SSO",
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_SSO",
                    new Pair<>("%SUPER_OBJECT%", ssoId),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
