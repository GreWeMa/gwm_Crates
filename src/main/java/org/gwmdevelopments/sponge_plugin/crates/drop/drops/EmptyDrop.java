package org.gwmdevelopments.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public final class EmptyDrop extends Drop {

    public static final String TYPE = "EMPTY";

    public EmptyDrop(ConfigurationNode node) {
        super(node);
    }

    public EmptyDrop(Optional<String> id,
                     int level, Optional<ItemStack> dropItem, Optional<Integer> fakeLevel,
                     Map<String, Integer> permissionLevels, Map<String, Integer> permissionFakeLevels,
                     Optional<String> customName) {
        super(id, Optional.empty(), Optional.empty(), level, dropItem, fakeLevel, permissionLevels, permissionFakeLevels, customName);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
    }
}
