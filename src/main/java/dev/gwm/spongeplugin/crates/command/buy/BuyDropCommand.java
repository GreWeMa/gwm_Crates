package dev.gwm.spongeplugin.crates.command.buy;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
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

public class BuyDropCommand implements CommandExecutor {

    private final Language language;

    public BuyDropCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        UUID uuid = player.getUniqueId();
        Manager manager = args.<Manager>getOne(Text.of("manager")).get();
        String managerId = manager.id();
        String dropId = args.<String>getOne(Text.of("drop")).get();
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        Optional<Drop> optionalDrop = manager.getDropById(dropId);
        if (!optionalDrop.isPresent()) {
            player.sendMessages(language.getTranslation("DROP_IS_NOT_FOUND", Arrays.asList(
                    new Pair<>("DROP_ID", dropId),
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.id())
            ), player));
            return CommandResult.empty();
        }
        Drop drop = optionalDrop.get();
        String dropCustomName = drop.getCustomName().orElse(dropId);
        if (!player.hasPermission("gwm_crates.command.buy." + managerId + ".drop." + dropId)) {
            player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
            return CommandResult.empty();
        }
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
        Optional<BigDecimal> optionalPrice = drop.getPrice();
        if (!optionalPrice.isPresent()) {
            player.sendMessages(language.getTranslation("DROP_IS_NOT_FOR_SALE", Arrays.asList(
                    new Pair<>("DROP_ID", dropId),
                    new Pair<>("DROP_CUSTOM_NAME", dropCustomName),
                    new Pair<>("MANAGER_NAME", manager.getName()),
                    new Pair<>("MANAGER_ID", manager.id())
            ), player));
            return CommandResult.empty();
        }
        BigDecimal price = optionalPrice.get();
        BigDecimal totalPrice = price.multiply(new BigDecimal(String.valueOf(amount)));
        Currency currency = drop.getSaleCurrency().orElse(economyService.getDefaultCurrency());
        BigDecimal balance = playerAccount.getBalance(currency);
        if (balance.compareTo(totalPrice) < 0) {
            player.sendMessages(language.getTranslation("HAVE_NOT_ENOUGH_MONEY", Arrays.asList(
                    new Pair<>("CURRENCY_ID", currency.getId()),
                    new Pair<>("CURRENCY_NAME", currency.getName()),
                    new Pair<>("CURRENCY_DISPLAY_NAME", TextSerializers.FORMATTING_CODE.serialize(currency.getDisplayName())),
                    new Pair<>("CURRENCY_SYMBOL", TextSerializers.FORMATTING_CODE.serialize(currency.getSymbol())),
                    new Pair<>("REQUIRED_AMOUNT", totalPrice),
                    new Pair<>("BALANCE", balance),
                    new Pair<>("DIFFERENCE", totalPrice.subtract(balance))
            ), player));
            return CommandResult.empty();
        }
        playerAccount.withdraw(currency, totalPrice, GWMCrates.getInstance().getCause());
        drop.give(player, amount);
        player.sendMessages(language.getTranslation("SUCCESSFULLY_BOUGHT_DROP", Arrays.asList(
                new Pair<>("DROP_ID", dropId),
                new Pair<>("DROP_CUSTOM_NAME", dropCustomName),
                new Pair<>("MANAGER_NAME", manager.getName()),
                new Pair<>("MANAGER_ID", manager.id())
        ), player));
        return CommandResult.success();
    }
}
