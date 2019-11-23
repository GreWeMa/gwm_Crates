package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public final class CurrencyKey extends GiveableKey {

    public static final String TYPE = "CURRENCY";

    private final Optional<Currency> currency;
    private final BigDecimal amount;

    public CurrencyKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode currencyNode = node.getNode("CURRENCY");
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            if (amountNode.isVirtual()) {
                throw new IllegalArgumentException("AMOUNT node does not exist!");
            }
            if (!currencyNode.isVirtual()) {
                String currencyId = currencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new IllegalArgumentException("Economy Service is not found!");
                }
                currency = GWMLibraryUtils.getCurrencyById(optionalEconomyService.get(), currencyId);
                if (!currency.isPresent()) {
                    throw new IllegalArgumentException("Currency \"" + currencyId + "\" is not found!");
                }
            } else {
                currency = Optional.empty();
            }
            amount = new BigDecimal(amountNode.getString());
            if (amount.compareTo(BigDecimal.ZERO) < 1) {
                throw new IllegalArgumentException("Amount is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public CurrencyKey(String id, boolean doNotWithdraw,
                       GiveableData giveableData, boolean doNotAdd,
                       Optional<Currency> currency, BigDecimal amount) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            throw new IllegalArgumentException("Amount is equal to or less than 0!");
        }
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new IllegalStateException("Economy Service not found!");
            }
            EconomyService economyService = optionalEconomyService.get();
            Currency realCurrency = currency.orElse(economyService.getDefaultCurrency());
            Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
            if (!optionalAccount.isPresent()) {
                return;
            }
            UniqueAccount account = optionalAccount.get();
            BigDecimal balance = account.getBalance(realCurrency);
            BigDecimal value = balance.subtract(this.amount.multiply(new BigDecimal(Integer.toString(amount))));
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                value = BigDecimal.ZERO;
            }
            account.setBalance(realCurrency,  value, GWMCrates.getInstance().getCause());
        }
    }

    @Override
    public int get(Player player) {
        Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
        if (!optionalEconomyService.isPresent()) {
            throw new IllegalStateException("Economy Service not found!");
        }
        EconomyService economyService = optionalEconomyService.get();
        Currency realCurrency = currency.orElse(economyService.getDefaultCurrency());
        Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
        if (!optionalAccount.isPresent()) {
            return 0;
        }
        UniqueAccount account = optionalAccount.get();
        BigDecimal balance = account.getBalance(realCurrency);
        int value = balance.divide(amount, RoundingMode.FLOOR).intValue();
        return value > 0 ? value : 0;
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new IllegalStateException("Economy Service not found!");
            }
            EconomyService economyService = optionalEconomyService.get();
            Currency realCurrency = currency.orElse(economyService.getDefaultCurrency());
            Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
            if (!optionalAccount.isPresent()) {
                return;
            }
            UniqueAccount account = optionalAccount.get();
            BigDecimal balance = account.getBalance(realCurrency);
            BigDecimal value = balance.add(this.amount.multiply(new BigDecimal(Integer.toString(amount))));
            account.setBalance(realCurrency, value, GWMCrates.getInstance().getCause());
        }
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
