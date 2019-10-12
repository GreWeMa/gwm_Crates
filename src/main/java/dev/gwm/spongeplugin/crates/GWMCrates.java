package dev.gwm.spongeplugin.crates;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.crates.listener.*;
import dev.gwm.spongeplugin.crates.superobject.caze.*;
import dev.gwm.spongeplugin.crates.superobject.changemode.OrderedDecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.changemode.RandomDecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.drop.*;
import dev.gwm.spongeplugin.crates.superobject.key.*;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.manager.ManagerImpl;
import dev.gwm.spongeplugin.crates.superobject.openmanager.*;
import dev.gwm.spongeplugin.crates.superobject.preview.FirstGuiPreview;
import dev.gwm.spongeplugin.crates.superobject.preview.SecondGuiPreview;
import dev.gwm.spongeplugin.crates.utils.GWMCratesCommandUtils;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.event.SuperObjectCategoriesRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectIdentifiersRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectsRegistrationEvent;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.sql.SqlService;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(
        id = "gwm_crates",
        name = "GWMCrates",
        version = "4.2.2",
        description = "Universal crates plugin",
        authors = {"GWM"/* My contacts:
                         * E-Mail(nazark@tutanota.com),
                         * Telegram(@grewema),
                         * Discord(GWM#2192)*/},
        dependencies = {
                @Dependency(id = "gwm_library"),
                @Dependency(id = "holograms", optional = true),
                @Dependency(id = "cosmetics", optional = true)
        })
public final class GWMCrates extends SpongePlugin {

    public static final Version VERSION = new Version(null,4, 2, 2);

    private static GWMCrates instance = null;

    public static GWMCrates getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GWMCrates is not initialized!");
        }
        return instance;
    }

    private static final DateTimeFormatter DEFAULT_LOG_FILE_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DEFAULT_LOG_FILE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final Vector3d DEFAULT_HOLOGRAM_OFFSET = new Vector3d(0.5, 1, 0.5);
    private static final String DEFAULT_RANDOM_MANAGER_ID = "default_random_manager";

    private Cause cause;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDirectory;
    private File managersDirectory;
    private File logsDirectory;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private Config config;
    private Config languageConfig;
    private Config virtualCasesConfig;
    private Config virtualKeysConfig;
    private Config timedCasesConfig;
    private Config timedKeysConfig;

    private Language language;

    private boolean logLoadedManagers;
    private boolean logOpenedManagers;
    private DateTimeFormatter logFileDateFormat;
    private DateTimeFormatter logFileTimeFormat;
    private Vector3d hologramOffset;
    private double multilineHologramsDistance;
    private int maxVirtualNamesLength;
    private boolean useMySQLForVirtualCases;
    private boolean useMySQLForVirtualKeys;
    private boolean useMySQLForTimedCases;
    private boolean useMySQLForTimedKeys;
    private long crateOpenDelay;
    private boolean forceCrateCommandRegistration;
    private String defaultRandomManagerId;

    private DebugCrateListener debugCrateListener;

    private Optional<DataSource> dataSource = Optional.empty();

    private Map<UUID, Long> crateOpenDelays = new HashMap<>();

    @Listener
    public void onConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        managersDirectory = new File(configDirectory, "managers");
        logsDirectory = new File(configDirectory, "logs");
        if (!configDirectory.exists()) {
            logger.info("Config directory does not exist! Trying to create it...");
            if (configDirectory.mkdirs()) {
                logger.info("Config directory successfully created!");
            } else {
                logger.error("Failed to create config directory!");
            }
        }
        if (!managersDirectory.exists()) {
            logger.info("Managers directory does not exist! Trying to create it...");
            if (managersDirectory.mkdirs()) {
                logger.info("Managers directory successfully created!");
            } else {
                logger.error("Failed to create Managers config directory!");
            }
        }
        if (!logsDirectory.exists()) {
            logger.info("Logs directory does not exist! Trying to create it...");
            if (logsDirectory.mkdirs()) {
                logger.info("Logs directory successfully created!");
            } else {
                logger.error("Failed to create logs config directory!");
            }
        }

        cause = Cause.of(EventContext.empty(), container);
        AssetManager assetManager = Sponge.getAssetManager();
        config = new Config(this, new File(configDirectory, "config.conf"),
                assetManager.getAsset(this, "config.conf"), true, false);
        languageConfig = new Config(this, new File(configDirectory, "language.conf"),
                getDefaultTranslation(assetManager), true, false);
        virtualCasesConfig = new Config(this, new File(configDirectory, "virtual_cases.conf"),
                assetManager.getAsset(this, "virtual_cases.conf"), true, true);
        virtualKeysConfig = new Config(this, new File(configDirectory, "virtual_keys.conf"),
                assetManager.getAsset(this, "virtual_keys.conf"), true, true);
        timedCasesConfig = new Config(this, new File(configDirectory, "timed_cases.conf"),
                assetManager.getAsset(this, "timed_cases.conf"), true, true);
        timedKeysConfig = new Config(this, new File(configDirectory, "timed_keys.conf"),
                assetManager.getAsset(this, "timed_keys.conf"), true, true);
        loadConfigValues();
        if (connectMySQL()) {
            createMySQLTables();
        }
        language = new Language(this);
        registerListeners();
        GWMCratesCommandUtils.registerCommands(this);
        logger.info("PreInitialization completed!");
    }

    @Listener
    public void onCategoriesRegistration(SuperObjectCategoriesRegistrationEvent event) {
        GWMCratesSuperObjectCategories.CATEGORIES.forEach(event::register);
    }

    @Listener
    public void onIdentifiersRegistration(SuperObjectIdentifiersRegistrationEvent event) {
        getSuperObjects().forEach(event::register);
    }

    @Listener
    public void onSuperObjectsRegistration(SuperObjectsRegistrationEvent event) {
        loadManagers();
    }

    //https://github.com/codeHusky/HuskyCrates-Sponge/commit/8b20d0737bfeda8c4f1d3d912fb36635ed55ab8e
    @Listener(order = Order.LATE)
    public void fightForCrateCommand(GameStartedServerEvent event) {
        if (forceCrateCommandRegistration) {
            CommandManager manager = Sponge.getCommandManager();
            CommandMapping mapping = manager.get("crate").get();
            Optional<PluginContainer> optionalContainer = manager.getOwner(mapping);
            if (optionalContainer.isPresent() && !optionalContainer.get().equals(container)) {
                manager.removeMapping(mapping);
                logger.warn("Evil command was removed!");
                GWMCratesCommandUtils.registerCommands(this);
                //This is a necessary measure, because HuskyCrates does the same,
                //and it breaks ALL the commands ('/gwmcrates', '/gwmcrate', '/crates', 'crate') NOT only '/crate'.
            }
        }
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("Reload completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        unloadManagers();
        save();
        logger.info("Stopping completed!");
    }

    public void save() {
        virtualCasesConfig.save();
        virtualKeysConfig.save();
        timedCasesConfig.save();
        timedKeysConfig.save();
        logger.info("All plugin configs have been saved!");
    }

    public void reload() {
        config.reload();
        languageConfig.reload();
        virtualCasesConfig.reload();
        virtualKeysConfig.reload();
        timedCasesConfig.reload();
        timedKeysConfig.reload();
        loadConfigValues();
        if (connectMySQL()) {
            createMySQLTables();
        }
        language = new Language(this);
        debugCrateListener.reschedule();
        unloadManagers();
        loadManagers();
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
        try {
            ConfigurationNode logLoadedManagersNode = config.getNode("LOG_LOADED_MANAGERS");
            ConfigurationNode logOpenedManagersNode = config.getNode("LOG_OPENED_MANAGERS");
            ConfigurationNode logFileDateFormatNode = config.getNode("LOG_FILE_DATE_FORMAT");
            ConfigurationNode logFileTimeFormatNode = config.getNode("LOG_FILE_TIME_FORMAT");
            ConfigurationNode hologramOffsetNode = config.getNode("HOLOGRAM_OFFSET");
            ConfigurationNode multilineHologramsDistanceNode = config.getNode("MULTILINE_HOLOGRAMS_DISTANCE");
            ConfigurationNode maxVirtualNamesLengthNode = config.getNode("MAX_VIRTUAL_NAMES_LENGTH");
            ConfigurationNode userMysqlForVirtualCasesNode = config.getNode("USE_MYSQL_FOR_VIRTUAL_CASES");
            ConfigurationNode userMysqlForVirtualKeysNode = config.getNode("USE_MYSQL_FOR_VIRTUAL_KEYS");
            ConfigurationNode userMysqlForTimedCasesNode = config.getNode("USE_MYSQL_FOR_TIMED_CASES");
            ConfigurationNode userMysqlForTimedKeysNode = config.getNode("USE_MYSQL_FOR_TIMED_KEYS");
            ConfigurationNode crateOpenDelayNode = config.getNode("CRATE_OPEN_DELAY");
            ConfigurationNode forceCrateCommandRegistrationNode = config.getNode("FORCE_CRATE_COMMAND_REGISTRATION");
            ConfigurationNode defaultRandomManagerIdNode = config.getNode("DEFAULT_RANDOM_MANAGER_ID");
            logLoadedManagers = logLoadedManagersNode.getBoolean(true);
            logOpenedManagers = logOpenedManagersNode.getBoolean(false);
            if (!logFileDateFormatNode.isVirtual()) {
                logFileDateFormat = DateTimeFormatter.ofPattern(logFileDateFormatNode.getString());
            } else {
                logFileDateFormat = DEFAULT_LOG_FILE_DATE_FORMAT;
            }
            if (!logFileTimeFormatNode.isVirtual()) {
                logFileTimeFormat = DateTimeFormatter.ofPattern(logFileTimeFormatNode.getString());
            } else {
                logFileTimeFormat = DEFAULT_LOG_FILE_TIME_FORMAT;
            }
            if (!hologramOffsetNode.isVirtual()) {
                hologramOffset = GWMLibraryUtils.parseVector3d(hologramOffsetNode);
            } else {
                hologramOffset = DEFAULT_HOLOGRAM_OFFSET;
            }
            multilineHologramsDistance = multilineHologramsDistanceNode.getDouble(0.2);
            if (multilineHologramsDistance < 0) {
                logger.warn("Multiline Holograms Distance is less than 0!");
                multilineHologramsDistance = 0;
            }
            maxVirtualNamesLength = maxVirtualNamesLengthNode.getInt(100);
            if (maxVirtualNamesLength < 1) {
                logger.warn("Max Virtual Names Length is less than 1!");
                maxVirtualNamesLength = 1;
            }
            useMySQLForVirtualCases = userMysqlForVirtualCasesNode.getBoolean(false);
            useMySQLForVirtualKeys = userMysqlForVirtualKeysNode.getBoolean(false);
            useMySQLForTimedCases = userMysqlForTimedCasesNode.getBoolean(false);
            useMySQLForTimedKeys = userMysqlForTimedKeysNode.getBoolean(false);
            crateOpenDelay = crateOpenDelayNode.getLong(10000L);
            if (crateOpenDelay < 0) {
                logger.warn("Crate Open Delay is less than 0!");
                maxVirtualNamesLength = 0;
            }
            forceCrateCommandRegistration = forceCrateCommandRegistrationNode.getBoolean(true);
            if (!defaultRandomManagerIdNode.isVirtual()) {
                defaultRandomManagerId = defaultRandomManagerIdNode.getString();
            } else {
                defaultRandomManagerId = DEFAULT_RANDOM_MANAGER_ID;
            }
        } catch (Exception e) {
            logger.error("Failed to load config values!", e);
        }
    }

    private void registerListeners() {
        debugCrateListener = new DebugCrateListener(language, logFileDateFormat, logFileTimeFormat);
        Sponge.getEventManager().registerListeners(this, debugCrateListener);
        Sponge.getEventManager().registerListeners(this, new ItemCaseListener(language));
        Sponge.getEventManager().registerListeners(this, new BlockCaseListener(language));
        Sponge.getEventManager().registerListeners(this, new EntityCaseListener(language));
        Sponge.getEventManager().registerListeners(this, new FirstOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new SecondOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new CasinoOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new Animation1Listener());
        Sponge.getEventManager().registerListeners(this, new PreviewListener());
    }

    private void loadManagers() {
        try {
            AtomicInteger loaded = new AtomicInteger();
            AtomicInteger skipped = new AtomicInteger();
            AtomicInteger failed = new AtomicInteger();
            Files.walk(managersDirectory.toPath()).
                    filter(path -> path.getFileName().toString().endsWith(".conf")).
                    forEach(path -> {
                        File managerFile = path.toFile();
                        if (managerFile.isFile()) {
                            try {
                                if (GWMCratesUtils.loadManager(managerFile, false)) {
                                    loaded.incrementAndGet();
                                } else {
                                    skipped.incrementAndGet();
                                }
                            } catch (Exception e) {
                                logger.warn("Failed to load Manager from file \"" + GWMCratesUtils.getManagerRelativePath(managerFile) + "\"!", e);
                                failed.incrementAndGet();
                            }
                        }
                    });
            String message = String.format("Managers load statistics (loaded/skipped/failed): %d/%d/%d",
                    loaded.get(), skipped.get(), failed.get());
            if (loaded.get() > 0 && failed.get() == 0) {
                logger.info(message);
            } else {
                logger.warn(message);
            }
        } catch (Exception e) {
            logger.warn("Failed to load Managers!", e);
        }
    }

    private void unloadManagers() {
        Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                shutdownCreatedSuperObjects(superObject -> superObject instanceof Manager);
    }

    private Map<SuperObjectIdentifier, Class<? extends SuperObject>> getSuperObjects() {
        Map<SuperObjectIdentifier, Class<? extends SuperObject>> map = new HashMap<>();
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.MANAGER, ManagerImpl.TYPE), ManagerImpl.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, ItemCase.TYPE), ItemCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, BlockCase.TYPE), BlockCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, EntityCase.TYPE), EntityCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, VirtualCase.TYPE), VirtualCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, TimedCase.TYPE), TimedCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.CASE, EmptyCase.TYPE), EmptyCase.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, ItemKey.TYPE), ItemKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, MultiKey.TYPE), MultiKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, MultipleAmountKey.TYPE), MultipleAmountKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, VirtualKey.TYPE), VirtualKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, TimedKey.TYPE), TimedKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, PermissionKey.TYPE), PermissionKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, CurrencyKey.TYPE), CurrencyKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, ExperienceKey.TYPE), ExperienceKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, ExperienceLevelKey.TYPE), ExperienceLevelKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, HealthKey.TYPE), HealthKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, FoodKey.TYPE), FoodKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, BiomeKey.TYPE), BiomeKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, WorldKey.TYPE), WorldKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, WorldTimeKey.TYPE), WorldTimeKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, WorldWeatherKey.TYPE), WorldWeatherKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, BoundariesKey.TYPE), BoundariesKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, RadiusKey.TYPE), RadiusKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.KEY, EmptyKey.TYPE), EmptyKey.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, NoGuiOpenManager.TYPE), NoGuiOpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, FirstOpenManager.TYPE), FirstOpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, SecondOpenManager.TYPE), SecondOpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, Animation1OpenManager.TYPE), Animation1OpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, PermissionOpenManager.TYPE), PermissionOpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.OPEN_MANAGER, CasinoOpenManager.TYPE), CasinoOpenManager.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, ItemDrop.TYPE), ItemDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, CommandDrop.TYPE), CommandDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, MultiDrop.TYPE), MultiDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, DelayDrop.TYPE), DelayDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, PermissionDrop.TYPE), PermissionDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DROP, EmptyDrop.TYPE), EmptyDrop.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.PREVIEW, FirstGuiPreview.TYPE), FirstGuiPreview.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.PREVIEW, SecondGuiPreview.TYPE), SecondGuiPreview.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.PREVIEW, SecondGuiPreview.TYPE), SecondGuiPreview.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE, RandomDecorativeItemsChangeMode.TYPE), RandomDecorativeItemsChangeMode.class);
        map.put(new SuperObjectIdentifier<>(GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE, OrderedDecorativeItemsChangeMode.TYPE), OrderedDecorativeItemsChangeMode.class);
        return map;
    }

    private boolean connectMySQL() {
        try {
            SqlService sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
            ConfigurationNode mysqlNode = config.getNode("MYSQL");
            if (mysqlNode.isVirtual()) {
                return false;
            }
            ConfigurationNode ipNode = mysqlNode.getNode("IP");
            ConfigurationNode portNode = mysqlNode.getNode("PORT");
            ConfigurationNode dbNode = mysqlNode.getNode("DB");
            ConfigurationNode userNode = mysqlNode.getNode("USER");
            ConfigurationNode passwordNode = mysqlNode.getNode("PASSWORD");
            String ip = ipNode.getString();
            int port = portNode.getInt(3306);
            String db = dbNode.getString();
            String user = userNode.getString();
            String password = passwordNode.getString();
            dataSource =
                    Optional.of(sqlService.getDataSource("jdbc:mysql://" + user + ":" + password + "@" + ip + ":" + port + "/" + db));
            logger.info("Successfully connected to MySQL!");
            return true;
        } catch (Exception e) {
            logger.warn("Failed to connect to MySQL!", e);
            return false;
        }
    }

    private void createMySQLTables() {
        try (Connection connection = dataSource.get().getConnection();
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
        } catch (Exception e) {
            logger.warn("Failed to create MySQL tables!", e);
        }
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public File getConfigDirectory() {
        return configDirectory;
    }

    public File getManagersDirectory() {
        return managersDirectory;
    }

    public File getLogsDirectory() {
        return logsDirectory;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PluginContainer getContainer() {
        return container;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Config getLanguageConfig() {
        return languageConfig;
    }

    public Config getVirtualCasesConfig() {
        return virtualCasesConfig;
    }

    public Config getVirtualKeysConfig() {
        return virtualKeysConfig;
    }

    public Config getTimedCasesConfig() {
        return timedCasesConfig;
    }

    public Config getTimedKeysConfig() {
        return timedKeysConfig;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    public boolean isLogLoadedManagers() {
        return logLoadedManagers;
    }

    public boolean isLogOpenedManagers() {
        return logOpenedManagers;
    }

    public DateTimeFormatter getLogFileDateFormat() {
        return logFileDateFormat;
    }

    public DateTimeFormatter getLogFileTimeFormat() {
        return logFileTimeFormat;
    }

    public Vector3d getHologramOffset() {
        return hologramOffset;
    }

    public double getMultilineHologramsDistance() {
        return multilineHologramsDistance;
    }

    public int getMaxVirtualNamesLength() {
        return maxVirtualNamesLength;
    }

    public boolean isUseMySQLForVirtualCases() {
        return useMySQLForVirtualCases;
    }

    public boolean isUseMySQLForVirtualKeys() {
        return useMySQLForVirtualKeys;
    }

    public boolean isUseMySQLForTimedCases() {
        return useMySQLForTimedCases;
    }

    public boolean isUseMySQLForTimedKeys() {
        return useMySQLForTimedKeys;
    }

    public long getCrateOpenDelay() {
        return crateOpenDelay;
    }

    public boolean isForceCrateCommandRegistration() {
        return forceCrateCommandRegistration;
    }

    public String getDefaultRandomManagerId() {
        return defaultRandomManagerId;
    }

    public Optional<DataSource> getDataSource() {
        return dataSource;
    }

    public Map<UUID, Long> getCrateOpenDelays() {
        return crateOpenDelays;
    }
}
