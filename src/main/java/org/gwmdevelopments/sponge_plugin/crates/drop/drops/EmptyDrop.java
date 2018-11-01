package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.AbstractDrop;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class EmptyDrop extends AbstractDrop {

    public EmptyDrop(ConfigurationNode node) {
        super(node);
    }

    public EmptyDrop(Optional<String> id,
                     int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                     Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels) {
        super("EMPTY", id, Optional.empty(), Optional.empty(), level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels);
    }

    @Override
    public void give(Player player, int amount) {
    }
}
