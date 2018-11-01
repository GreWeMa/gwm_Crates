package org.gwmdevelopments.sponge_plugin.crates.drop;

import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface Drop extends SuperObject, Giveable {

    int getLevel();

    Optional<ItemStack> getDropItem();

    Optional<Integer> getFakeLevel();

    Map<String, Integer> getPermissionLevels();

    Map<String, Integer> getPermissionFakeLevels();
}
