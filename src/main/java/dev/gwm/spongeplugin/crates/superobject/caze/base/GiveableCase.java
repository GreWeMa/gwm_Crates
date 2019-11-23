package dev.gwm.spongeplugin.crates.superobject.caze.base;

import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class GiveableCase extends AbstractCase implements Giveable {

    private final Optional<Currency> saleCurrency;
    private final Optional<BigDecimal> price;
    private final boolean doNotAdd;

    public GiveableCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode doNotAddNode = node.getNode("DO_NOT_ADD");
            GiveableData giveableData = new GiveableData(node);
            saleCurrency = giveableData.getSaleCurrency();
            price = giveableData.getPrice();
            doNotAdd = doNotAddNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public GiveableCase(String id, boolean doNotWithdraw,
                        GiveableData giveableData, boolean doNotAdd) {
        super(id, doNotWithdraw);
        this.saleCurrency = giveableData.getSaleCurrency();
        this.price = giveableData.getPrice();
        this.doNotAdd = doNotAdd;
    }

    @Override
    public Optional<Currency> getSaleCurrency() {
        return saleCurrency;
    }

    @Override
    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public boolean isDoNotAdd() {
        return doNotAdd;
    }
}
