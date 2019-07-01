package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class EmptyDrop extends Drop {

    public static final String TYPE = "EMPTY";

    public EmptyDrop(ConfigurationNode node) {
        super(node);
    }

    public EmptyDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview, Optional<Integer> level, Optional<Integer> fakeLevel, Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels, Optional<Long> weight, Optional<Long> fakeWeight, Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights) {
        super(id, price, sellCurrency, dropItem, customName, showInPreview, level, fakeLevel, permissionLevels, permissionFakeLevels, weight, fakeWeight, permissionWeights, permissionFakeWeights);
    }

    public EmptyDrop() {
        super(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), false, Optional.empty(), Optional.empty(), Collections.emptyMap(), Collections.emptyMap(), Optional.empty(), Optional.empty(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
    }
}
