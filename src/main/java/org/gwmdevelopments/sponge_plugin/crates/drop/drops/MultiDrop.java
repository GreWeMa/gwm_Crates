package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultiDrop extends Drop {

    private List<Drop> drops;
    private boolean giveAll;

    public MultiDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode dropsNode = node.getNode("DROPS");
            ConfigurationNode giveAllNode = node.getNode("GIVE_ALL");
            if (dropsNode.isVirtual()) {
                throw new RuntimeException("DROPS node does not exist");
            }
            drops = new ArrayList<Drop>();
            for (ConfigurationNode drop_node : dropsNode.getChildrenList()) {
                drops.add((Drop) GWMCratesUtils.createSuperObject(drop_node, SuperObjectType.DROP));
            }
            giveAll = giveAllNode.getBoolean(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Multi Drop!", e);
        }
    }

    public MultiDrop(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                     int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                     Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                     List<Drop> drops, boolean giveAll) {
        super("MULTI", id, price, sellCurrency, level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
        this.drops = drops;
        this.giveAll = giveAll;
    }

    @Override
    public void apply(Player player) {
        if (giveAll) {
            drops.forEach(drop -> drop.apply(player));
        } else {
            GWMCratesUtils.chooseDropByLevel(drops, player, false).apply(player);
        }
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void setDrops(List<Drop> drops) {
        this.drops = drops;
    }

    public boolean isGiveAll() {
        return giveAll;
    }

    public void setGiveAll(boolean giveAll) {
        this.giveAll = giveAll;
    }
}
