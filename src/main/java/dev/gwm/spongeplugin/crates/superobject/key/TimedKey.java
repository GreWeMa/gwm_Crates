package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class TimedKey extends GiveableKey {

    public static final String TYPE = "TIMED";

    public static final String SELECT_QUERY = "SELECT expire FROM timed_keys " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String UPDATE_QUERY = "UPDATE timed_keys " +
            "SET expire = ? " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String INSERT_QUERY = "INSERT INTO timed_keys (name, uuid, expire) " +
            "VALUES (?, ?, ?)";

    private final Map<UUID, Long> cache = new WeakHashMap<>();

    private final String virtualName;
    private final long delay;

    public TimedKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtualNameNode = node.getNode("VIRTUAL_NAME");
            ConfigurationNode delayNode = node.getNode("DELAY");
            if (virtualNameNode.isVirtual()) {
                throw new IllegalArgumentException("VIRTUAL_NAME node does not exist!");
            }
            if (delayNode.isVirtual()) {
                throw new IllegalArgumentException("DELAY node does not exist!");
            }
            virtualName = virtualNameNode.getString();
            if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
                throw new IllegalArgumentException("Virtual Name length is greater than MAX_VIRTUAL_NAMES_LENGTH (" +
                        GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
            }
            delay = delayNode.getLong();
            if (delay <= 0) {
                throw new IllegalArgumentException("Delay is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public TimedKey(String id, boolean doNotWithdraw,
                    GiveableData giveableData, boolean doNotAdd,
                    String virtualName, long delay) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
            throw new IllegalArgumentException("Virtual Name length is greater than MAX_VIRTUAL_NAMES_LENGTH (" +
                    GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
        }
        this.virtualName = virtualName;
        if (delay <= 0) {
            throw new IllegalArgumentException("Delay is equal to or less than 0!");
        }
        this.delay = delay;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            if (GWMCrates.getInstance().isUseMySQLForTimedKeys()) {
                try {
                    setSQL(player, true);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set timed key \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, true);
            }
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            if (GWMCrates.getInstance().isUseMySQLForTimedKeys()) {
                try {
                    setSQL(player, false);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set timed key \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, false);
            }
        }
    }

    private void setCfg(Player player, boolean withdraw) {
        ConfigurationNode expireNode = GWMCrates.getInstance().getTimedKeysConfig().
                getNode(player.getUniqueId().toString(), virtualName);
        if (withdraw) {
            expireNode.setValue(System.currentTimeMillis() + delay);
        } else {
            expireNode.setValue(0L);
        }
    }

    private void setSQL(Player player, boolean withdraw) throws SQLException {
        UUID uuid = player.getUniqueId();
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection()) {
            long expire = withdraw ? System.currentTimeMillis() + delay : 0L;
            if (hasValue(connection, uuid)) {
                update(connection, uuid, expire);
            } else {
                insert(connection, uuid, expire);
            }
            cache.put(uuid, expire);
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

    private void update(Connection connection, UUID uuid, long delay) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setLong(1, delay);
            statement.setString(2, virtualName);
            statement.setString(3, uuid.toString());
            statement.execute();
        }
    }

    private void insert(Connection connection, UUID uuid, long delay) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            statement.setLong(3, delay);
            statement.execute();
        }
    }

    @Override
    public int get(Player player) {
        if (GWMCrates.getInstance().isUseMySQLForTimedKeys()) {
            try {
                return getSQL(player);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get timed key \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
            }
        } else {
            return getCfg(player);
        }
    }

    private int getCfg(Player player) {
        ConfigurationNode expireNode = GWMCrates.getInstance().getTimedKeysConfig().
                getNode(player.getUniqueId().toString(), virtualName);
        if (expireNode.isVirtual()) {
            return 1;
        }
        long expire = expireNode.getLong();
        return System.currentTimeMillis() >= expire ? 1 : 0;
    }

    private int getSQL(Player player) throws SQLException {
        UUID uuid = player.getUniqueId();
        if (cache.containsKey(uuid)) {
            long expire = cache.get(uuid);
            return System.currentTimeMillis() >= expire ? 1 : 0;
        }
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                long expire = set.getLong(1);
                cache.put(uuid, expire);
                return System.currentTimeMillis() >= expire ? 1 : 0;
            } else {
                cache.put(uuid, 0L);
                return 1;
            }
        }
    }

    public String getVirtualName() {
        return virtualName;
    }

    public long getDelay() {
        return delay;
    }
}
