package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.AbstractKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WorldTimeKey extends AbstractKey {

    public static final String TYPE = "WORLD-TIME";

    private final static Map<String, Integer> PREDEFINED_TIME_VALUES;

    static {
        Map<String, Integer> predefinedTimeValues = new HashMap<>();
        predefinedTimeValues.put("DAY", 1000);
        predefinedTimeValues.put("NOON", 6000);
        predefinedTimeValues.put("SUNSET", 12000);
        predefinedTimeValues.put("NIGHT", 13000);
        predefinedTimeValues.put("MIDNIGHT", 18000);
        predefinedTimeValues.put("SUNRISE", 23000);
        PREDEFINED_TIME_VALUES = Collections.unmodifiableMap(predefinedTimeValues);
    }

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
                int key = parseTimeValue(entry.getKey().toString());
                int value = parseTimeValue(entry.getValue().getString());
                tempTimeValues.put(key, value);
            }
            checkTimeValues(tempTimeValues);
            timeValues = Collections.unmodifiableMap(tempTimeValues);
            if (!worldNode.isVirtual()) {
                String worldName = worldNode.getString();
                world = Sponge.getServer().getWorld(worldName);
                if (!world.isPresent()) {
                    throw new IllegalArgumentException("World \"" + worldNode + "\" is not found!");
                }
            } else {
                world = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public WorldTimeKey(String id, boolean doNotWithdraw,
                        boolean whitelistMode, Map<Integer, Integer> timeValues, Optional<World> world) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        checkTimeValues(timeValues);
        this.timeValues = Collections.unmodifiableMap(timeValues);
        this.world = world;
    }

    private int parseTimeValue(String string) {
        if (PREDEFINED_TIME_VALUES.containsKey(string)) {
            return PREDEFINED_TIME_VALUES.get(string);
        }
        return Integer.parseInt(string);
    }

    private void checkTimeValues(Map<Integer, Integer> timeValues) {
        if (timeValues.isEmpty()) {
            throw new IllegalArgumentException("No Time Values are configured! At least one Tie Value is required!");
        }
        timeValues.forEach((start, end) -> {
            if (start >= end || start < 0 || end > 24000) {
                throw new IllegalArgumentException("Time Value (" + start + " -> " + end + ") is illegal! Start should be less then End, Start should be greater than 0 and End should be less than 24000!");
            }
        });
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
        long time = world.orElse(player.getWorld()).getProperties().getWorldTime() % 24_000;
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
