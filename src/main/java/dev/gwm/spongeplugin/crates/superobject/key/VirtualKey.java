package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public final class VirtualKey extends GiveableKey {

    public static final String TYPE = "VIRTUAL";

    public static final String SELECT_QUERY = "SELECT value FROM virtual_keys " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String UPDATE_QUERY = "UPDATE virtual_keys " +
            "SET value = ? " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String INSERT_QUERY = "INSERT INTO virtual_keys (name, uuid, value)" +
            "VALUES (?, ?, ?)";

    private final Map<UUID, Integer> cache = new WeakHashMap<>();

    private final String virtualName;

    public VirtualKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtualNameNode = node.getNode("VIRTUAL_NAME");
            if (virtualNameNode.isVirtual()) {
                throw new IllegalArgumentException("VIRTUAL_NAME node does not exist!");
            }
            virtualName = virtualNameNode.getString();
            if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
                throw new IllegalArgumentException("Virtual Name length is greater than MAX_VIRTUAL_NAMES_LENGTH (" +
                        GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public VirtualKey(Optional<String> id, boolean doNotWithdraw,
                      GiveableData giveableData, boolean doNotAdd,
                      String virtualName) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
            throw new IllegalArgumentException("Virtual Name length is greater than MAX_VIRTUAL_NAMES_LENGTH (" +
                    GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
        }
        this.virtualName = virtualName;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            if (GWMCrates.getInstance().isUseMySQLForVirtualKeys()) {
                try {
                    setSQL(player, getSQL(player) - amount);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set virtual keys \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, getCfg(player) - amount);
            }
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            if (GWMCrates.getInstance().isUseMySQLForVirtualKeys()) {
                try {
                    setSQL(player, getSQL(player) + amount);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set virtual keys \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, getCfg(player) + amount);
            }
        }
    }

    private void setCfg(Player player, int value) {
        GWMCrates.getInstance().getVirtualKeysConfig().
                getNode(player.getUniqueId().toString(), virtualName).setValue(value);
    }

    private void setSQL(Player player, int value) throws SQLException {
        UUID uuid = player.getUniqueId();
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection()) {
            if (hasValue(connection, uuid)) {
                update(connection, uuid, value);
            } else {
                insert(connection, uuid, value);
            }
            cache.put(uuid, value);
        }
    }

    private boolean hasValue(Connection connection, UUID uuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            ResultSet set = statement.executeQuery();
            return set.next();
        }
    }

    private void update(Connection connection, UUID uuid, int value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setInt(1, value);
            statement.setString(2, virtualName);
            statement.setString(3, uuid.toString());
            statement.execute();
        }
    }

    private void insert(Connection connection, UUID uuid, int value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            statement.setInt(3, value);
            statement.execute();
        }
    }

    @Override
    public int get(Player player) {
        if (GWMCrates.getInstance().isUseMySQLForVirtualKeys()) {
            try {
                return getSQL(player);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get virtual keys \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
            }
        } else {
            return getCfg(player);
        }
    }

    private int getCfg(Player player) {
        return GWMCrates.getInstance().getVirtualKeysConfig().
                getNode(player.getUniqueId().toString(), virtualName).getInt(0);
    }

    private int getSQL(Player player) throws SQLException {
        UUID uuid = player.getUniqueId();
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                int value = set.getInt(1);
                cache.put(uuid, value);
                return value;
            } else {
                cache.put(uuid, 0);
                return 0;
            }
        }
    }

    public String getVirtualName() {
        return virtualName;
    }
}
