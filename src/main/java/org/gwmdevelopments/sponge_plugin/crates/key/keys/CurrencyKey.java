package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CurrencyKey extends GiveableKey {

    private BigDecimal amount;
    private Currency currency;

    public CurrencyKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            ConfigurationNode currencyNode = node.getNode("CURRENCY");
            if (amountNode.isVirtual()) {
                throw new RuntimeException("AMOUNT node does not exist!");
            }
            if (currencyNode.isVirtual()) {
                throw new RuntimeException("CURRENCY node does not exist!");
            }
            amount = new BigDecimal(amountNode.getString());
            String currencyName = currencyNode.getString();
            Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new RuntimeException("Economy Service not found!");
            }
            EconomyService economyService = optionalEconomyService.get();
            boolean found = false;
            for (Currency currency : economyService.getCurrencies()) {
                if (currency.getId().equals(currencyName)) {
                    this.currency = currency;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Currency \"" + currencyName + "\" not found!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Currency Key!", e);
        }
    }

    public CurrencyKey(String type, Optional<String> id, boolean doNotWithdraw,
                       Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                       Currency currency, BigDecimal amount) {
        super(type, id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new RuntimeException("Economy Service not found!");
            }
            EconomyService economyService = optionalEconomyService.get();
            Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
            if (!optionalAccount.isPresent()) {
                return;
            }
            UniqueAccount account = optionalAccount.get();
            BigDecimal balance = account.getBalance(currency);
            BigDecimal value = balance.subtract(this.amount.multiply(new BigDecimal(Integer.toString(amount))));
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                value = BigDecimal.ZERO;
            }
            account.setBalance(currency,  value, GWMCrates.getInstance().getCause());
        }
    }

    @Override
    public int get(Player player) {
        Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
        if (!optionalEconomyService.isPresent()) {
            throw new RuntimeException("Economy Service not found!");
        }
        EconomyService economyService = optionalEconomyService.get();
        Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
        if (!optionalAccount.isPresent()) {
            return 0;
        }
        UniqueAccount account = optionalAccount.get();
        BigDecimal balance = account.getBalance(currency);
        int value = balance.divide(amount, RoundingMode.FLOOR).intValue();
        return value > 0 ? value : 0;
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new RuntimeException("Economy Service not found!");
            }
            EconomyService economyService = optionalEconomyService.get();
            Optional<UniqueAccount> optionalAccount = economyService.getOrCreateAccount(player.getUniqueId());
            if (!optionalAccount.isPresent()) {
                return;
            }
            UniqueAccount account = optionalAccount.get();
            BigDecimal balance = account.getBalance(currency);
            BigDecimal value = balance.add(this.amount.multiply(new BigDecimal(Integer.toString(amount))));
            account.setBalance(currency, value, GWMCrates.getInstance().getCause());
        }
    }
}
