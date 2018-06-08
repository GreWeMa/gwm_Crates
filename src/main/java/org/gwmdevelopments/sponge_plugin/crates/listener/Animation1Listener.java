package org.gwmdevelopments.sponge_plugin.crates.listener;

import com.flowpowered.math.vector.Vector3i;
import de.randombyte.holograms.api.HologramsService;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.Animation1OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Animation1Listener {

    public static HashMap<Player, Animation1OpenManager> OPENED_PLAYERS = new HashMap<Player, Animation1OpenManager>();

    @Listener(order = Order.LATE)
    public void moveListener(MoveEntityEvent event) {
        Entity entity = event.getTargetEntity();
        if (!entity.getType().equals(EntityTypes.PLAYER)) {
            return;
        }
        Player player = (Player) entity;
        if (!Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.containsKey(player) ||
                OPENED_PLAYERS.containsKey(player)) {
            return;
        }
        if (GWMCratesUtils.isLocationChanged(event.getFromTransform(), event.getToTransform(), true)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.LATE)
    public void commandListener(SendCommandEvent event, @First CommandSource source) {
        if (!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        if (!Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.containsKey(player) ||
                OPENED_PLAYERS.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @Listener(order = Order.LATE)
    public void interactListener(InteractBlockEvent event) {
        Optional<Location<World>> optionalTargetBlock = event.getTargetBlock().getLocation();
        if (!optionalTargetBlock.isPresent()) {
            return;
        }
        Vector3i targetBlock = optionalTargetBlock.get().getBlockPosition();
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (!optionalPlayer.isPresent()) {
            return;
        }
        Player player = optionalPlayer.get();
        if (!Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.containsKey(player) ||
                OPENED_PLAYERS.containsKey(player)) {
            for (Animation1OpenManager.Information info : Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.values()) {
                for (Location<World> location : info.getOriginalBlockStates().keySet()) {
                    if (targetBlock.equals(location.getBlockPosition())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            return;
        }
        event.setCancelled(true);
        Animation1OpenManager.Information information = Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.get(player);
        for (Map.Entry<Location<World>, Boolean> entry : information.getLocations().entrySet()) {
            Location<World> location = entry.getKey();
            boolean opened = entry.getValue();
            Vector3i crateBlock = location.getBlockPosition();
            if (targetBlock.equals(crateBlock) && !opened) {
                information.getOpenManager().getOpenManager().open(player, information.getManager());
                entry.setValue(true);
                break;
            }
        }
        for (boolean bool : information.getLocations().values()) {
            if (!bool) {
                return;
            }
        }
        PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, information.getManager(), null);
        Sponge.getEventManager().post(openedEvent);
        OPENED_PLAYERS.put(player, information.getOpenManager());
        Sponge.getScheduler().createTaskBuilder().delayTicks(information.getOpenManager().getCloseDelay()).execute(() -> {
            information.getOriginalBlockStates().forEach(((location, state) ->
                location.setBlock(state, BlockChangeFlags.NONE)));
            information.getHolograms().forEach(HologramsService.Hologram::remove);
            Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.remove(player);
            OPENED_PLAYERS.remove(player);
        }).submit(GWMCrates.getInstance());
    }
}
