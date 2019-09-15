package dev.gwm.spongeplugin.crates.event;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Collections;
import java.util.List;

public class PlayerOpenedCrateEvent extends AbstractEvent implements TargetPlayerEvent {

    private final Player player;
    private final Manager manager;
    private final List<Drop> drops;

    public PlayerOpenedCrateEvent(Player player, Manager manager, List<Drop> drops) {
        this.player = player;
        this.manager = manager;
        this.drops = Collections.unmodifiableList(drops);
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

    public List<Drop> getDrops() {
        return drops;
    }
}
