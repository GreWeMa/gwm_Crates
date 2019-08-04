package org.gwmdevelopments.sponge_plugin.crates.event;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerOpenedCrateEvent extends AbstractEvent implements TargetPlayerEvent {

    private final Player player;
    private final Manager manager;
    private final Drop drop;

    public PlayerOpenedCrateEvent(Player player, Manager manager, Drop drop) {
        this.player = player;
        this.manager = manager;
        this.drop = drop;
    }

    @Override
    public Cause getCause() {
        return GWMCrates.getInstance().getCause();
    }

    @Override
    public Player getTargetEntity() {
        return player;
    }

    public Manager getManager() {
        return manager;
    }

    public Drop getDrop() {
        return drop;
    }
}
