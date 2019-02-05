package org.gwmdevelopments.sponge_plugin.crates.preview.previews;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.FirstOpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.AbstractPreview;
import org.gwmdevelopments.sponge_plugin.crates.util.DecorativeDropChangeRunnable;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.*;

public class FirstGuiPreview extends AbstractPreview {

    public static final Map<Container, Pair<FirstGuiPreview, Manager>> FIRST_GUI_CONTAINERS = new HashMap<>();

    public static final List<Integer> DECORATIVE_ITEMS_INDICES;

    private Optional<Text> displayName = Optional.empty();
    private List<ItemStack> decorativeItems;
    private int scrollDelay;
    private Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode = Optional.empty();

    static {
        List<Integer> decorativeItemsIndices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            decorativeItemsIndices.add(i);
        }
        for (int i = 17; i < 27; i++) {
            decorativeItemsIndices.add(i);
        }
        DECORATIVE_ITEMS_INDICES = Collections.unmodifiableList(decorativeItemsIndices);
    }

    public FirstGuiPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorativeItemsNode = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scrollDelayNode = node.getNode("SCROLL_DELAY");
            ConfigurationNode decorativeItemsChangeModeNode = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(displayNameNode.getString()));
            }
            decorativeItems = new ArrayList<>();
            if (!decorativeItemsNode.isVirtual()) {
                for (ConfigurationNode decorativeItemNode : decorativeItemsNode.getChildrenList()) {
                    decorativeItems.add(GWMLibraryUtils.parseItem(decorativeItemNode));
                }
            }
            scrollDelay = scrollDelayNode.getInt(10);
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of((DecorativeItemsChangeMode) GWMCratesUtils.createSuperObject(decorativeItemsChangeModeNode, SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE));
            }
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create First Gui Preview!", e);
        }
    }

    public FirstGuiPreview(Optional<String> id, Optional<Text> displayName, List<ItemStack> decorativeItems,
                           int scrollDelay, Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        super("FIRST", id);
        this.displayName = displayName;
        this.decorativeItems = decorativeItems;
        this.scrollDelay = scrollDelay;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }

    @Override
    public void preview(Player player, Manager manager) {
        Inventory.Builder builder = Inventory.builder().
                of(InventoryArchetypes.CHEST);
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        if (!decorativeItems.isEmpty()) {
            int index = 0;
            for (int i = 0; i < DECORATIVE_ITEMS_INDICES.size(); i++, index++) {
                if (index == decorativeItems.size()) {
                    index = 0;
                }
                ordered.getSlot(new SlotIndex(DECORATIVE_ITEMS_INDICES.get(i))).get().
                        set(decorativeItems.get(index));
            }
        }
        List<Drop> drops = manager.getDrops();
        int index = 0;
        for (int i = 10; i < 17; i++) {
            if (index == drops.size()) {
                index = 0;
            }
            ordered.getSlot(new SlotIndex(i)).get().set(drops.get(index).getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
            index++;
        }
        Container container = player.openInventory(inventory).get();
        FIRST_GUI_CONTAINERS.put(container, new Pair<>(this, manager));
        if (!decorativeItems.isEmpty()) {
            decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                    createTaskBuilder().delayTicks(mode.getChangeDelay()).
                    execute(new DecorativeDropChangeRunnable(player, container, ordered, new ArrayList<>(decorativeItems), mode, FirstOpenManager.DECORATIVE_ITEMS_INDICES)).
                    submit(GWMCrates.getInstance()));
        }
        Sponge.getScheduler().createTaskBuilder().delayTicks(scrollDelay).
                execute(new DropChangeRunnable(container, drops, index)).
                submit(GWMCrates.getInstance());
    }

    public class DropChangeRunnable implements Runnable {

        private Container container;
        private OrderedInventory inventory;
        private List<Drop> drops;
        private int index;

        public DropChangeRunnable(Container container, List<Drop> drops, int index) {
            this.container = container;
            this.inventory = GWMCratesUtils.castToOrdered(container.first());
            this.drops = drops;
            this.index = index;
        }

        @Override
        public void run() {
            if (!FIRST_GUI_CONTAINERS.containsKey(container)) {
                return;
            }
            for (int i = 10; i < 16; i++) {
                inventory.getSlot(new SlotIndex(i)).get().
                        set(inventory.getSlot(new SlotIndex(i + 1)).get().peek().
                                orElse(GWMCratesUtils.EMPTY_ITEM));
            }
            if (index == drops.size()) {
                index = 0;
            }
            inventory.getSlot(new SlotIndex(16)).get().
                    set(drops.get(index).getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
            index++;
            Sponge.getScheduler().createTaskBuilder().delayTicks(scrollDelay).execute(this).submit(GWMCrates.getInstance());
        }
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Optional<Text> displayName) {
        this.displayName = displayName;
    }

    public List<ItemStack> getDecorativeItems() {
        return decorativeItems;
    }

    public void setDecorativeItems(List<ItemStack> decorativeItems) {
        this.decorativeItems = decorativeItems;
    }

    public int getScrollDelay() {
        return scrollDelay;
    }

    public void setScrollDelay(int scrollDelay) {
        this.scrollDelay = scrollDelay;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public void setDecorativeItemsChangeMode(Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }
}
