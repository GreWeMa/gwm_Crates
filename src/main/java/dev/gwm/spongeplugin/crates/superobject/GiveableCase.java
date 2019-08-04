package dev.gwm.spongeplugin.crates.caze;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class GiveableCase extends Case implements Giveable {

    private final Optional<BigDecimal> price;
    private final Optional<Currency> sellCurrency;
    private final boolean doNotAdd;

    public GiveableCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            ConfigurationNode doNotAddNode = node.getNode("DO_NOT_ADD");
            if (!priceNode.isVirtual()) {
                price = Optional.of(new BigDecimal(priceNode.getString()));
            } else {
                price = Optional.empty();
            }
            if (!sellCurrencyNode.isVirtual()) {
                String sellCurrencyId = sellCurrencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new IllegalArgumentException("Economy Service not found, but parameter \"SELL_CURRENCY\" specified!");
                }
                sellCurrency = GWMCratesUtils.getCurrencyById(optionalEconomyService.get(), sellCurrencyId);
                if (!sellCurrency.isPresent()) {
                    throw new IllegalArgumentException("Currency \"" + sellCurrencyId + "\" not found!");
                }
            } else {
                sellCurrency = Optional.empty();
            }
            doNotAdd = doNotAddNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public GiveableCase(Optional<String> id, boolean doNotWithdraw,
                        Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd) {
        super(id, doNotWithdraw);
        this.price = price;
        this.sellCurrency = sellCurrency;
        this.doNotAdd = doNotAdd;
    }

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    @Override
    public Optional<Currency> getSellCurrency() {
        return sellCurrency;
    }

    public boolean isDoNotAdd() {
        return doNotAdd;
    }
}
