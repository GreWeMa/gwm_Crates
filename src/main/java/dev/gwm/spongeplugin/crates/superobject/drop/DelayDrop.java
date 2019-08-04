package dev.gwm.spongeplugin.crates.drop.drops;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.drop.Drop;
import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class DelayDrop extends Drop {

    public static final String TYPE = "DELAY";

    private final Drop childDrop;
    private final long delay;

    public DelayDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childDropNode = node.getNode("CHILD_DROP");
            ConfigurationNode delayNode = node.getNode("DELAY");
            if (childDropNode.isVirtual()) {
                throw new IllegalArgumentException("CHILD_DROP node does not exist!");
            }
            if (delayNode.isVirtual()) {
                throw new IllegalArgumentException("DELAY node does not exist!");
            }
            childDrop = (Drop) GWMCratesUtils.createSuperObject(childDropNode, SuperObjectType.DROP);
            delay = delayNode.getLong();
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public DelayDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview, Optional<Integer> level, Optional<Integer> fakeLevel, Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels, Optional<Long> weight, Optional<Long> fakeWeight, Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights,
                     Drop childDrop, long delay) {
        super(id, price, sellCurrency, dropItem, customName, showInPreview, level, fakeLevel, permissionLevels, permissionFakeLevels, weight, fakeWeight, permissionWeights, permissionFakeWeights);
        this.childDrop = childDrop;
        this.delay = delay;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).
                execute(() -> childDrop.give(player, amount)).
                submit(GWMCrates.getInstance());
    }

    public Drop getChildDrop() {
        return childDrop;
    }

    public long getDelay() {
        return delay;
    }
}
