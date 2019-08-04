package org.gwmdevelopments.sponge_plugin.crates.event;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerOpenCrateEvent extends AbstractEvent implements TargetPlayerEvent, Cancellable {

    private final Player player;
    private final Manager manager;
    private boolean cancelled = false;

    public PlayerOpenCrateEvent(Player player, Manager manager) {
        this.player = player;
        this.manager = manager;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
}
