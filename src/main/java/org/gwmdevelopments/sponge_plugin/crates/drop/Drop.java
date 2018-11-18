package org.gwmdevelopments.sponge_plugin.crates.drop;

import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface Drop extends SuperObject, Giveable {

    void give(Player player, int amount);

    @Override
    default void give(Player player, int amount, boolean force) {
        give(player, amount);
    }

    int getLevel();

    Optional<ItemStack> getDropItem();

    Optional<Integer> getFakeLevel();

    Map<String, Integer> getPermissionLevels();

    Map<String, Integer> getPermissionFakeLevels();
}
