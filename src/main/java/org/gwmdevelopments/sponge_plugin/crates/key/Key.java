package org.gwmdevelopments.sponge_plugin.crates.key;

import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.entity.living.player.Player;

public interface Key extends SuperObject {

    void withdraw(Player player, int amount);

    default void withdraw(Player player) {
        withdraw(player, 1);
    }

    int get(Player player);
}
