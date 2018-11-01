package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiKey extends GiveableKey {

    private List<Key> keys;
    private boolean allKeysNeeded;

    public MultiKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode keysNode = node.getNode("KEYS");
            ConfigurationNode allKeysNeededNode = node.getNode("ALL_KEYS_NEEDED");
            if (keysNode.isVirtual()) {
                throw new RuntimeException("KEYS node does not exist");
            }
            keys = new ArrayList<>();
            for (ConfigurationNode keyNode : keysNode.getChildrenList()) {
                keys.add((Key) GWMCratesUtils.createSuperObject(keyNode, SuperObjectType.KEY));
            }
            allKeysNeeded = allKeysNeededNode.getBoolean(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Multi Key!", e);
        }
    }

    public MultiKey(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                    List<Key> keys, boolean allKeysNeeded) {
        super("MULTI", id, price, sellCurrency);
        this.keys = keys;
        this.allKeysNeeded = allKeysNeeded;
    }

    @Override
    public void withdraw(Player player, int amount) {
        if (allKeysNeeded) {
            for (Key key : keys) {
                key.withdraw(player, amount);
            }
        } else if (keys.size() > 0) {
            for (Key key : keys) {
                int value = key.get(player);
                if (value > 0) {
                    if (value >= amount) {
                        key.withdraw(player, amount);
                        break;
                    } else {
                        key.withdraw(player, value);
                        amount -= value;
                    }
                }
            }
        }
    }

    @Override
    public void give(Player player, int amount) {
        for (Key key : keys) {
            if (key instanceof Giveable) {
                ((Giveable) key).give(player, amount);
                if (!allKeysNeeded) {
                    break;
                }
            }
        }
    }

    @Override
    public int get(Player player) {
        if (allKeysNeeded) {
            if (keys.size() == 0) {
                return 0;
            } else if (keys.size() == 1) {
                return keys.iterator().next().get(player);
            } else {
                int min = Integer.MAX_VALUE;
                for (Key key : keys) {
                    int value = key.get(player);
                    if (value <= 0) {
                        return 0;
                    } else if (value < min) {
                        min = value;
                    }
                }
                return min;
            }
        } else {
            int sum = 0;
            for (Key key : keys) {
                sum += key.get(player);
            }
            return sum;
        }
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public boolean isAllKeysNeeded() {
        return allKeysNeeded;
    }

    public void setAllKeysNeeded(boolean allKeysNeeded) {
        this.allKeysNeeded = allKeysNeeded;
    }
}
