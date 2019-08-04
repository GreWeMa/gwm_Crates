package dev.gwm.spongeplugin.crates.command.commands.give;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.util.Giveable;
import dev.gwm.spongeplugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

//Saved Super Object
public class GiveSSOCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        SuperObject sso = args.<SuperObject>getOne(Text.of("sso")).get();
        String ssoId = sso.id().get();
        Player player = args.<Player>getOne(Text.of("player")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        boolean force = args.<Boolean>getOne(Text.of("force")).orElse(false);
        boolean self = src.equals(player);
        if (self) {
            if (!player.hasPermission("gwm_crates.command.give.sso." + ssoId)) {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        } else {
            if (!src.hasPermission("gwm_crates.command.give_others.sso." + ssoId)) {
                src.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
                return CommandResult.success();
            }
        }
        if (!(sso instanceof Giveable)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_GIVEABLE", src, null,
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
            return CommandResult.success();
        }
        ((Giveable) sso).give(player, amount, force);
        if (self) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GOT_SSO", src, null,
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
        } else {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_GAVE_SSO", src, null,
                    new Pair<>("%SUPER_OBJECT%", ssoId),
                    new Pair<>("%PLAYER%", player.getName())));
        }
        return CommandResult.success();
    }
}
