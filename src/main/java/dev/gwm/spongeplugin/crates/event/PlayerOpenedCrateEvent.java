package dev.gwm.spongeplugin.crates.event;

import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

import java.util.Collection;

public interface PlayerOpenedCrateEvent extends TargetPlayerEvent {

    Manager getManager();

    Collection<Drop> getDrops();
}
