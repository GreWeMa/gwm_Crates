package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.Giveable;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MultiKey extends GiveableKey {

    public static final String TYPE = "MULTI";

    private final List<Key> keys;
    private final boolean allKeysNeeded;

    public MultiKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode keysNode = node.getNode("KEYS");
            ConfigurationNode allKeysNeededNode = node.getNode("ALL_KEYS_NEEDED");
            if (keysNode.isVirtual()) {
                throw new IllegalArgumentException("KEYS node does not exist");
            }
            List<Key> tempKeys = new ArrayList<>();
            for (ConfigurationNode keyNode : keysNode.getChildrenList()) {
                tempKeys.add((Key) GWMCratesUtils.createSuperObject(keyNode, SuperObjectType.KEY));
            }
            keys = Collections.unmodifiableList(tempKeys);
            allKeysNeeded = allKeysNeededNode.getBoolean(true);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public MultiKey(Optional<String> id, boolean doNotWithdraw,
                    Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                    List<Key> keys, boolean allKeysNeeded) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.keys = keys;
        this.allKeysNeeded = allKeysNeeded;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            if (allKeysNeeded) {
                for (Key key : keys) {
                    key.withdraw(player, amount, force);
                }
            } else if (keys.size() > 0) {
                for (Key key : keys) {
                    int value = key.get(player);
                    if (value > 0) {
                        if (value >= amount) {
                            key.withdraw(player, amount, force);
                            break;
                        } else {
                            key.withdraw(player, value, force);
                            amount -= value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            for (Key key : keys) {
                if (key instanceof Giveable) {
                    ((Giveable) key).give(player, amount, force);
                    if (!allKeysNeeded) {
                        break;
                    }
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

    public boolean isAllKeysNeeded() {
        return allKeysNeeded;
    }
}
