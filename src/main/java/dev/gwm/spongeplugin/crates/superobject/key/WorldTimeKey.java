package dev.gwm.spongeplugin.crates.superobject.keys;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import dev.gwm.spongeplugin.crates.superobject.Key;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WorldTimeKey extends Key {

    public static final String TYPE = "WORLD-TIME";

    private final boolean whitelistMode;
    private final Map<Integer, Integer> timeValues;
    private final Optional<World> world;

    public WorldTimeKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode timeValuesNode = node.getNode("TIME_VALUES");
            ConfigurationNode worldNode = node.getNode("WORLD");
            if (timeValuesNode.isVirtual()) {
                throw new IllegalArgumentException("TIME_VALUES node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            Map<Integer, Integer> tempTimeValues = new HashMap<>();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : timeValuesNode.getChildrenMap().entrySet()) {
                int key = Integer.parseInt(entry.getKey().toString());
                int value = entry.getValue().getInt();
                tempTimeValues.put(key, value);
            }
            timeValues = Collections.unmodifiableMap(tempTimeValues);
            if (!worldNode.isVirtual()) {
                String worldName = worldNode.getString();
                world = Sponge.getServer().getWorld(worldName);
                if (!world.isPresent()) {
                    throw new IllegalArgumentException("WORLD \"" + worldNode + "\" does not exist!");
                }
            } else {
                world = Optional.empty();
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldTimeKey(Optional<String> id, boolean doNotWithdraw,
                        boolean whitelistMode, Map<Integer, Integer> timeValues, Optional<World> world) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.timeValues = timeValues;
        this.world = world;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
    }

    @Override
    public int get(Player player) {
        long time = world.orElse(player.getWorld()).getProperties().getWorldTime() % 2400;
        if (whitelistMode) {
            for (Map.Entry<Integer, Integer> entry : timeValues.entrySet()) {
                if (entry.getKey() >= time && time <= entry.getValue()) {
                    return 1;
                }
            }
            return 0;
        } else {
            for (Map.Entry<Integer, Integer> entry : timeValues.entrySet()) {
                if (entry.getKey() >= time && time <= entry.getValue()) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public boolean isWhitelistMode() {
        return whitelistMode;
    }

    public Map<Integer, Integer> getTimeValues() {
        return timeValues;
    }

    public Optional<World> getWorld() {
        return world;
    }
}
