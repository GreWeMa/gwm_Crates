package dev.gwm.spongeplugin.crates.event;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public final class PlayerOpenCrateEventImpl extends AbstractEvent implements PlayerOpenCrateEvent {

    private final Player player;
    private final Manager manager;
    private boolean cancelled = false;

    public PlayerOpenCrateEventImpl(Player player, Manager manager) {
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

    @Override
    public Manager getManager() {
        return manager;
    }
}
