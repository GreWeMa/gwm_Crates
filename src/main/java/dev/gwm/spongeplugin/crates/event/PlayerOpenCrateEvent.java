package dev.gwm.spongeplugin.crates.event;

import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

public interface PlayerOpenCrateEvent extends TargetPlayerEvent, Cancellable  {

    Manager getManager();
}
