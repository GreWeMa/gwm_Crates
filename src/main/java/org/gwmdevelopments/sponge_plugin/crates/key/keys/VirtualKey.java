package org.gwmdevelopments.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.key.GiveableKey;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public class VirtualKey extends GiveableKey {

    private Map<UUID, Integer> cache = new WeakHashMap<>();

    private String virtualName;

    public VirtualKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtualNameNode = node.getNode("VIRTUAL_NAME");
            if (virtualNameNode.isVirtual()) {
                throw new RuntimeException("VIRTUAL_NAME node does not exist!");
            }
            virtualName = virtualNameNode.getString();
            if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
                throw new RuntimeException("VIRTUAL_NAME length is more than \"MAX_VIRTUAL_NAMES_LENGTH\" (" +
                        GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Virtual Key!", e);
        }
    }

    public VirtualKey(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                      String virtualName) {
        super("VIRTUAL", id, price, sellCurrency);
        this.virtualName = virtualName;
    }

    @Override
    public void withdraw(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int value = get(player) - amount;
        if (GWMCrates.getInstance().isUseMySQLForVirtualKeys()) {
            try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery("SELECT value FROM virtual_keys " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
                if (set.next()) {
                    statement.executeQuery("UPDATE virtual_keys " +
                            "SET value = " + value + " " +
                            "WHERE uuid = '" + uuid + "' AND " +
                            "name = '" + virtualName + "';");
                } else {
                    statement.executeQuery("INSERT INTO virtual_keys (uuid, name, value)" +
                            "VALUES ('" + uuid + "', '" + virtualName + "', " + value + ");");
                }
                cache.put(uuid, value);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to withdraw virtual keys \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            GWMCrates.getInstance().getVirtualKeysConfig().
                    getNode(uuid.toString(), virtualName).setValue(value);
        }
    }

    @Override
    public void give(Player player, int amount) {
        withdraw(player, -amount);
    }

    @Override
    public int get(Player player) {
        UUID uuid = player.getUniqueId();
        if (GWMCrates.getInstance().isUseMySQLForVirtualKeys()) {
            if (cache.containsKey(uuid)) {
                return cache.get(uuid);
            }
            try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery("SELECT value FROM virtual_keys " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
                if (set.next()) {
                    return cache.put(uuid, set.getInt(1));
                } else {
                    return cache.put(uuid, 0);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get virtual keys \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            return GWMCrates.getInstance().getVirtualKeysConfig().
                    getNode(uuid.toString(), virtualName).getInt(0);
        }
    }

    public String getVirtualName() {
        return virtualName;
    }

    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }
}
