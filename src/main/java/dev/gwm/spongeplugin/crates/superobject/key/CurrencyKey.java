package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.GiveableKey;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public final class CurrencyKey extends GiveableKey {

    public static final String TYPE = "CURRENCY";

    private final BigDecimal amount;
    private final Optional<Currency> currency;

    public CurrencyKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            ConfigurationNode currencyNode = node.getNode("CURRENCY");
            if (amountNode.isVirtual()) {
                throw new IllegalArgumentException("AMOUNT node does not exist!");
            }
            amount = new BigDecimal(amountNode.getString());
            if (!currencyNode.isVirtual()) {
                String currencyId = currencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new IllegalArgumentException("Economy Service not found!");
                }
                currency = GWMCratesUtils.getCurrencyById(optionalEconomyService.get(), currencyId);
                if (!currency.isPresent()) {
                    throw new IllegalArgumentException("Currency \"" + currencyId + "\" not found!");
                }
            } else {
                currency = Optional.empty();
            }

        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public CurrencyKey(Optional<String> id, boolean doNotWithdraw,
                       Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                       BigDecimal amount, Optional<Currency> currency) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.amount = amount;
        this.currency = currency;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }
}
