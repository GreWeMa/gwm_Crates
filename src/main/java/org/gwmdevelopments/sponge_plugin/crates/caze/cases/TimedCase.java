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
        if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
            setSQL(player, true);
        } else {
            setCfg(player, true);
        }
    }

    @Override
    public void give(Player player, int amount) {
        if (GWMCrates.getInstance().isUseMySQLForTimedCases()) {
            setSQL(player, false);
        } else {
            setCfg(player, false);
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

    private void setSQL(Player player, boolean withdraw) {
        UUID uuid = player.getUniqueId();
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT delay FROM timed_cases " +
                    "WHERE uuid = '" + uuid + "' AND " +
                    "name = '" + virtualName + "';");
            if (set.next()) {
                if (withdraw) {
                    long expire = System.currentTimeMillis() + delay;
                    cache.put(uuid, expire);
                    statement.executeQuery("UPDATE timed_cases " +
                            "SET delay = " + expire + " " +
                            "WHERE uuid = '" + uuid + "' AND " +
                            "name = '" + virtualName + "';");
                } else {
                    cache.put(uuid, 0L);
                    statement.executeQuery("UPDATE timed_cases " +
                            "SET delay = " + 0L + " " +
                            "WHERE uuid = '" + uuid + "' AND " +
                            "name = '" + virtualName + "';");
                }
            } else {
                if (withdraw) {
                    long expire = System.currentTimeMillis() + delay;
                    cache.put(uuid, expire);
                    statement.executeQuery("INSERT INTO timed_cases (uuid, name, delay) " +
                            "VALUES ('" + uuid + "', '" + virtualName + "', " + expire + ");");
                } else {
                    cache.put(uuid, 0L);
                    statement.executeQuery("INSERT INTO timed_cases (uuid, name, delay) " +
                            "VALUES ('" + uuid + "', '" + virtualName + "', " + 0L + ");");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
        }
    }

    @Override
    public int get(Player player) {
        return GWMCrates.getInstance().isUseMySQLForTimedCases() ? getSQL(player) : getCfg(player);
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

    private int getSQL(Player player) {
        UUID uuid = player.getUniqueId();
        if (cache.containsKey(uuid)) {
            long delay = cache.get(uuid);
            return System.currentTimeMillis() >= delay ? 1 : 0;
        }
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT delay FROM timed_cases " +
                    "WHERE uuid = '" + uuid + "' AND " +
                    "name = '" + virtualName + "';");
            if (set.next()) {
                long delay = set.getLong(1);
                cache.put(uuid, delay);
                return System.currentTimeMillis() >= delay ? 1 : 0;
            } else {
                cache.put(uuid, 0L);
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get timed case \"" + virtualName + "\" delay for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
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
