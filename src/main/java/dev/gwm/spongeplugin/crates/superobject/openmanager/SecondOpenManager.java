package dev.gwm.spongeplugin.crates.superobject.openmanager;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenCrateEvent;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SecondOpenManager extends AbstractOpenManager {

    public static final String TYPE = "SECOND";

    public static final Map<Container, Data> SECOND_GUI_INVENTORIES = new HashMap<>();

    private static final ItemStack DEFAULT_HIDDEN_ITEM = ItemStack.of(ItemTypes.CHEST);

    private final Optional<Text> displayName;
    private final ItemStack hiddenItem;
    private final boolean increaseHiddenItemQuantity;
    private final int rows;
    private final int dropsAmount;
    private final boolean showOtherDrops;
    private final int showOtherDropsDelay;
    private final int closeDelay;
    private final boolean forbidClose;
    private final boolean giveRandomOnClose;
    private final Optional<SoundType> clickSound;

    public SecondOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode hiddenItemNode = node.getNode("HIDDEN_ITEM");
            ConfigurationNode increaseHiddenItemQuantityNode = node.getNode("INCREASE_HIDDEN_ITEM_QUANTITY");
            ConfigurationNode rowsNode = node.getNode("ROWS");
            ConfigurationNode dropsAmountNode = node.getNode("DROPS_AMOUNT");
            ConfigurationNode showOtherDropsNode = node.getNode("SHOW_OTHER_DROPS");
            ConfigurationNode showOtherDropsDelayNode = node.getNode("SHOW_OTHER_DROPS_DELAY");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbidCloseNode = node.getNode("FORBID_CLOSE");
            ConfigurationNode giveRandomOnCloseNode = node.getNode("GIVE_RANDOM_ON_CLOSE");
            ConfigurationNode clickSoundNode = node.getNode("CLICK_SOUND");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(displayNameNode.getValue(TypeToken.of(Text.class)));
            } else {
                displayName = Optional.empty();
            }
            if (!hiddenItemNode.isVirtual()) {
                hiddenItem = GWMLibraryUtils.parseItem(hiddenItemNode);
            } else {
                hiddenItem = DEFAULT_HIDDEN_ITEM;
            }
            increaseHiddenItemQuantity = increaseHiddenItemQuantityNode.getBoolean(true);
            rows = rowsNode.getInt(6);
            if (rows < 1 || rows > 6) {
                throw new RuntimeException("ROWS is greater than 6 or less than 1!");
            }
            dropsAmount = dropsAmountNode.getInt(1);
            final int maxDropsToChoose = rows * 9;
            if (dropsAmount < 0 || dropsAmount > maxDropsToChoose) {
                throw new RuntimeException("DROPS_AMOUNT is less than 0 or greater than " + maxDropsToChoose + "!");
            }
            showOtherDrops = showOtherDropsNode.getBoolean(true);
            showOtherDropsDelay = showOtherDropsDelayNode.getInt(20);
            closeDelay = closeDelayNode.getInt(60);
            if (closeDelay <= showOtherDropsDelay) {
                throw new RuntimeException("SHOW_OTHER_DROPS_DELAY is greater than or equal to CLOSE DELAY!");
            }
            forbidClose = forbidCloseNode.getBoolean(true);
            giveRandomOnClose = giveRandomOnCloseNode.getBoolean(true);
            if (!clickSoundNode.isVirtual()) {
                clickSound = Optional.of(clickSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                clickSound = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public SecondOpenManager(String id, Optional<SoundType> openSound, Optional<Text> displayName,
                             ItemStack hiddenItem, boolean increaseHiddenItemQuantity,
                             int rows, int dropsAmount, boolean showOtherDrops,
                             int showOtherDropsDelay, int closeDelay, boolean forbidClose, boolean giveRandomOnClose,
                             Optional<SoundType> clickSound) {
        super(id, openSound);
        this.displayName = displayName;
        this.hiddenItem = hiddenItem;
        this.increaseHiddenItemQuantity = increaseHiddenItemQuantity;
        if (rows < 1 || rows > 6) {
            throw new RuntimeException("ROWS is greater than 6 or less than 1!");
        }
        this.rows = rows;
        final int maxDropsToChoose = rows * 9;
        if (dropsAmount < 0 || dropsAmount > maxDropsToChoose) {
            throw new RuntimeException("DROPS_AMOUNT is less than 0 or greater than " + maxDropsToChoose + "!");
        }
        this.dropsAmount = dropsAmount;
        this.showOtherDrops = showOtherDrops;
        this.showOtherDropsDelay = showOtherDropsDelay;
        if (closeDelay <= showOtherDropsDelay) {
            throw new RuntimeException("SHOW_OTHER_DROPS_DELAY is greater than or equal to CLOSE DELAY!");
        }
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.giveRandomOnClose = giveRandomOnClose;
        this.clickSound = clickSound;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent openEvent = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Inventory.Builder builder = Inventory.builder().
                of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, rows));
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        int hiddenItemQuantity = hiddenItem.getQuantity();
        for (int i = 0; i < 9 * rows; i++) {
            int quantity = increaseHiddenItemQuantity ? i + 1 : hiddenItemQuantity;
            ItemStack copy = hiddenItem.copy();
            copy.setQuantity(quantity);
            ordered.getSlot(new SlotIndex(i)).get().set(copy);
        }
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(openSound -> player.playSound(openSound, player.getLocation().getPosition(), 1.));
        SECOND_GUI_INVENTORIES.put(container, new Data(this, manager));
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public ItemStack getHiddenItem() {
        return hiddenItem;
    }

    public boolean isIncreaseHiddenItemQuantity() {
        return increaseHiddenItemQuantity;
    }

    public int getRows() {
        return rows;
    }

    public int getDropsAmount() {
        return dropsAmount;
    }

    public boolean isShowOtherDrops() {
        return showOtherDrops;
    }

    public int getShowOtherDropsDelay() {
        return showOtherDropsDelay;
    }

    public int getCloseDelay() {
        return closeDelay;
    }

    public boolean isForbidClose() {
        return forbidClose;
    }

    public boolean isGiveRandomOnClose() {
        return giveRandomOnClose;
    }

    public Optional<SoundType> getClickSound() {
        return clickSound;
    }

    public static class Data {

        private final SecondOpenManager openManager;
        private final Manager manager;

        private final Map<Integer, Drop> openedIndices = new HashMap<>();

        public Data(SecondOpenManager openManager, Manager manager) {
            this.openManager = openManager;
            this.manager = manager;
        }

        public Drop openIndex(int index, Player target) {
            final int maxIndex = openManager.rows * 9;
            if (index < 0 || index >= maxIndex) {
                throw new IllegalArgumentException("Invalid index " + index + "! Valid values are from 0 to " + (maxIndex - 1));
            }
            if (openedIndices.containsKey(index)) {
                throw new IllegalArgumentException("The index " + index + " is already in opened indices set!");
            }
            Drop drop = (Drop) manager.getRandomManager().choose(manager.getDrops(), target, false);
            openedIndices.put(index, drop);
            return drop;
        }

        public boolean isOpened() {
            return openManager.getDropsAmount() == openedIndices.size();
        }

        public SecondOpenManager getOpenManager() {
            return openManager;
        }

        public Manager getManager() {
            return manager;
        }

        public Map<Integer, Drop> getOpenedIndices() {
            return Collections.unmodifiableMap(openedIndices);
        }
    }
}
