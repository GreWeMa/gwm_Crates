package dev.gwm.spongeplugin.crates.util;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.EmptyDrop;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.NoGuiOpenManager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.OpenManager;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.superobject.randommanager.LevelRandomManager;
import dev.gwm.spongeplugin.library.superobject.randommanager.RandomManager;
import dev.gwm.spongeplugin.library.util.Config;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public final class GWMCratesUtils {

    private GWMCratesUtils() {
    }

    public static final ItemStack EMPTY_ITEM = ItemStack.of(ItemTypes.NONE, 0);
    public static final Drop EMPTY_DROP = new EmptyDrop("gwmcrates_default_drop");
    public static final RandomManager FALLBACK_RANDOM_MANAGER = new LevelRandomManager("gwmcrates_fallback_random_manager");
    public static final OpenManager DEFAULT_OPEN_MANAGER = new NoGuiOpenManager("gwmcrates_default_open_manager", Optional.empty());

    public static boolean loadManager(File file, boolean force) {
        try {
            Config managerConfig = new Config(GWMCrates.getInstance(), file);
            ConfigurationNode loadNode = managerConfig.getNode("LOAD");
            if (force || loadNode.getBoolean(true)) {
                Manager manager = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                        create(GWMCratesSuperObjectCategories.MANAGER, managerConfig.getNode());
                if (GWMCrates.getInstance().isLogLoadedManagers()) {
                    GWMCrates.getInstance().getLogger().
                            info("Loaded the Manager from the file \"" + getManagerRelativePath(file) + "\" with id \"" + manager.id() + "\"!");
                }
                return true;
            } else {
                GWMCrates.getInstance().getLogger().
                        info("Skipping the Manager from the file \"" + getManagerRelativePath(file) + "\"!");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load the Manager from the file \"" + getManagerRelativePath(file) + "\"!", e);
        }
    }

    public static RandomManager getDefaultRandomManager() {
        String defaultRandomManagerId = GWMCrates.getInstance().getDefaultRandomManagerId();
        return Sponge.getServiceManager().provide(SuperObjectService.class).get().
                getSavedSuperObjectById(defaultRandomManagerId).
                map(superObject -> {
                    try {
                        return (RandomManager) superObject;
                    } catch (Exception e) {
                        throw new RuntimeException("Saved Super Object \"" + defaultRandomManagerId + "\" is not a Random Manager!");
                    }
                }).orElse(FALLBACK_RANDOM_MANAGER);
    }

    public static Stream<Manager> getManagersStream() {
        return Sponge.getServiceManager().provide(SuperObjectService.class).get().
                getCreatedSuperObjects().
                stream().
                filter(superObject -> superObject instanceof Manager).
                map(superObject -> (Manager) superObject);
    }

    public static void asyncImportToMySQL() {
        new Thread(() -> {
            ConsoleSource console = Sponge.getServer().getConsole();
            try {
                final long time = importToMySQL();
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("IMPORT_TO_MYSQL_SUCCESSFUL",
                                        new ImmutablePair<>("TIME", millisToString(time)),
                                        console))).
                        submit(GWMCrates.getInstance());
            } catch (SQLException e) {
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("IMPORT_TO_MYSQL_FAILED", console)))
                        .submit(GWMCrates.getInstance());
                GWMCrates.getInstance().getLogger().error("Async import to MySQL failed!", e);
            }
        }).start();
    }

    public static long importToMySQL() throws SQLException {
        long start = System.currentTimeMillis();
        Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
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
                connection.prepareStatement("SELECT delay FROM timed_cases " +
                        "WHERE uuid = ? " +
                        "AND name = ?");
        PreparedStatement timedCasesUpdateStatement =
                connection.prepareStatement("UPDATE timed_cases " +
                        "SET delay = ? " +
                        "WHERE uuid = ? " +
                        "AND name = ?");
        PreparedStatement timedCasesInsertStatement =
                connection.prepareStatement("INSERT INTO timed_cases (uuid, name, delay) " +
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
                connection.prepareStatement("SELECT delay FROM timed_keys " +
                        "WHERE uuid = ? " +
                        "AND name = ?");
        PreparedStatement timedKeysUpdateStatement =
                connection.prepareStatement("UPDATE timed_keys " +
                        "SET delay = ? " +
                        "WHERE uuid = ? " +
                        "AND name = ?");
        PreparedStatement timedKeysInsertStatement =
                connection.prepareStatement("INSERT INTO timed_keys (uuid, name, delay) " +
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
        connection.close();
        return System.currentTimeMillis() - start;
    }

    public static void asyncImportFromMySQL() {
        new Thread(() -> {
            ConsoleSource console = Sponge.getServer().getConsole();
            try {
                final long time = importFromMySQL();
                Sponge.getScheduler().createTaskBuilder().execute(() ->
                        console.sendMessages(GWMCrates.getInstance().getLanguage().
                                getTranslation("IMPORT_FROM_MYSQL_SUCCESSFUL",
                                        new ImmutablePair<>("TIME", millisToString(time)),
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

    public static long importFromMySQL() throws SQLException {
        long start = System.currentTimeMillis();
        Connection connection = GWMCrates.getInstance().getDataSource().get().getConnection();
        Statement statement = connection.createStatement();
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
        ResultSet timedCases = statement.executeQuery("SELECT uuid, name, delay FROM timed_cases;");
        while (timedCases.next()) {
            GWMCrates.getInstance().getVirtualCasesConfig().
                    getNode(timedCases.getString(1), timedCases.getString(2)).
                    setValue(timedCases.getLong(3));
        }
        ResultSet timedKeys = statement.executeQuery("SELECT uuid, name, delay FROM timed_keys;");
        while (timedKeys.next()) {
            GWMCrates.getInstance().getTimedKeysConfig().
                    getNode(timedKeys.getString(1), timedKeys.getString(2)).
                    setValue(timedKeys.getLong(3));
        }
        statement.close();
        connection.close();
        GWMCrates.getInstance().getVirtualCasesConfig().save();
        GWMCrates.getInstance().getVirtualKeysConfig().save();
        GWMCrates.getInstance().getTimedCasesConfig().save();
        GWMCrates.getInstance().getTimedKeysConfig().save();
        return System.currentTimeMillis() - start;
    }

    public static String millisToString(long time) {
        return time / 1000 + "." + time % 1000;
    }
    
    public static long getCrateOpenDelay(UUID uuid) {
        if (GWMCrates.getInstance().getCrateOpenDelays().containsKey(uuid)) {
            long current = System.currentTimeMillis();
            long delay = GWMCrates.getInstance().getCrateOpenDelays().get(uuid);
            if (delay > current) {
                return delay - current;
            } else {
                GWMCrates.getInstance().getCrateOpenDelays().remove(uuid);
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    public static void updateCrateOpenDelay(UUID uuid) {
        long crateOpenDelay = GWMCrates.getInstance().getCrateOpenDelay();
        if (crateOpenDelay >= 0) {
            GWMCrates.getInstance().getCrateOpenDelays().put(uuid, System.currentTimeMillis() + crateOpenDelay);
        }
    }

    public static boolean isFirstInventory(Container container, Slot slot) {
        int upperSize = container.iterator().next().capacity();
        Integer affectedSlot = slot.getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
        return affectedSlot != -1 && affectedSlot < upperSize;
    }

    public static int getInventoryHeight(int size) {
        int height = (int) Math.ceil((size)/9.);
        if (height < 1) {
            return 1;
        }
        if (height > 6) {
            return 6;
        }
        return height;
    }

    public static boolean isLocationChanged(Transform<World> from, Transform<World> to, boolean ySensitive) {
        Location<World> locationFrom = from.getLocation();
        Location<World> locationTo = to.getLocation();
        int xFrom = locationFrom.getBlockX();
        int yFrom = locationFrom.getBlockY();
        int zFrom = locationFrom.getBlockZ();
        int xTo = locationTo.getBlockX();
        int yTo = locationTo.getBlockY();
        int zTo = locationTo.getBlockZ();
        return xFrom != xTo || zFrom != zTo || (ySensitive && yFrom != yTo);
    }

    public static void addItemStack(Player player, ItemStack item, int amount) {
        int maxStackQuantity = item.getMaxStackQuantity();
        Iterator<Slot> slotIterator = ((PlayerInventory) player.getInventory()).getMain().<Slot>slots().iterator();
        while (slotIterator.hasNext() && amount > 0) {
            Slot slot = slotIterator.next();
            Optional<ItemStack> optionalInventoryItem = slot.peek();
            if (optionalInventoryItem.isPresent()) {
                ItemStack inventoryItem = optionalInventoryItem.get();
                if (ItemStackComparators.IGNORE_SIZE.compare(inventoryItem, item) == 0) {
                    int inventoryItemQuantity = inventoryItem.getQuantity();
                    if (inventoryItemQuantity < maxStackQuantity) {
                        int difference = maxStackQuantity - inventoryItemQuantity;
                        if (amount >= difference) {
                            inventoryItem.setQuantity(maxStackQuantity);
                            slot.set(inventoryItem);
                            amount -= difference;
                        } else {
                            inventoryItem.setQuantity(inventoryItemQuantity + amount);
                            slot.set(inventoryItem);
                            amount = 0;
                        }
                    }
                }
            } else {
                if (amount >= maxStackQuantity) {
                    ItemStack copy = item.copy();
                    copy.setQuantity(maxStackQuantity);
                    slot.set(copy);
                    amount -= maxStackQuantity;
                } else {
                    ItemStack copy = item.copy();
                    copy.setQuantity(amount);
                    slot.set(copy);
                    amount = 0;
                }
            }
        }
        if (amount > 0) {
            ItemStack copy = item.copy();
            copy.setQuantity(amount);
            Location<World> playerLocation = player.getLocation();
            World world = playerLocation.getExtent();
            Entity entity = world.createEntity(EntityTypes.ITEM, playerLocation.getPosition());
            world.spawnEntity(entity);
            entity.offer(Keys.REPRESENTED_ITEM, copy.createSnapshot());
        }
    }

    public static void removeItemStack(Player player, ItemStack item, int amount) {
        Inventory inventory = player.getInventory();
        Iterator<Slot> slotIterator = inventory.<Slot>slots().iterator();
        while (slotIterator.hasNext() && amount > 0) {
            Slot slot = slotIterator.next();
            Optional<ItemStack> optionalInventoryItem = slot.peek();
            if (optionalInventoryItem.isPresent()) {
                ItemStack inventoryItem = optionalInventoryItem.get();
                if (ItemStackComparators.IGNORE_SIZE.compare(inventoryItem, item) == 0) {
                    int itemQuantity = inventoryItem.getQuantity();
                    if (itemQuantity > amount) {
                        item.setQuantity(itemQuantity - amount);
                        slot.set(item);
                        amount = 0;
                    } else {
                        slot.set(ItemStack.of(ItemTypes.NONE, 1));
                        amount -= itemQuantity;
                    }
                }
            }
        }
    }

    public static int getItemStackAmount(Player player, ItemStack item) {
        int amount = 0;
        Inventory inventory = player.getInventory();
        for (Slot slot : inventory.<Slot>slots()) {
            Optional<ItemStack> optionalInventoryItem = slot.peek();
            if (optionalInventoryItem.isPresent()) {
                ItemStack inventoryItem = optionalInventoryItem.get();
                if (ItemStackComparators.IGNORE_SIZE.compare(inventoryItem, item) == 0) {
                    amount += inventoryItem.getQuantity();
                }
            }
        }
        return amount;
    }

    public static OrderedInventory castToOrdered(Inventory inventory) {
        Inventory result = inventory.query(QueryOperationTypes.INVENTORY_TYPE.of(OrderedInventory.class));
        if (result instanceof OrderedInventory) {
            return (OrderedInventory) result;
        }
        if (result instanceof EmptyInventory) {
            throw new IllegalArgumentException("Inventory can not be casted to Ordered Inventory!");
        }
        for (Inventory subInventory : inventory) {
            if (subInventory instanceof OrderedInventory) {
                return (OrderedInventory) subInventory;
            }
        }
        throw new IllegalArgumentException("Inventory can not be casted to Ordered Inventory!");
    }

    public static Path getManagerRelativePath(File managerFile) {
        return GWMCrates.getInstance().getManagersDirectory().toPath().relativize(managerFile.toPath());
    }

    public static <T> Optional<List<T>> parseOptionalList(ConfigurationNode node, TypeToken<T> token) throws ObjectMappingException  {
        return node.isVirtual() ?
                Optional.empty() :
                Optional.of(node.getList(token));
    }

    public static void sendInfoMessage(CommandSource source, Manager manager) {
        source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomInfo(),
                GWMCrates.getInstance().getLanguage(), "MANAGER_INFO", Arrays.asList(
                        new ImmutablePair<>("MANAGER_ID", manager.id()),
                        new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                        new ImmutablePair<>("CASE_TYPE", manager.getCase().type()),
                        new ImmutablePair<>("KEY_TYPE", manager.getKey().type()),
                        new ImmutablePair<>("OPEN_MANAGER_TYPE", manager.getOpenManager().type()),
                        new ImmutablePair<>("PREVIEW_TYPE", manager.getPreview().
                                map(SuperObject::type).orElse("No preview")),
                        new ImmutablePair<>("DROPS", GWMCratesUtils.formatDrops(manager.getDrops()))
                )));
    }

    public static void sendOpenMessage(CommandSource source, Manager manager, String formattedDrops) {
        if (manager.getCustomMessageData().isSendOpenMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomOpenMessage(),
                    GWMCrates.getInstance().getLanguage(), "SUCCESSFULLY_OPENED_MANAGER", Arrays.asList(
                            new ImmutablePair<>("DROPS", formattedDrops),
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static void sendCaseMissingMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendCaseMissingMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomCaseMissingMessage(),
                    GWMCrates.getInstance().getLanguage(), "HAVE_NOT_CASE", Arrays.asList(
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static void sendKeyMissingMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendKeyMissingMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomKeyMissingMessage(),
                    GWMCrates.getInstance().getLanguage(), "HAVE_NOT_KEY", Arrays.asList(
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static void sendPreviewNotAvailableMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendPreviewIsNotAvailableMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomPreviewIsNotAvailableMessage(),
                    GWMCrates.getInstance().getLanguage(), "PREVIEW_IS_NOT_AVAILABLE", Arrays.asList(
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static void sendNoPermissionToOpenMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendNoPermissionToOpenMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomNoPermissionToOpenMessage(),
                    GWMCrates.getInstance().getLanguage(), "HAVE_NOT_PERMISSION", Collections.emptyList()));
        }
    }

    public static void sendNoPermissionToPreviewMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendNoPermissionToPreviewMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomNoPermissionToPreviewMessage(),
                    GWMCrates.getInstance().getLanguage(), "HAVE_NOT_PERMISSION", Collections.emptyList()));
        }
    }

    public static void sendCrateDelayMessage(CommandSource source, Manager manager, long delay) {
        if (manager.getCustomMessageData().isSendCrateDelayMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomCrateDelayMessage(),
                    GWMCrates.getInstance().getLanguage(), "CRATE_OPEN_DELAY", Arrays.asList(
                            new ImmutablePair<>("TIME", GWMCratesUtils.millisToString(delay)),
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static void sendCannotOpenMessage(CommandSource source, Manager manager) {
        if (manager.getCustomMessageData().isSendCannotOpenManagerMessage()) {
            source.sendMessages(GWMLibraryUtils.getMessage(source, manager.getCustomMessageData().getCustomCannotOpenManagerMessage(),
                    GWMCrates.getInstance().getLanguage(), "CANNOT_OPEN_MANAGER", Arrays.asList(
                            new ImmutablePair<>("MANAGER_NAME", manager.getName()),
                            new ImmutablePair<>("MANAGER_ID", manager.id())
                    )));
        }
    }

    public static String formatManagers(List<Manager> managers) {
        StringBuilder builder = new StringBuilder();
        final int managersSize = managers.size();
        for (int i = 0; i < managersSize; i++) {
            Manager manager = managers.get(i);
            String id = manager.id();
            String name = manager.getName();
            List<Map.Entry<String, ?>> entries = Arrays.asList(
                    new ImmutablePair<>("MANAGER_ID", id),
                    new ImmutablePair<>("MANAGER_NAME", name)
            );
            if (i != managersSize - 1) {
                builder.append(GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().
                        getSimpleTranslation("MANAGER_LIST_FORMAT",
                                entries)));
            } else {
                builder.append(GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().
                        getSimpleTranslation("LAST_MANAGER_LIST_FORMAT",
                                entries)));
            }
        }
        return builder.toString();
    }

    public static String formatDrops(List<Drop> drops) {
        StringBuilder builder = new StringBuilder();
        final int dropsSize = drops.size();
        for (int i = 0; i < dropsSize; i++) {
            Drop drop = drops.get(i);
            String id = drop.id();
            String customName = drop.getCustomName().orElse(id);
            List<Map.Entry<String, ?>> entries = Arrays.asList(
                    new ImmutablePair<>("DROP_ID", id),
                    new ImmutablePair<>("DROP_CUSTOM_NAME", customName)
            );
            if (i != dropsSize - 1) {
                builder.append(GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().
                        getSimpleTranslation("DROP_LIST_FORMAT",
                        entries)));
            } else {
                builder.append(GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().
                        getSimpleTranslation("LAST_DROP_LIST_FORMAT",
                        entries)));
            }
        }
        return builder.toString();
    }

    public static String formatLocation(Location<World> location) {
        return GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().getSimpleTranslation("LOCATION_FORMAT", Arrays.asList(
                new ImmutablePair<>("WORLD_NAME", location.getExtent().getName()),
                new ImmutablePair<>("WORLD_UUID", location.getExtent().getUniqueId().toString()),
                new ImmutablePair<>("X", String.format("%.2f", location.getX())),
                new ImmutablePair<>("Y", String.format("%.2f", location.getY())),
                new ImmutablePair<>("Z", String.format("%.2f", location.getZ()))
        )));
    }
}
