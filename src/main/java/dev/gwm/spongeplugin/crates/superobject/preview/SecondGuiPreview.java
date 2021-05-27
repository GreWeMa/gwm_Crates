package dev.gwm.spongeplugin.crates.superobject.preview;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.AbstractPreview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
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
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SecondGuiPreview extends AbstractPreview {

    public static final String TYPE = "SECOND";

    public static final Map<Container, Information> SECOND_GUI_CONTAINERS = new HashMap<>();

    public static final ItemStack DEFAULT_NEXT_PAGE_ITEM = ItemStack.builder().
            itemType(ItemTypes.STAINED_GLASS_PANE).
            add(Keys.DYE_COLOR, DyeColors.GREEN).
            add(Keys.DISPLAY_NAME, Text.builder("NEXT").
                    color(TextColors.GREEN).
                    style(TextStyles.BOLD).
                    build()).
            build();

    public static final ItemStack DEFAULT_PREVIOUS_PAGE_ITEM = ItemStack.builder().
            itemType(ItemTypes.STAINED_GLASS_PANE).
            add(Keys.DYE_COLOR, DyeColors.RED).
            add(Keys.DISPLAY_NAME, Text.builder("PREVIOUS").
                    color(TextColors.DARK_RED).
                    style(TextStyles.BOLD).
                    build()).
            build();

    private final Optional<Text> displayName;
    private final Optional<Integer> rows;
    private final boolean enablePages;
    private final ItemStack nextPageItem;
    private final ItemStack previousPageItem;

    public SecondGuiPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode rowsNode = node.getNode("ROWS");
            ConfigurationNode enablePagesNode = node.getNode("ENABLE_PAGES");
            ConfigurationNode nextPageItemNode = node.getNode("NEXT_PAGE_ITEM");
            ConfigurationNode previousPageItemNode = node.getNode("PREVIOUS_PAGE_ITEM");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(displayNameNode.getValue(TypeToken.of(Text.class)));
            } else {
                displayName = Optional.empty();
            }
            if (!rowsNode.isVirtual()) {
                int tempRows = rowsNode.getInt(6);
                if (tempRows < 1 || tempRows > 6) {
                    throw new RuntimeException("ROWS is greater than 6 or less than 1!");
                }
                rows = Optional.of(tempRows);
            } else {
                rows = Optional.empty();
            }
            enablePages = enablePagesNode.getBoolean(false);
            if (!nextPageItemNode.isVirtual()) {
                nextPageItem = GWMLibraryUtils.parseItem(nextPageItemNode);
            } else {
                nextPageItem = DEFAULT_NEXT_PAGE_ITEM;
            }
            if (!previousPageItemNode.isVirtual()) {
                previousPageItem = GWMLibraryUtils.parseItem(previousPageItemNode);
            } else {
                previousPageItem = DEFAULT_PREVIOUS_PAGE_ITEM;
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public SecondGuiPreview(String id, boolean showEmptyDrops, Optional<List<Drop>> customDrops,
                            Optional<Text> displayName, Optional<Integer> rows, boolean enablePages,
                            ItemStack nextPageItem, ItemStack previousPageItem) {
        super(id, showEmptyDrops, customDrops);
        this.displayName = displayName;
        if (rows.isPresent()) {
            int rowsValue = rows.get();
            if (rowsValue < 1 || rowsValue > 6) {
                throw new RuntimeException("ROWS is greater than 6 or less than 1!");
            }
        }
        this.rows = rows;
        this.enablePages = enablePages;
        this.nextPageItem = nextPageItem;
        this.previousPageItem = previousPageItem;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void preview(Player player, Manager manager) {
        List<Drop> drops = getDrops(manager);
        int rows = this.rows.orElse(GWMCratesUtils.getInventoryHeight(drops.size()));
        InventoryDimension dimension = new InventoryDimension(9, rows);
        Inventory.Builder builder = Inventory.builder().
                of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, dimension);
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        fillInventory(ordered, drops, rows, 0);
        Container container = player.openInventory(inventory).get();
        SECOND_GUI_CONTAINERS.put(container, new Information(this, manager, rows));
    }

    public void fillInventory(OrderedInventory ordered, List<Drop> drops, int rows, int page) {
        final int size = 9 * rows;
        final int prevPageItemIndex = size - 9;
        final int nextPageItemIndex = size - 1;
        final int toSkip = (size - 2) * page;
        ordered.clear();
        if (enablePages) {
            ordered.getSlot(new SlotIndex(prevPageItemIndex)).get().set(previousPageItem);
            ordered.getSlot(new SlotIndex(nextPageItemIndex)).get().set(nextPageItem);
        }
        int dropIndex = toSkip;
        for (int i = 0; i < size && dropIndex < drops.size(); i++) {
            ItemStack dropItem = drops.get(dropIndex).getDropItem().get();
            if (enablePages && (i == prevPageItemIndex || i == nextPageItemIndex)) {
                continue;
            } else {
                ordered.getSlot(new SlotIndex(i)).get().set(dropItem);
                dropIndex++;
            }
        }
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public Optional<Integer> getRows() {
        return rows;
    }

    public boolean isEnablePages() {
        return enablePages;
    }

    public ItemStack getNextPageItem() {
        return nextPageItem;
    }

    public ItemStack getPreviousPageItem() {
        return previousPageItem;
    }

    public static class Information {

        private final SecondGuiPreview preview;
        private final Manager manager;
        private final int inventoryRows;
        private int page = 0;

        public Information(SecondGuiPreview preview, Manager manager, int inventoryRows) {
            this.preview = preview;
            this.manager = manager;
            this.inventoryRows = inventoryRows;
        }

        public SecondGuiPreview getPreview() {
            return preview;
        }

        public Manager getManager() {
            return manager;
        }

        public int getInventoryRows() {
            return inventoryRows;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}
