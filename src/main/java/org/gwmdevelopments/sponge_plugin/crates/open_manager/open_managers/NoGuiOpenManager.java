package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class NoGuiOpenManager extends OpenManager {

    public static final String TYPE = "NO-GUI";

    public NoGuiOpenManager(ConfigurationNode node) {
        super(node);
    }

    public NoGuiOpenManager(Optional<String> id, Optional<SoundType> openSound) {
        super(id, openSound);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent openEvent = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Drop drop = manager.getRandomManager().choose(manager.getDrops(), player, false);
        drop.give(player, 1);
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, drop);
        Sponge.getEventManager().post(openedEvent);
    }
}
