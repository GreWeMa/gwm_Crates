package dev.gwm.spongeplugin.crates.util;

import dev.gwm.spongeplugin.crates.GWMCrates;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

public final class GWMCratesMySqlUtils {

    private GWMCratesMySqlUtils() {
    }

    public static DataSource createDataSource(ConfigurationNode node) throws SQLException {
        SqlService sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
        ConfigurationNode ipNode = node.getNode("IP");
        ConfigurationNode portNode = node.getNode("PORT");
        ConfigurationNode dbNode = node.getNode("DB");
        ConfigurationNode userNode = node.getNode("USER");
        ConfigurationNode passwordNode = node.getNode("PASSWORD");
        String ip = ipNode.getString("localhost");
        int port = portNode.getInt(3306);
        String db = dbNode.getString("gwmcrates");
        String user = userNode.getString("gwmcrates");
        String password = passwordNode.getString("gwmcrates");
        return sqlService.getDataSource("jdbc:mysql://" + user + ":" + password + "@" + ip + ":" + port + "/" + db);
    }

    public static void createTables(DataSource dataSource, int maxVirtualNamesLength) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS virtual_cases (" +
                    "name VARCHAR(" + maxVirtualNamesLength + "), " +
                    "uuid VARCHAR(36), " +
                    "value INTEGER NOT NULL DEFAULT 0, " +
                    "CONSTRAINT virtual_cases_pk PRIMARY KEY (name, uuid));");
            statement.execute("CREATE TABLE IF NOT EXISTS virtual_keys (" +
                    "name VARCHAR(" + maxVirtualNamesLength + "), " +
                    "uuid VARCHAR(36), " +
                    "value INTEGER NOT NULL DEFAULT 0, " +
                    "CONSTRAINT virtual_keys_pk PRIMARY KEY (name, uuid));");
            statement.execute("CREATE TABLE IF NOT EXISTS timed_cases (" +
                    "name VARCHAR(" + maxVirtualNamesLength + "), " +
                    "uuid VARCHAR(36), " +
                    "expire BIGINT NOT NULL DEFAULT 0, " +
                    "CONSTRAINT timed_cases_pk PRIMARY KEY (name, uuid));");
            statement.execute("CREATE TABLE IF NOT EXISTS timed_keys (" +
                    "name VARCHAR(" + maxVirtualNamesLength + "), " +
                    "uuid VARCHAR(36), " +
                    "expire BIGINT NOT NULL DEFAULT 0, " +
                    "CONSTRAINT timed_keys_pk PRIMARY KEY (name, uuid));");
        }
    }

    public static void asyncExportData(DataSource dataSource) {
        new Thread(() -> {
            ConsoleSource console = Sponge.getServer().getConsole();
            try {
                final long time = exportData(dataSource);
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("EXPORT_TO_MYSQL_SUCCESSFUL",
                                        new ImmutablePair<>("TIME", GWMCratesUtils.millisToString(time)),
                                        console))).
                        submit(GWMCrates.getInstance());
            } catch (SQLException e) {
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("EXPORT_TO_MYSQL_FAILED", console)))
                        .submit(GWMCrates.getInstance());
                GWMCrates.getInstance().getLogger().error("Async export to MySQL failed!", e);
            }
        }).start();
    }

    public static long exportData(DataSource dataSource) throws SQLException {
        long start = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement virtualCasesSelectStatement =
                    connection.prepareStatement("SELECT value FROM virtual_cases " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement virtualCasesUpdateStatement =
                    connection.prepareStatement("UPDATE virtual_cases " +
                            "SET value = ? " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement virtualCasesInsertStatement =
                    connection.prepareStatement("INSERT INTO virtual_cases (uuid, name, value) " +
                            "VALUES (?, ?, ?)");
            for (Map.Entry<Object, ? extends ConfigurationNode> virtualCasesEntry :
                    GWMCrates.getInstance().getVirtualCasesConfig().getNode().getChildrenMap().entrySet()) {
                String uuid = virtualCasesEntry.getKey().toString();
                for (Map.Entry<Object, ? extends ConfigurationNode> valueEntry :
                        virtualCasesEntry.getValue().getChildrenMap().entrySet()) {
                    String name = valueEntry.getKey().toString();
                    int value = valueEntry.getValue().getInt();
                    virtualCasesSelectStatement.setString(1, uuid);
                    virtualCasesSelectStatement.setString(2, name);
                    if (virtualCasesSelectStatement.executeQuery().next()) {
                        virtualCasesUpdateStatement.setInt(1, value);
                        virtualCasesUpdateStatement.setString(2, uuid);
                        virtualCasesUpdateStatement.setString(3, name);
                        virtualCasesUpdateStatement.executeUpdate();
                    } else {
                        virtualCasesInsertStatement.setString(1, uuid);
                        virtualCasesInsertStatement.setString(2, name);
                        virtualCasesInsertStatement.setInt(3, value);
                        virtualCasesInsertStatement.executeUpdate();
                    }
                }
            }
            PreparedStatement virtualKeysSelectStatement =
                    connection.prepareStatement("SELECT value FROM virtual_keys " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement virtualKeysUpdateStatement =
                    connection.prepareStatement("UPDATE virtual_keys " +
                            "SET value = ? " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement virtualKeysInsertStatement =
                    connection.prepareStatement("INSERT INTO virtual_keys (uuid, name, value) " +
                            "VALUES (?, ?, ?)");
            for (Map.Entry<Object, ? extends ConfigurationNode> virtualKeysEntry :
                    GWMCrates.getInstance().getVirtualKeysConfig().getNode().getChildrenMap().entrySet()) {
                String uuid = virtualKeysEntry.getKey().toString();
                for (Map.Entry<Object, ? extends ConfigurationNode> valueEntry :
                        virtualKeysEntry.getValue().getChildrenMap().entrySet()) {
                    String name = valueEntry.getKey().toString();
                    int value = valueEntry.getValue().getInt();
                    virtualKeysSelectStatement.setString(1, uuid);
                    virtualKeysSelectStatement.setString(2, name);
                    if (virtualKeysSelectStatement.executeQuery().next()) {
                        virtualKeysUpdateStatement.setInt(1, value);
                        virtualKeysUpdateStatement.setString(2, uuid);
                        virtualKeysUpdateStatement.setString(3, name);
                        virtualKeysUpdateStatement.executeUpdate();
                    } else {
                        virtualKeysInsertStatement.setString(1, uuid);
                        virtualKeysInsertStatement.setString(2, name);
                        virtualKeysInsertStatement.setInt(3, value);
                        virtualKeysInsertStatement.executeUpdate();
                    }
                }
            }
            PreparedStatement timedCasesSelectStatement =
                    connection.prepareStatement("SELECT expire FROM timed_cases " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement timedCasesUpdateStatement =
                    connection.prepareStatement("UPDATE timed_cases " +
                            "SET expire = ? " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement timedCasesInsertStatement =
                    connection.prepareStatement("INSERT INTO timed_cases (uuid, name, expire) " +
                            "VALUES (?, ?, ?)");
            for (Map.Entry<Object, ? extends ConfigurationNode> timedCasesEntry :
                    GWMCrates.getInstance().getTimedCasesConfig().getNode().getChildrenMap().entrySet()) {
                String uuid = timedCasesEntry.getKey().toString();
                for (Map.Entry<Object, ? extends ConfigurationNode> valueEntry :
                        timedCasesEntry.getValue().getChildrenMap().entrySet()) {
                    String name = valueEntry.getKey().toString();
                    long delay = valueEntry.getValue().getLong();
                    timedCasesSelectStatement.setString(1, uuid);
                    timedCasesSelectStatement.setString(2, name);
                    if (timedCasesSelectStatement.executeQuery().next()) {
                        timedCasesUpdateStatement.setLong(1, delay);
                        timedCasesUpdateStatement.setString(2, uuid);
                        timedCasesUpdateStatement.setString(3, name);
                        timedCasesUpdateStatement.executeUpdate();
                    } else {
                        timedCasesInsertStatement.setString(1, uuid);
                        timedCasesInsertStatement.setString(2, name);
                        timedCasesInsertStatement.setLong(3, delay);
                        timedCasesInsertStatement.executeUpdate();
                    }
                }
            }
            PreparedStatement timedKeysSelectStatement =
                    connection.prepareStatement("SELECT expire FROM timed_keys " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement timedKeysUpdateStatement =
                    connection.prepareStatement("UPDATE timed_keys " +
                            "SET expire = ? " +
                            "WHERE uuid = ? " +
                            "AND name = ?");
            PreparedStatement timedKeysInsertStatement =
                    connection.prepareStatement("INSERT INTO timed_keys (uuid, name, expire) " +
                            "VALUES (?, ?, ?)");
            for (Map.Entry<Object, ? extends ConfigurationNode> timedKeysEntry :
                    GWMCrates.getInstance().getTimedKeysConfig().getNode().getChildrenMap().entrySet()) {
                String uuid = timedKeysEntry.getKey().toString();
                for (Map.Entry<Object, ? extends ConfigurationNode> valueEntry :
                        timedKeysEntry.getValue().getChildrenMap().entrySet()) {
                    String name = valueEntry.getKey().toString();
                    long delay = valueEntry.getValue().getLong();
                    timedKeysSelectStatement.setString(1, uuid);
                    timedKeysSelectStatement.setString(2, name);
                    if (timedKeysSelectStatement.executeQuery().next()) {
                        timedKeysUpdateStatement.setLong(1, delay);
                        timedKeysUpdateStatement.setString(2, uuid);
                        timedKeysUpdateStatement.setString(3, name);
                        timedKeysUpdateStatement.executeUpdate();
                    } else {
                        timedKeysInsertStatement.setString(1, uuid);
                        timedKeysInsertStatement.setString(2, name);
                        timedKeysInsertStatement.setLong(3, delay);
                        timedKeysInsertStatement.executeUpdate();
                    }
                }
            }
            connection.commit();
            virtualCasesSelectStatement.close();
            virtualCasesUpdateStatement.close();
            virtualCasesInsertStatement.close();
            virtualKeysSelectStatement.close();
            virtualKeysUpdateStatement.close();
            virtualKeysInsertStatement.close();
            timedCasesSelectStatement.close();
            timedCasesUpdateStatement.close();
            timedCasesInsertStatement.close();
            timedKeysSelectStatement.close();
            timedKeysUpdateStatement.close();
            timedKeysInsertStatement.close();
        }
        return System.currentTimeMillis() - start;
    }

    public static void asyncImportData(DataSource dataSource) {
        new Thread(() -> {
            ConsoleSource console = Sponge.getServer().getConsole();
            try {
                final long time = importData(dataSource);
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("IMPORT_FROM_MYSQL_SUCCESSFUL",
                                        new ImmutablePair<>("TIME", GWMCratesUtils.millisToString(time)),
                                        console))).
                        submit(GWMCrates.getInstance());
            } catch (SQLException e) {
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("IMPORT_FROM_MYSQL_FAILED", console)))
                        .submit(GWMCrates.getInstance());
                GWMCrates.getInstance().getLogger().error("Async import from MySQL failed!", e);
            }
        }).start();
    }

    public static long importData(DataSource dataSource) throws SQLException {
        long start = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet virtualCases = statement.executeQuery("SELECT uuid, name, value FROM virtual_cases;");
            while (virtualCases.next()) {
                GWMCrates.getInstance().getVirtualCasesConfig().
                        getNode(virtualCases.getString(1), virtualCases.getString(2)).
                        setValue(virtualCases.getInt(3));
            }
            ResultSet virtualKeys = statement.executeQuery("SELECT uuid, name, value FROM virtual_keys;");
            while (virtualKeys.next()) {
                GWMCrates.getInstance().getVirtualKeysConfig().
                        getNode(virtualKeys.getString(1), virtualKeys.getString(2)).
                        setValue(virtualKeys.getInt(3));
            }
            ResultSet timedCases = statement.executeQuery("SELECT uuid, name, expire FROM timed_cases;");
            while (timedCases.next()) {
                GWMCrates.getInstance().getVirtualCasesConfig().
                        getNode(timedCases.getString(1), timedCases.getString(2)).
                        setValue(timedCases.getLong(3));
            }
            ResultSet timedKeys = statement.executeQuery("SELECT uuid, name, expire FROM timed_keys;");
            while (timedKeys.next()) {
                GWMCrates.getInstance().getTimedKeysConfig().
                        getNode(timedKeys.getString(1), timedKeys.getString(2)).
                        setValue(timedKeys.getLong(3));
            }
            GWMCrates.getInstance().getVirtualCasesConfig().save();
            GWMCrates.getInstance().getVirtualKeysConfig().save();
            GWMCrates.getInstance().getTimedCasesConfig().save();
            GWMCrates.getInstance().getTimedKeysConfig().save();
        }
        return System.currentTimeMillis() - start;
    }
}
