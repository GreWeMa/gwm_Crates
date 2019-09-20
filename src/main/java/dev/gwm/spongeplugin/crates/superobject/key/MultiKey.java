package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

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
                tempKeys.add(Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(GWMCratesSuperObjectCategories.KEY, keyNode));
            }
            if (tempKeys.isEmpty()) {
                throw new IllegalArgumentException("No Keys are configured! At least one Key is required!");
            }
            keys = Collections.unmodifiableList(tempKeys);
            allKeysNeeded = allKeysNeededNode.getBoolean(true);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public MultiKey(String id, boolean doNotWithdraw,
                    GiveableData giveableData, boolean doNotAdd,
                    List<Key> keys, boolean allKeysNeeded) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("No Keys are configured! At least one Key is required!");
        }
        this.keys = Collections.unmodifiableList(keys);
        this.allKeysNeeded = allKeysNeeded;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.addAll(keys);
        return set;
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
