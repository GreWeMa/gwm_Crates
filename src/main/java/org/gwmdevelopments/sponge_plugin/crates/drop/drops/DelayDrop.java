package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.AbstractDrop;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DelayDrop extends AbstractDrop {

    private Drop childDrop;
    private long delay;

    public DelayDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childDropNode = node.getNode("CHILD_DROP");
            ConfigurationNode delayNode = node.getNode("DELAY");
            if (childDropNode.isVirtual()) {
                throw new RuntimeException("CHILD_DROP node does not exist!");
            }
            childDrop = (Drop) GWMCratesUtils.createSuperObject(childDropNode, SuperObjectType.DROP);
            if (delayNode.isVirtual()) {
                throw new RuntimeException("DELAY node does not exist!");
            }
            delay = delayNode.getLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Delay Drop!", e);
        }
    }

    public DelayDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                     int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                     Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                     Drop childDrop, int delay) {
        super("DELAY", id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
        this.childDrop = childDrop;
        this.delay = delay;
    }

    public Drop getChildDrop() {
        return childDrop;
    }

    public void setChildDrop(Drop childDrop) {
        this.childDrop = childDrop;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void give(Player player, int amount) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).
                execute(() -> childDrop.give(player, amount)).
                submit(GWMCrates.getInstance());
    }
}
