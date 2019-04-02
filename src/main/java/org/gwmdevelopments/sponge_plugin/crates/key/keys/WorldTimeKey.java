package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WorldTimeKey extends Key {

    public static final String TYPE = "WORLD-TIME";

    private final boolean whitelistMode;
    private final Map<Integer, Integer> timeValues;

    public WorldTimeKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode timeValuesNode = node.getNode("TIME_VALUES");
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
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public WorldTimeKey(Optional<String> id, boolean doNotWithdraw,
                        boolean whitelistMode, Map<Integer, Integer> timeValues) {
        super(id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.timeValues = timeValues;
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
        long time = player.getWorld().getProperties().getWorldTime() % 2400;
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
}
