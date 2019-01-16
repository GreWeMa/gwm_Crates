package org.gwmdevelopments.sponge_plugin.crates.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class GiveableKey extends AbstractKey implements Giveable {

    private Optional<BigDecimal> price = Optional.empty();
    private Optional<Currency> sellCurrency = Optional.empty();
    private boolean doNotAdd;

    public GiveableKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            ConfigurationNode doNotAddNode = node.getNode("DO_NOT_ADD");
            if (!priceNode.isVirtual()) {
                price = Optional.of(new BigDecimal(priceNode.getString()));
            }
            if (!sellCurrencyNode.isVirtual()) {
                String sellCurrencyName = sellCurrencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new IllegalArgumentException("Economy Service not found, but parameter \"SELL_CURRENCY\" specified!");
                }
                EconomyService economyService = optionalEconomyService.get();
                boolean found = false;
                for (Currency currency : economyService.getCurrencies()) {
                    if (currency.getId().equals(sellCurrencyName)) {
                        sellCurrency = Optional.of(currency);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("Currency \"" + sellCurrencyName + "\" not found!");
                }
            }
            doNotAdd = doNotAddNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Giveable Key!", e);
        }
    }

    public GiveableKey(String type, Optional<String> id, boolean doNotWithdraw,
                       Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd) {
        super(type, id, doNotWithdraw);
        this.price = price;
        this.sellCurrency = sellCurrency;
        this.doNotAdd = doNotAdd;
    }

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    protected void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }

    @Override
    public Optional<Currency> getSellCurrency() {
        return sellCurrency;
    }

    protected void setSellCurrency(Optional<Currency> sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public boolean isDoNotAdd() {
        return doNotAdd;
    }

    public void setDoNotAdd(boolean doNotAdd) {
        this.doNotAdd = doNotAdd;
    }
}
