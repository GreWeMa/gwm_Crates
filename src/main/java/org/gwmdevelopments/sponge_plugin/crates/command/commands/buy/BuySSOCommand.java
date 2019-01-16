package org.gwmdevelopments.sponge_plugin.crates.command.commands.buy;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

//Saved Super Object
public class BuySSOCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", src, null));
            return CommandResult.success();
        }
        Player player = (Player) src;
        UUID uuid = player.getUniqueId();
        SuperObject sso = args.<SuperObject>getOne(Text.of("sso")).get();
        String ssoId = sso.getId().get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        if (!(sso instanceof Giveable)) {
            src.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_GIVEABLE", src, null,
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
            return CommandResult.success();
        }
        Giveable giveable = (Giveable) sso;
        if (!player.hasPermission("gwm_crates.command.buy.sso." + ssoId)) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION", src, null));
            return CommandResult.success();
        }
        Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
        if (!optionalEconomyService.isPresent()) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("ECONOMY_SERVICE_NOT_FOUND", src, null));
            return CommandResult.success();
        }
        EconomyService economyService = optionalEconomyService.get();
        Optional<UniqueAccount> optionalPlayerAccount = economyService.getOrCreateAccount(uuid);
        if (!optionalPlayerAccount.isPresent()) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("ECONOMY_ACCOUNT_NOT_FOUND", src, null));
            return CommandResult.success();
        }
        UniqueAccount playerAccount = optionalPlayerAccount.get();
        Optional<BigDecimal> optionalPrice = giveable.getPrice();
        if (!optionalPrice.isPresent()) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SSO_IS_NOT_FOR_SALE", src, null,
                    new Pair<>("%SUPER_OBJECT%", ssoId)));
        }
        BigDecimal price = optionalPrice.get();
        BigDecimal totalPrice = price.multiply(new BigDecimal(String.valueOf(amount)));
        Currency currency = giveable.getSellCurrency().orElse(economyService.getDefaultCurrency());
        BigDecimal balance = playerAccount.getBalance(currency);
        if (balance.compareTo(totalPrice) < 0) {
            player.sendMessage(GWMCrates.getInstance().getLanguage().getText("NOT_ENOUGH_MONEY", src, null));
            return CommandResult.success();
        }
        playerAccount.withdraw(currency, totalPrice, GWMCrates.getInstance().getCause());
        giveable.give(player, amount, false);
        player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_BOUGHT_SSO", src, null,
                new Pair<>("%SUPER_OBJECT%", ssoId)));
        return CommandResult.success();
    }
}
