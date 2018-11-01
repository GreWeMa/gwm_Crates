package org.gwmdevelopments.sponge_plugin.crates.preview;

import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.spongepowered.api.entity.living.player.Player;

public interface Preview extends SuperObject {

    void preview(Player player, Manager manager);
}
