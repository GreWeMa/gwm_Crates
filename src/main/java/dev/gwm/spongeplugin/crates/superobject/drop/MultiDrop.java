package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.RandomManager;
import dev.gwm.spongeplugin.library.utils.DefaultRandomableData;
import dev.gwm.spongeplugin.library.utils.GWMLibrarySuperObjectCategories;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

public final class MultiDrop extends AbstractDrop {

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
            for (ConfigurationNode dropNode : dropsNode.getChildrenList()) {
                tempDrops.add(Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(GWMCratesSuperObjectCategories.DROP, dropNode));
            }
            if (tempDrops.isEmpty()) {
                throw new IllegalArgumentException("No drops are configured! At least one drop is required!");
            }
            drops = Collections.unmodifiableList(tempDrops);
            giveAll = giveAllNode.getBoolean(true);
            prefetch = prefetchNode.getBoolean(false);
            if (prefetch && giveAll) {
                throw new IllegalArgumentException("Both Give All and Prefetch parameters are set to true!");
            }
            if (randomManagerNode.isVirtual()) {
                randomManager = GWMCratesUtils.getDefaultRandomManager();
            } else {
                randomManager = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(GWMLibrarySuperObjectCategories.RANDOM_MANAGER, randomManagerNode);
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public MultiDrop(Optional<String> id,
                     GiveableData giveableData,
                     Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                     DefaultRandomableData defaultRandomableData,
                     List<Drop> drops, boolean giveAll, boolean prefetch, RandomManager randomManager) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        this.drops = drops;
        this.giveAll = giveAll;
        if (prefetch && giveAll) {
            throw new IllegalArgumentException("Both Give All and Prefetch parameters are set to true!");
        }
        this.prefetch = prefetch;
        this.randomManager = randomManager;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.addAll(drops);
        return set;
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
                ((Drop) randomManager.choose(drops, player, false)).give(player, 1, false);
            }
        }
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public boolean isGiveAll() {
        return giveAll;
    }

    @Override
    public boolean isPrefetch() {
        return prefetch;
    }

    public RandomManager getRandomManager() {
        return randomManager;
    }
}
