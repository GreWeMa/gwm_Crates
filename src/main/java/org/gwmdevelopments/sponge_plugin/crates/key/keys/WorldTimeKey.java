package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.AbstractKey;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldTimeKey extends AbstractKey {

    private boolean whitelistMode;
    private Map<Integer, Integer> timeValues;

    public WorldTimeKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode whitelistModeNode = node.getNode("WHITELIST_MODE");
            ConfigurationNode timeValuesNode = node.getNode("TIME_VALUES");
            if (timeValuesNode.isVirtual()) {
                throw new IllegalArgumentException("TIME_VALUES node does not exist!");
            }
            whitelistMode = whitelistModeNode.getBoolean(true);
            timeValues = new HashMap<>();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : timeValuesNode.getChildrenMap().entrySet()) {
                int key = Integer.parseInt(entry.getKey().toString());
                int value = entry.getValue().getInt();
                timeValues.put(key, value);
            }
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create World Time Key!", e);
        }
    }

    public WorldTimeKey(String type, Optional<String> id, boolean doNotWithdraw,
                        boolean whitelistMode, Map<Integer, Integer> timeValues) {
        super(type, id, doNotWithdraw);
        this.whitelistMode = whitelistMode;
        this.timeValues = timeValues;
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
}
