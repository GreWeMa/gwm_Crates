package org.gwmdevelopments.sponge_plugin.crates;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.*;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.change_modes.OrderedChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.change_modes.RandomChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.command.GWMCratesCommandUtils;
import org.gwmdevelopments.sponge_plugin.crates.drop.drops.*;
import org.gwmdevelopments.sponge_plugin.crates.event.GWMCratesRegistrationEvent;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze.*;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode.OrderedChangeModeConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode.RandomChangeModeConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop.*;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key.*;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager.*;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview.FirstPreviewConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview.PermissionPreviewConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview.SecondPreviewConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.key.keys.*;
import org.gwmdevelopments.sponge_plugin.crates.listener.*;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.*;
import org.gwmdevelopments.sponge_plugin.crates.preview.previews.FirstGuiPreview;
import org.gwmdevelopments.sponge_plugin.crates.preview.previews.PermissionPreview;
import org.gwmdevelopments.sponge_plugin.crates.preview.previews.SecondGuiPreview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectStorage;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(
        id = "gwm_crates",
        name = "GWMCrates",
        version = "beta-3.9",
        description = "Universal crates plugin!",
        authors = {"GWM"/* My contacts:
                         * E-Mail(nazark@tutanota.com),
                         * Telegram(@grewema),
                         * Discord(GWM#2192)*/},
        dependencies = {
                @Dependency(id = "gwm_library"),
                @Dependency(id = "holograms", optional = true)
        })
public final class GWMCrates extends SpongePlugin {

    public static final Version VERSION = new Version("beta", 3, 9);

    private static GWMCrates instance = null;

    public static GWMCrates getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GWMCrates not initialized!");
        }
        return instance;
    }

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
    private Config savedSuperObjectsConfig;

    private Language language;

    private boolean checkUpdates = true;
    private boolean logOpenedCrates = false;
    private Vector3d hologramOffset = new Vector3d(0.5, 1, 0.5);
    private double multilineHologramsDistance = 0.2;
    private int maxVirtualNamesLength = 100;
    private boolean useMySQLForVirtualCases = false;
    private boolean useMySQLForVirtualKeys = false;
    private boolean useMySQLForTimedCases = false;
    private boolean useMySQLForTimedKeys = false;
    private long crateOpenDelay = 10000;
    private long managersLoadDelay = 20;

    private Optional<DataSource> dataSource = Optional.empty();

    private Set<SuperObjectStorage> superObjects =
            new HashSet<>();

    private Map<Pair<SuperObjectType, String>, SuperObject> savedSuperObjects =
            new HashMap<>();

    private Set<Manager> createdManagers = new HashSet<>();

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
                logger.error("Failed to create managers config directory!");
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

        { //Backwards compatibility
            File oldTimedCasesFile = new File(configDirectory, "timed_cases_delays.conf");
            File oldTimedKeysFile = new File(configDirectory, "timed_keys_delays.conf");
            File newTimedCasesFile = new File(configDirectory, "timed_cases.conf");
            File newTimedKeysFile = new File(configDirectory, "timed_keys.conf");
            if (oldTimedCasesFile.exists()) {
                logger.warn("[BACKWARD COMPATIBILITY] Trying to rename file \"" + oldTimedCasesFile.getName() + "\" to \"" + newTimedCasesFile.getName() + "\"!");
                if (oldTimedCasesFile.renameTo(newTimedCasesFile)) {
                    logger.warn("[BACKWARD COMPATIBILITY] Successfully renamed!");
                } else {
                    logger.error("[BACKWARD COMPATIBILITY] Failed to rename!");
                }
            }
            if (oldTimedKeysFile.exists()) {
                logger.warn("[BACKWARD COMPATIBILITY] Trying to rename file \"" + oldTimedKeysFile.getName() + "\" to \"" + newTimedKeysFile.getName() + "\"!");
                if (oldTimedKeysFile.renameTo(newTimedKeysFile)) {
                    logger.warn("[BACKWARD COMPATIBILITY] Successfully renamed!");
                } else {
                    logger.error("[BACKWARD COMPATIBILITY] Failed to rename!");
                }
            }
        }

        cause = Cause.of(EventContext.empty(), container);
        config = new Config(this, "config.conf", false);
        languageConfig = new Config(this, "language.conf", false);
        savedSuperObjectsConfig = new Config(this, "saved_super_objects.conf", false);
        virtualCasesConfig = new Config(this, "virtual_cases.conf", true);
        virtualKeysConfig = new Config(this, "virtual_keys.conf", true);
        timedCasesConfig = new Config(this, "timed_cases.conf", true);
        timedKeysConfig = new Config(this, "timed_keys.conf", true);
        loadConfigValues();
        if (connectMySQL()) {
            createMySQLTables();
        }
        language = new Language(this);
        if (checkUpdates) {
            checkUpdates();
        }
        logger.info("\"PreInitialization\" completed!");
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new ItemCaseListener());
        Sponge.getEventManager().registerListeners(this, new BlockCaseListener());
        Sponge.getEventManager().registerListeners(this, new FirstOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new SecondOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new CasinoOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new PreviewListener());
        Sponge.getEventManager().registerListeners(this, new Animation1Listener());
        Sponge.getEventManager().registerListeners(this, new EntityCaseListener());
        Sponge.getEventManager().registerListeners(this, new DebugCrateListener());
        GWMCratesCommandUtils.registerCommands();
        logger.info("\"Initialization\" completed!");
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        register();
        logger.info("\"PostInitialization\" completed!");
    }

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        loadSavedSuperObjects();
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(managersLoadDelay).
                execute(this::loadManagers).
                submit(this);
        logger.info("\"GameStarting\" completed!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        Iterator<Manager> managerIterator = createdManagers.iterator();
        while (managerIterator.hasNext()) {
            managerIterator.next().shutdown();
            managerIterator.remove();
        }
        save();
        logger.info("\"Stopping\" completed!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
        logger.info("\"Reload\" completed!");
    }

    public void save() {
        virtualCasesConfig.save();
        virtualKeysConfig.save();
        timedCasesConfig.save();
        timedKeysConfig.save();
        logger.info("All plugin configs have been saved!");
    }

    public void reload() {
        Iterator<Manager> managerIterator = createdManagers.iterator();
        while (managerIterator.hasNext()) {
            managerIterator.next().shutdown();
            managerIterator.remove();
        }
        cause = Cause.of(EventContext.empty(), container);
        config.reload();
        languageConfig.reload();
        virtualCasesConfig.reload();
        virtualKeysConfig.reload();
        timedCasesConfig.reload();
        timedKeysConfig.reload();
        savedSuperObjectsConfig.reload();
        superObjects.clear();
        savedSuperObjects.clear();
        loadConfigValues();
        if (connectMySQL()) {
            createMySQLTables();
        }
        language = new Language(this);
        register();
        loadSavedSuperObjects();
        loadManagers();
        if (checkUpdates) {
            checkUpdates();
        }
        logger.info("Plugin has been reloaded.");
    }

    private void register() {
        GWMCratesRegistrationEvent registrationEvent = new GWMCratesRegistrationEvent();
        registrationEvent.register(SuperObjectType.CASE, ItemCase.TYPE, ItemCase.class, Optional.of(ItemCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.CASE, BlockCase.TYPE, BlockCase.class, Optional.of(BlockCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.CASE, EntityCase.TYPE, EntityCase.class, Optional.of(EntityCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.CASE, TimedCase.TYPE, TimedCase.class, Optional.of(TimedCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.CASE, VirtualCase.TYPE, VirtualCase.class, Optional.of(VirtualCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.CASE, EmptyCase.TYPE, EmptyCase.class, Optional.of(EmptyCaseConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, ItemKey.TYPE, ItemKey.class, Optional.of(ItemKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, MultiKey.TYPE, MultiKey.class, Optional.of(MultiKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, MultipleAmountKey.TYPE, MultipleAmountKey.class, Optional.of(MultipleAmountKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, TimedKey.TYPE, TimedKey.class, Optional.of(TimedKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, VirtualKey.TYPE, VirtualKey.class, Optional.of(VirtualKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.KEY, PermissionKey.TYPE, PermissionKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, CurrencyKey.TYPE, CurrencyKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, ExperienceKey.TYPE, ExperienceKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, ExperienceLevelKey.TYPE, ExperienceLevelKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, HealthKey.TYPE, HealthKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, FoodKey.TYPE, FoodKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, BiomeKey.TYPE, BiomeKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, WorldTimeKey.TYPE, WorldTimeKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, WorldWeatherKey.TYPE, WorldWeatherKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, BoundariesKey.TYPE, BoundariesKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, RadiusKey.TYPE, RadiusKey.class, Optional.empty());
        registrationEvent.register(SuperObjectType.KEY, EmptyKey.TYPE, EmptyKey.class, Optional.of(EmptyKeyConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, NoGuiOpenManager.TYPE, NoGuiOpenManager.class, Optional.of(NoGuiOpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, FirstOpenManager.TYPE, FirstOpenManager.class, Optional.of(FirstOpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, SecondOpenManager.TYPE, SecondOpenManager.class, Optional.of(SecondOpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, Animation1OpenManager.TYPE, Animation1OpenManager.class, Optional.of(Animation1OpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, PermissionOpenManager.TYPE, PermissionOpenManager.class, Optional.of(PermissionOpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.OPEN_MANAGER, CasinoOpenManager.TYPE, CasinoOpenManager.class, Optional.of(CasinoOpenManagerConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.PREVIEW, FirstGuiPreview.TYPE, FirstGuiPreview.class, Optional.of(FirstPreviewConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.PREVIEW, SecondGuiPreview.TYPE, SecondGuiPreview.class, Optional.of(SecondPreviewConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.PREVIEW, PermissionPreview.TYPE, PermissionPreview.class, Optional.of(PermissionPreviewConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, ItemDrop.TYPE, ItemDrop.class, Optional.of(ItemDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, CommandsDrop.TYPE, CommandsDrop.class, Optional.of(CommandsDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, MultiDrop.TYPE, MultiDrop.class, Optional.of(MultiDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, DelayDrop.TYPE, DelayDrop.class, Optional.of(DelayDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, PermissionDrop.TYPE, PermissionDrop.class, Optional.of(PermissionDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DROP, EmptyDrop.TYPE, EmptyDrop.class, Optional.of(EmptyDropConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, RandomChangeMode.TYPE, RandomChangeMode.class, Optional.of(RandomChangeModeConfigurationDialog.class));
        registrationEvent.register(SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, OrderedChangeMode.TYPE, OrderedChangeMode.class, Optional.of(OrderedChangeModeConfigurationDialog.class));
        Sponge.getEventManager().post(registrationEvent);
        for (SuperObjectStorage superObjectStorage : registrationEvent.getSuperObjectStorage()) {
            SuperObjectType superObjectType = superObjectStorage.getSuperObjectType();
            String type = superObjectStorage.getType();
            superObjects.add(superObjectStorage);
            logger.info("Successfully added Super Object \"" + superObjectType + "\" with type \"" + type + "\"!");
        }
        logger.info("Registration completed!");
    }

    private void loadConfigValues() {
        try {
            checkUpdates = config.getNode("CHECK_UPDATES").getBoolean(true);
            logOpenedCrates = config.getNode("LOG_OPENED_CRATES").getBoolean(false);
            hologramOffset = GWMLibraryUtils.parseVector3d(config.getNode("HOLOGRAM_OFFSET"), new Vector3d(0.5, 1, 0.5));
            multilineHologramsDistance = config.getNode("MULTILINE_HOLOGRAMS_DISTANCE").getDouble(0.2);
            maxVirtualNamesLength = config.getNode("MAX_VIRTUAL_NAMES_LENGTH").getInt(100);
            useMySQLForVirtualCases = config.getNode("USE_MYSQL_FOR_VIRTUAL_CASES").getBoolean(false);
            useMySQLForVirtualKeys = config.getNode("USE_MYSQL_FOR_VIRTUAL_KEYS").getBoolean(false);
            useMySQLForTimedCases = config.getNode("USE_MYSQL_FOR_TIMED_CASES").getBoolean(false);
            useMySQLForTimedKeys = config.getNode("USE_MYSQL_FOR_TIMED_KEYS").getBoolean(false);
            crateOpenDelay = config.getNode("CRATE_OPEN_DELAY").getLong(10000);
            managersLoadDelay = config.getNode("MANAGERS_LOAD_DELAY").getLong(20);
        } catch (Exception e) {
            logger.warn("Failed to load config values!", e);
        }
    }

    private void loadSavedSuperObjects() {
        for (ConfigurationNode node : savedSuperObjectsConfig.getNode("SAVED_SUPER_OBJECTS").getChildrenList()) {
            ConfigurationNode superObjectTypeNode = node.getNode("SUPER_OBJECT_TYPE");
            ConfigurationNode savedIdNode = node.getNode("SAVED_ID");
            ConfigurationNode idNode = node.getNode("ID");
            String id = idNode.isVirtual() ? "Unknown ID" : idNode.getString();
            if (superObjectTypeNode.isVirtual()) {
                logger.warn("SUPER_OBJECT_TYPE node does not exist for Saved Super Object with id \"" + id + "\"!");
                continue;
            }
            String superObjectTypeName = superObjectTypeNode.getString();
            if (!SuperObjectType.SUPER_OBJECT_TYPES.containsKey(superObjectTypeName)) {
                logger.warn("Super Object Type \"" + superObjectTypeName + "\" does not found!");
                continue;
            }
            SuperObjectType superObjectType = SuperObjectType.SUPER_OBJECT_TYPES.get(superObjectTypeName);
            if (savedIdNode.isVirtual()) {
                logger.warn("SAVED_ID node does not exist for Saved Super Object \"" + superObjectType + "\" with id \"" + id + "\"!");
                continue;
            }
            String savedId = savedIdNode.getString();
            if (savedSuperObjects.keySet().stream().map(Pair::getValue).anyMatch(s -> s.equals(savedId))) {
                logger.warn("Saved Super Object \"" + superObjectType + "\" with saved ID \"" + savedId + "\" and ID \"" + id + "\" is not loaded because its SAVED_ID is not unique!");
                continue;
            }
            Pair<SuperObjectType, String> pair = new Pair<>(superObjectType, savedId);
            if (savedSuperObjects.containsKey(pair)) {
                logger.warn("Saved Super Objects already contains Saved Super Object \"" + superObjectType + "\" with saved ID \"" + savedId + "\"!");
                continue;
            }
            try {
                savedSuperObjects.put(pair, GWMCratesUtils.createSuperObject(node, superObjectType));
                logger.info("Successfully loaded Saved Super Object \"" + superObjectType + "\" with saved ID \"" + savedId + "\" and ID \"" + id + "\"!");
            } catch (Exception e) {
                logger.warn("Failed to load Saved Super Object \"" + superObjectType + "\" with saved ID \"" + savedId + "\" and ID \"" + id + "\"!", e);
            }
        }
        logger.info("All Saved Super Objects loaded!");
    }

    private void loadManagers() {
        try {
            AtomicInteger loaded = new AtomicInteger();
            AtomicInteger failed = new AtomicInteger();
            Files.walk(managersDirectory.toPath()).forEach(path -> {
                File managerFile = path.toFile();
                if (!managerFile.isDirectory()) {
                    try {
                        GWMCratesUtils.loadManager(managerFile, false);
                        loaded.incrementAndGet();
                    } catch (Exception e) {
                        logger.warn("Failed to load manager from file \"" + GWMCratesUtils.getManagerRelativePath(managerFile) + "\"!", e);
                        failed.incrementAndGet();
                    }
                }
            });
            String message = "Successfully loaded " + loaded.get() + " managers. Failed to load " + failed.get() + " managers.";
            if (loaded.get() > 0 && failed.get() == 0) {
                logger.info(message);
            } else {
                logger.warn(message);
            }
        } catch (Exception e) {
            logger.warn("Failed to load managers!", e);
        }
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
                    "delay BIGINT NOT NULL DEFAULT 0, " +
                    "CONSTRAINT timed_cases_pk PRIMARY KEY (name, uuid));");
            statement.execute("CREATE TABLE IF NOT EXISTS timed_keys (" +
                    "name VARCHAR(" + maxVirtualNamesLength + "), " +
                    "uuid VARCHAR(36), " +
                    "delay BIGINT NOT NULL DEFAULT 0, " +
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

    public Config getSavedSuperObjectsConfig() {
        return savedSuperObjectsConfig;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    public boolean isLogOpenedCrates() {
        return logOpenedCrates;
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

    public long getManagersLoadDelay() {
        return managersLoadDelay;
    }

    public Optional<DataSource> getDataSource() {
        return dataSource;
    }

    public Set<SuperObjectStorage> getSuperObjects() {
        return superObjects;
    }

    public Map<Pair<SuperObjectType, String>, SuperObject> getSavedSuperObjects() {
        return savedSuperObjects;
    }

    public Set<Manager> getCreatedManagers() {
        return createdManagers;
    }

    public Map<UUID, Long> getCrateOpenDelays() {
        return crateOpenDelays;
    }
}
