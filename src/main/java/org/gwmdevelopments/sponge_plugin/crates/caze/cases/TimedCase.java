package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.GiveableCase;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public final class TimedCase extends GiveableCase {

    public static final String TYPE = "TIMED";

    public static final String SELECT_QUERY = "SELECT delay FROM timed_cases " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String UPDATE_QUERY = "UPDATE timed_cases " +
            "SET delay = ? " +
            "WHERE name = ? " +
            "AND uuid = ?";

    public static final String INSERT_QUERY = "INSERT INTO timed_cases (name, uuid, delay) " +
            "VALUES (?, ?, ?)";

    private final Map<UUID, Long> cache = new WeakHashMap<>();

    private final String virtualName;
    private final long delay;

    public TimedCase(ConfigurationNode node) {
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
                throw new IllegalArgumentException("VIRTUAL_NAME length is more than \"MAX_VIRTUAL_NAMES_LENGTH\" (" +
                        GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
            }
            delay = delayNode.getLong();
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public TimedCase(Optional<String> id, boolean doNotWithdraw,
                     Optional<BigDecimal> price, Optional<Currency> sellCurrency, boolean doNotAdd,
                     String virtualName, long delay) {
        super(id, doNotWithdraw, price, sellCurrency, doNotAdd);
        this.virtualName = virtualName;
        this.delay = delay;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
                try {
                    setSQL(player, true);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, true);
            }
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
                try {
                    setSQL(player, false);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
                }
            } else {
                setCfg(player, false);
            }
        }
    }

    private void setCfg(Player player, boolean withdraw) {
        ConfigurationNode delayNode = GWMCrates.getInstance().getTimedCasesConfig().
                getNode(player.getUniqueId().toString(), virtualName);
        if (withdraw) {
            delayNode.setValue(System.currentTimeMillis() + delay);
        } else {
            delayNode.setValue(0L);
        }
    }

    private void setSQL(Player player, boolean withdraw) throws SQLException {
        UUID uuid = player.getUniqueId();
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection()) {
            long expire = withdraw ? System.currentTimeMillis() + delay : 0L;
            if (hasValue(connection, uuid)) {
                update(connection, uuid, delay);
            } else {
                insert(connection, uuid, delay);
            }
            cache.put(uuid, delay);
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
        if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
            try {
                return getSQL(player);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + player.getUniqueId() + "\")!", e);
            }
        } else {
            return getCfg(player);
        }
    }

    private int getCfg(Player player) {
        ConfigurationNode delayNode = GWMCrates.getInstance().getTimedCasesConfig().
                getNode(player.getUniqueId().toString(), virtualName);
        if (delayNode.isVirtual()) {
            return 1;
        }
        long delay = delayNode.getLong();
        return System.currentTimeMillis() >= delay ? 1 : 0;
    }

    private int getSQL(Player player) throws SQLException {
        UUID uuid = player.getUniqueId();
        if (cache.containsKey(uuid)) {
            long delay = cache.get(uuid);
            return System.currentTimeMillis() >= delay ? 1 : 0;
        }
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
            statement.setString(1, virtualName);
            statement.setString(2, uuid.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                long delay = set.getLong(1);
                cache.put(uuid, delay);
                return System.currentTimeMillis() >= delay ? 1 : 0;
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
