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

public class VirtualCase extends GiveableCase {

    private Map<UUID, Integer> cache = new WeakHashMap<>();

    private String virtualName;

    public VirtualCase(ConfigurationNode node) {
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
            throw new RuntimeException("Failed to create Virtual Case!", e);
        }
    }

    public VirtualCase(Optional<String> id, Optional<BigDecimal> price, Optional<Currency> sellCurrency,
                       String virtualName) {
        super("VIRTUAL", id, price, sellCurrency);
        this.virtualName = virtualName;
    }

    @Override
    public void withdraw(Player player, int amount) {
        if (GWMCrates.getInstance().isUseMySQLForVirtualCases()) {
            setSQL(player, getSQL(player) - amount);
        } else {
            setCfg(player, getCfg(player) - amount);
        }
    }

    @Override
    public void give(Player player, int amount) {
        if (GWMCrates.getInstance().isUseMySQLForVirtualCases()) {
            setSQL(player, getSQL(player) + amount);
        } else {
            setCfg(player, getCfg(player) + amount);
        }
    }

    private void setCfg(Player player, int value) {
        GWMCrates.getInstance().getVirtualCasesConfig().
                getNode(player.getUniqueId().toString(), virtualName).setValue(value);
    }

    private void setSQL(Player player, int value) {
        UUID uuid = player.getUniqueId();
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT value FROM virtual_cases " +
                    "WHERE uuid = '" + uuid + "' AND " +
                    "name = '" + virtualName + "';");
            if (set.next()) {
                statement.executeQuery("UPDATE virtual_cases " +
                        "SET value = " + value + " " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
            } else {
                statement.executeQuery("INSERT INTO virtual_cases (uuid, name, value)" +
                        "VALUES ('" + uuid + "', '" + virtualName + "', " + value + ");");
            }
            cache.put(uuid, value);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to withdraw virtual cases \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
        }
    }

    @Override
    public int get(Player player) {
        return GWMCrates.getInstance().isUseMySQLForVirtualCases() ? getSQL(player) : getCfg(player);
    }

    private int getCfg(Player player) {
        return GWMCrates.getInstance().getVirtualCasesConfig().
                getNode(player.getUniqueId().toString(), virtualName).getInt(0);
    }

    private int getSQL(Player player) {
        UUID uuid = player.getUniqueId();
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT value FROM virtual_cases " +
                    "WHERE uuid = '" + uuid + "' AND " +
                    "name = '" + virtualName + "';");
            if (set.next()) {
                int value = set.getInt(1);
                cache.put(uuid, value);
                return value;
            } else {
                cache.put(uuid, 0);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get virtual cases \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
        }
    }

    public String getVirtualName() {
        return virtualName;
    }

    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }
}
