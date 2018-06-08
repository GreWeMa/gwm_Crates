package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
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
import org.spongepowered.api.text.serializer.TextSerializers;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SecondOpenManager extends OpenManager {

    public static final Map<Container, Pair<SecondOpenManager, Manager>> SECOND_GUI_INVENTORIES = new HashMap<>();

    public static final ItemStack DEFAULT_HIDDEN_ITEM = ItemStack.of(ItemTypes.CHEST, 1);

    private Optional<Text> displayName = Optional.empty();
    private ItemStack hiddenItem = DEFAULT_HIDDEN_ITEM;
    private boolean increaseHiddenItemQuantity;
    private int rows;
    private boolean showOtherDrops;
    private int showOtherDropsDelay;
    private int closeDelay;
    private boolean forbidClose;
    private boolean giveRandomOnClose;
    private Optional<SoundType> clickSound = Optional.empty();

    public SecondOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode hiddenItemNode = node.getNode("HIDDEN_ITEM");
            ConfigurationNode increaseHiddenItemQuantityNode = node.getNode("INCREASE_HIDDEN_ITEM_QUANTITY");
            ConfigurationNode rowsNode = node.getNode("ROWS");
            ConfigurationNode showOtherDropsNode = node.getNode("SHOW_OTHER_DROPS");
            ConfigurationNode showOtherDropsDelayNode = node.getNode("SHOW_OTHER_DROPS_DELAY");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbidCloseNode = node.getNode("FORBID_CLOSE");
            ConfigurationNode giveRandomOnCloseNode = node.getNode("GIVE_RANDOM_ON_CLOSE");
            ConfigurationNode clickSoundNode = node.getNode("CLICK_SOUND");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(displayNameNode.getString()));
            }
            if (!hiddenItemNode.isVirtual()) {
                hiddenItem = GWMCratesUtils.parseItem(hiddenItemNode);
            }
            increaseHiddenItemQuantity = increaseHiddenItemQuantityNode.getBoolean(true);
            rows = rowsNode.getInt(3);
            if (rows < 1 || rows > 6) {
                GWMCrates.getInstance().getLogger().info("ROWS value is more than 6 or less than 1! Force set it to 3!");
                rows = 3;
            }
            showOtherDrops = showOtherDropsNode.getBoolean(true);
            showOtherDropsDelay = showOtherDropsDelayNode.getInt(20);
            closeDelay = closeDelayNode.getInt(60);
            if (closeDelay <= showOtherDropsDelay) {
                GWMCrates.getInstance().getLogger().info("SHOW OTHER DROPS DELAY is more or equal to CLOSE DELAY! Force set it to 0!");
                showOtherDropsDelay = 0;
            }
            forbidClose = forbidCloseNode.getBoolean(true);
            giveRandomOnClose = giveRandomOnCloseNode.getBoolean(true);
            if (!clickSoundNode.isVirtual()) {
                clickSound = Optional.of(clickSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Second Gui Open Manager!", e);
        }
    }

    public SecondOpenManager(Optional<String> id, Optional<SoundType> openSound, Optional<Text> displayName,
                             ItemStack hiddenItem, boolean increaseHiddenItemQuantity,
                             int rows, boolean showOtherDrops, int showOtherDropsDelay, int closeDelay,
                             boolean forbidClose, boolean giveRandomOnClose,
                             Optional<SoundType> clickSound) {
        super("SECOND", id, openSound);
        this.displayName = displayName;
        this.hiddenItem = hiddenItem;
        this.increaseHiddenItemQuantity = increaseHiddenItemQuantity;
        this.rows = rows;
        this.showOtherDrops = showOtherDrops;
        this.showOtherDropsDelay = showOtherDropsDelay;
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.giveRandomOnClose = giveRandomOnClose;
        this.clickSound = clickSound;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent openEvent = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Inventory inventory = displayName.map(text -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, rows)).
                property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(text)).
                build(GWMCrates.getInstance())).orElseGet(() -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, rows)).
                build(GWMCrates.getInstance()));
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
        SECOND_GUI_INVENTORIES.put(container, new Pair<>(this, manager));
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Optional<Text> displayName) {
        this.displayName = displayName;
    }

    public ItemStack getHiddenItem() {
        return hiddenItem;
    }

    public void setHiddenItem(ItemStack hiddenItem) {
        this.hiddenItem = hiddenItem;
    }

    public boolean isIncreaseHiddenItemQuantity() {
        return increaseHiddenItemQuantity;
    }

    public void setIncreaseHiddenItemQuantity(boolean increaseHiddenItemQuantity) {
        this.increaseHiddenItemQuantity = increaseHiddenItemQuantity;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isShowOtherDrops() {
        return showOtherDrops;
    }

    public void setShowOtherDrops(boolean showOtherDrops) {
        this.showOtherDrops = showOtherDrops;
    }

    public int getShowOtherDropsDelay() {
        return showOtherDropsDelay;
    }

    public void setShowOtherDropsDelay(int showOtherDropsDelay) {
        this.showOtherDropsDelay = showOtherDropsDelay;
    }

    public int getCloseDelay() {
        return closeDelay;
    }

    public void setCloseDelay(int closeDelay) {
        this.closeDelay = closeDelay;
    }

    public boolean isForbidClose() {
        return forbidClose;
    }

    public void setForbidClose(boolean forbidClose) {
        this.forbidClose = forbidClose;
    }

    public boolean isGiveRandomOnClose() {
        return giveRandomOnClose;
    }

    public void setGiveRandomOnClose(boolean giveRandomOnClose) {
        this.giveRandomOnClose = giveRandomOnClose;
    }

    public Optional<SoundType> getClickSound() {
        return clickSound;
    }

    public void setClickSound(Optional<SoundType> clickSound) {
        this.clickSound = clickSound;
    }
}
