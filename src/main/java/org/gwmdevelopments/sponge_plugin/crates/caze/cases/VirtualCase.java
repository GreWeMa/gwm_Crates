package org.gwmdevelopments.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
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

public class VirtualCase extends Case {

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
    public void add(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int value = get(player) + amount;
        if (GWMCrates.getInstance().isUseMySQLForVirtualCases()) {
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
                throw new RuntimeException("Failed to add virtual cases \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            GWMCrates.getInstance().getVirtualCasesConfig().
                    getNode(uuid.toString(), virtualName).setValue(value);
        }
    }

    @Override
    public int get(Player player) {
        UUID uuid = player.getUniqueId();
        if (GWMCrates.getInstance().isUseMySQLForVirtualCases()) {
            if (cache.containsKey(uuid)) {
                return cache.get(uuid);
            }
            try (Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
                 Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery("SELECT value FROM virtual_cases " +
                        "WHERE uuid = '" + uuid + "' AND " +
                        "name = '" + virtualName + "';");
                if (set.next()) {
                    return cache.put(uuid, set.getInt(1));
                } else {
                    return cache.put(uuid, 0);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get virtual cases \"" + virtualName + "\" for player \"" + player.getName() + "\" (\"" + uuid + "\")!", e);
            }
        } else {
            return GWMCrates.getInstance().getVirtualCasesConfig().
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
