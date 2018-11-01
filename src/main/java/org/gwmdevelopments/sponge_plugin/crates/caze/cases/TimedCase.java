package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.GiveableCase;
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

public class TimedCase extends GiveableCase {

    private Map<UUID, Long> cache = new WeakHashMap<>();

    private String virtualName;
    private long delay;

    public TimedCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtualNameNode = node.getNode("VIRTUAL_NAME");
            ConfigurationNode delayNode = node.getNode("DELAY");
            if (virtualNameNode.isVirtual()) {
                throw new RuntimeException("VIRTUAL_NAME node does not exist!");
            }
            if (delayNode.isVirtual()) {
                throw new RuntimeException("DELAY node does not exist!");
            }
            virtualName = virtualNameNode.getString();
            if (virtualName.length() > GWMCrates.getInstance().getMaxVirtualNamesLength()) {
                throw new RuntimeException("VIRTUAL_NAME length is more than \"MAX_VIRTUAL_NAMES_LENGTH\" (" +
                        GWMCrates.getInstance().getMaxVirtualNamesLength() + ")!");
            }
            delay = delayNode.getLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Timed Case!", e);
        }
    }

    public TimedCase(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                     String virtualName, long delay) {
        super("TIMED", id, price, sellCurrency);
        this.virtualName = virtualName;
        this.delay = delay;
    }

    @Override
    public void withdraw(Player player, int amount) {

    }

    @Override
    public void give(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
            try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery("SELECT delay FROM timed_cases " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
                if (set.next()) {
                    if (amount > 0) {
                        statement.executeQuery("UPDATE timed_cases " +
                                "SET delay = " + 0L + " " +
                                "WHERE uuid = '" + uuid + "' AND" +
                                "name = '" + virtualName + "';");
                    } else if (amount < 0) {
                        statement.executeQuery("UPDATE timed_cases " +
                                "SET delay = " + (System.currentTimeMillis() + delay) + " " +
                                "WHERE uuid = '" + uuid + "' AND" +
                                "name = '" + virtualName + "';");
                    }
                } else {
                    if (amount > 0) {
                        statement.executeQuery("INSERT INTO timed_cases (uuid, name, delay)" +
                                "VALUES ('" + uuid + "', '" + virtualName + "', " + 0L + ");");
                    } else if (amount < 0) {
                        statement.executeQuery("INSERT INTO timed_cases (uuid, name, delay)" +
                                "VALUES ('" + uuid + "', '" + virtualName + "', " + (System.currentTimeMillis() + delay) + ");");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            ConfigurationNode delayNode = GWMCrates.getInstance().getTimedCasesConfig().
                    getNode(uuid.toString(), virtualName);
            if (amount > 0) {
                delayNode.setValue(0L);
            } else if (amount < 0) {
                delayNode.setValue(System.currentTimeMillis() + delay);
            }
        }
    }

    @Override
    public int get(Player player) {
        UUID uuid = player.getUniqueId();
        if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
            try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
                 Statement statement = connection.createStatement()) {
                if (cache.containsKey(uuid)) {
                    long delay = cache.get(uuid);
                    return System.currentTimeMillis() >= delay ? Integer.MAX_VALUE : 0;
                }
                ResultSet set = statement.executeQuery("SELECT delay FROM timed_cases " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
                if (set.next()) {
                    long delay = cache.put(uuid, set.getLong(1));
                    return System.currentTimeMillis() >= delay ? Integer.MAX_VALUE : 0;
                } else {
                    cache.put(uuid, 0L);
                    return Integer.MAX_VALUE;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            ConfigurationNode delayNode = GWMCrates.getInstance().getTimedCasesConfig().
                    getNode(uuid.toString(), virtualName);
            if (delayNode.isVirtual()) {
                return Integer.MAX_VALUE;
            }
            long delay = delayNode.getLong();
            return System.currentTimeMillis() >= delay ? Integer.MAX_VALUE : 0;
        }
    }

    public String getVirtualName() {
        return virtualName;
    }

    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
