package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.RandomManager;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.random_managers.LevelRandomManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.*;

public final class MultiDrop extends Drop {

    public static final String TYPE = "MULTI";

    private final List<Drop> drops;
    private final boolean giveAll;
    private final boolean prefetch;
    private final RandomManager randomManager;

    public MultiDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode dropsNode = node.getNode("DROPS");
            ConfigurationNode giveAllNode = node.getNode("GIVE_ALL");
            ConfigurationNode prefetchNode = node.getNode("PREFETCH");
            ConfigurationNode randomManagerNode = node.getNode("RANDOM_MANAGER");
            if (dropsNode.isVirtual()) {
                throw new IllegalArgumentException("DROPS node does not exist");
            }
            List<Drop> tempDrops = new ArrayList<>();
            for (ConfigurationNode drop_node : dropsNode.getChildrenList()) {
                tempDrops.add((Drop) GWMCratesUtils.createSuperObject(drop_node, SuperObjectType.DROP));
            }
            drops = Collections.unmodifiableList(tempDrops);
            giveAll = giveAllNode.getBoolean(true);
            prefetch = prefetchNode.getBoolean(false);
            if (randomManagerNode.isVirtual()) {
                randomManager = GWMCrates.getInstance().getDefaultRandomManager();
            } else {
                randomManager = (RandomManager) GWMCratesUtils.createSuperObject(randomManagerNode, SuperObjectType.RANDOM_MANAGER);
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public MultiDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency, Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview, Optional<Integer> level, Optional<Integer> fakeLevel, Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels, Optional<Long> weight, Optional<Long> fakeWeight, Map<String, Long> permissionWeights, Map<String, Long> permissionFakeWeights,
                     List<Drop> drops, boolean giveAll, boolean prefetch, RandomManager randomManager) {
        super(id, price, sellCurrency, dropItem, customName, showInPreview, level, fakeLevel, permissionLevels, permissionFakeLevels, weight, fakeWeight, permissionWeights, permissionFakeWeights);
        this.drops = drops;
        this.giveAll = giveAll;
        this.prefetch = prefetch;
        this.randomManager = randomManager;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        if (giveAll) {
            drops.forEach(drop -> drop.give(player, amount));
        } else {
            for (int i = 0; i < amount; i++) {
                randomManager.choose(drops, player, false).give(player, 1, false);
            }
        }
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public boolean isGiveAll() {
        return giveAll;
    }

    public boolean isPrefetch() {
        return prefetch;
    }

    public RandomManager getRandomManager() {
        return randomManager;
    }
}
