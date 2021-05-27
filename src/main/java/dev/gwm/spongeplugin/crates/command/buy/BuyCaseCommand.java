package dev.gwm.spongeplugin.crates.command.buy;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.caze.base.Case;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class BuyCaseCommand implements CommandExecutor {

    private final Language language;

    public BuyCaseCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(@NotNull CommandSource source, @NotNull CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        UUID uuid = player.getUniqueId();
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        if (amount < 1) {
            player.sendMessages(language.getTranslation("BUY_AMOUNT_IS_LESS_THAN_ONE", player));
            return CommandResult.empty();
        }
        if (!player.hasPermission("gwm_crates.command.buy." + managerId + ".case")) {
            player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
            return CommandResult.empty();
        }
        Case caze = manager.getCase();
        if (!(caze instanceof Giveable)) {
            player.sendMessages(language.getTranslation("CASE_IS_NOT_GIVEABLE", Arrays.asList(
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id())
            ), player));
            return CommandResult.empty();
        }
        Giveable giveable = (Giveable) caze;
        Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
        if (!optionalEconomyService.isPresent()) {
            player.sendMessages(language.getTranslation("ECONOMY_SERVICE_IS_NOT_FOUND", player));
            return CommandResult.empty();
        }
        EconomyService economyService = optionalEconomyService.get();
        Optional<UniqueAccount> optionalPlayerAccount = economyService.getOrCreateAccount(uuid);
        if (!optionalPlayerAccount.isPresent()) {
            player.sendMessages(language.getTranslation("ECONOMY_ACCOUNT_IS_NOT_FOUND", player));
            return CommandResult.empty();
        }
        UniqueAccount playerAccount = optionalPlayerAccount.get();
        Optional<BigDecimal> optionalPrice = giveable.getPrice();
        if (!optionalPrice.isPresent()) {
            player.sendMessages(language.getTranslation("CASE_IS_NOT_FOR_SALE", Arrays.asList(
                    new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                    new ImmutablePair<>("MANAGER_ID", manager.id())
            ), player));
            return CommandResult.empty();
        }
        BigDecimal price = optionalPrice.get();
        BigDecimal totalPrice = price.multiply(new BigDecimal(String.valueOf(amount)));
        Currency currency = giveable.getSaleCurrency().orElse(economyService.getDefaultCurrency());
        BigDecimal balance = playerAccount.getBalance(currency);
        if (balance.compareTo(totalPrice) < 0) {
            player.sendMessages(language.getTranslation("HAVE_NOT_ENOUGH_MONEY", Arrays.asList(
                    new ImmutablePair<>("CURRENCY_ID", currency.getId()),
                    new ImmutablePair<>("CURRENCY_NAME", currency.getName()),
                    new ImmutablePair<>("CURRENCY_DISPLAY_NAME", TextSerializers.FORMATTING_CODE.serialize(currency.getDisplayName())),
                    new ImmutablePair<>("CURRENCY_SYMBOL", TextSerializers.FORMATTING_CODE.serialize(currency.getSymbol())),
                    new ImmutablePair<>("REQUIRED_AMOUNT", totalPrice),
                    new ImmutablePair<>("BALANCE", balance),
                    new ImmutablePair<>("DIFFERENCE", totalPrice.subtract(balance))
            ), player));
            return CommandResult.empty();
        }
        playerAccount.withdraw(currency, totalPrice, GWMCrates.getInstance().getCause());
        giveable.give(player, amount, false);
        player.sendMessages(language.getTranslation("SUCCESSFULLY_BOUGHT_CASE", Arrays.asList(
                new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                new ImmutablePair<>("MANAGER_ID", manager.id())
        ), player));
        return CommandResult.success();
    }
}
