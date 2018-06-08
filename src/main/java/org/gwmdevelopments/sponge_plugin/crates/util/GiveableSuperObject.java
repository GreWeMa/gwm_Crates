package org.gwmdevelopments.sponge_plugin.crates.util;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class GiveableSuperObject extends SuperObject implements Giveable {

    private Optional<BigDecimal> price = Optional.empty();
    private Optional<Currency> sellCurrency = Optional.empty();

    public GiveableSuperObject(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode priceNode = node.getNode("PRICE");
            ConfigurationNode sellCurrencyNode = node.getNode("SELL_CURRENCY");
            if (!priceNode.isVirtual()) {
                price = Optional.of(new BigDecimal(priceNode.getString()));
            }
            if (!sellCurrencyNode.isVirtual()) {
                String sellCurrencyName = sellCurrencyNode.getString();
                Optional<EconomyService> optionalEconomyService = GWMCrates.getInstance().getEconomyService();
                if (!optionalEconomyService.isPresent()) {
                    throw new RuntimeException("Economy Service not found, but parameter \"SELL_CURRENCY\" specified!");
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
                    throw new RuntimeException("Currency \"" + sellCurrencyName + "\" not found!");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Giveable Super Object!", e);
        }
    }

    public GiveableSuperObject(String type, Optional<String> id,
                               Optional<BigDecimal> price, Optional<Currency> sellCurrency) {
        super(type, id);
        this.price = price;
        this.sellCurrency = sellCurrency;
    }

    public abstract void give(Player player, int amount);

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }

    @Override
    public Optional<Currency> getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(Optional<Currency> sellCurrency) {
        this.sellCurrency = sellCurrency;
    }
}
