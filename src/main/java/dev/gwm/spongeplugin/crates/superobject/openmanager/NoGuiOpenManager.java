package dev.gwm.spongeplugin.crates.superobject.openmanager;

import dev.gwm.spongeplugin.crates.event.PlayerOpenCrateEventImpl;
import dev.gwm.spongeplugin.crates.event.PlayerOpenedCrateEventImpl;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collections;
import java.util.Optional;

public final class NoGuiOpenManager extends AbstractOpenManager {

    public static final String TYPE = "NO-GUI";

    public NoGuiOpenManager(ConfigurationNode node) {
        super(node);
    }

    public NoGuiOpenManager(String id, Optional<SoundType> openSound) {
        super(id, openSound);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEventImpl openEvent = new PlayerOpenCrateEventImpl(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Drop drop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, false);
        drop.give(player, 1);
        getOpenSound().ifPresent(openSound -> player.playSound(openSound, player.getLocation().getPosition(), 1.));
        PlayerOpenedCrateEventImpl openedEvent = new PlayerOpenedCrateEventImpl(player, manager, Collections.singletonList(drop));
        Sponge.getEventManager().post(openedEvent);
    }
}
