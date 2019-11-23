package dev.gwm.spongeplugin.crates.superobject.preview;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.changemode.base.DecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.FirstOpenManager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.AbstractPreview;
import dev.gwm.spongeplugin.crates.util.DecorativeItemsChangeRunnable;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

import java.util.*;
import java.util.stream.Collectors;

public final class FirstGuiPreview extends AbstractPreview {

    public static final String TYPE = "FIRST";

    public static final Map<Container, Pair<FirstGuiPreview, Manager>> FIRST_GUI_CONTAINERS = new HashMap<>();

    public static final List<Integer> DECORATIVE_ITEMS_INDICES;

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

    private final Optional<Text> displayName;
    private final List<ItemStack> decorativeItems;
    private final int scrollDelay;
    private final Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode;

    public FirstGuiPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorativeItemsNode = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scrollDelayNode = node.getNode("SCROLL_DELAY");
            ConfigurationNode decorativeItemsChangeModeNode = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(displayNameNode.getValue(TypeToken.of(Text.class)));
            } else {
                displayName = Optional.empty();
            }
            List<ItemStack> tempDecorativeItems = new ArrayList<>();
            if (!decorativeItemsNode.isVirtual()) {
                for (ConfigurationNode decorativeItemNode : decorativeItemsNode.getChildrenList()) {
                    tempDecorativeItems.add(GWMLibraryUtils.parseItem(decorativeItemNode));
                }
            }
            decorativeItems = Collections.unmodifiableList(tempDecorativeItems);
            scrollDelay = scrollDelayNode.getInt(10);
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of(Sponge.getServiceManager().provide(SuperObjectService.class).get().
                        create(GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE, decorativeItemsChangeModeNode));
            } else {
                decorativeItemsChangeMode = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public FirstGuiPreview(String id, Optional<List<Drop>> customDrops,
                           Optional<Text> displayName, List<ItemStack> decorativeItems,
                           int scrollDelay, Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        super(id, customDrops);
        this.displayName = displayName;
        this.decorativeItems = Collections.unmodifiableList(decorativeItems);
        this.scrollDelay = scrollDelay;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        decorativeItemsChangeMode.ifPresent(set::add);
        return set;
    }

    @Override
    public String type() {
        return TYPE;
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
        List<Drop> drops = getCustomDrops().orElse(manager.getDrops()).
                stream().
                filter(Drop::isShowInPreview).
                collect(Collectors.toList());
        if (!drops.isEmpty()) {
            int index = 0;
            for (int i = 10; i < 17; i++) {
                if (index == drops.size()) {
                    index = 0;
                }
                ordered.getSlot(new SlotIndex(i)).get().set(drops.get(index).getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
                index++;
            }
            Container container = player.openInventory(inventory).get();
            FIRST_GUI_CONTAINERS.put(container, new ImmutablePair<>(this, manager));
            if (!decorativeItems.isEmpty()) {
                decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                        createTaskBuilder().delayTicks(mode.getChangeDelay()).
                        execute(new DecorativeItemsChangeRunnable(player, container, ordered, mode, FirstOpenManager.DECORATIVE_ITEMS_INDICES, decorativeItems)).
                        submit(GWMCrates.getInstance()));
            }
            Sponge.getScheduler().createTaskBuilder().delayTicks(scrollDelay).
                    execute(new DropChangeRunnable(container, drops, index)).
                    submit(GWMCrates.getInstance());
        }
    }

    public class DropChangeRunnable implements Runnable {

        private final Container container;
        private final OrderedInventory inventory;
        private final List<Drop> drops;
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
            Sponge.getScheduler().createTaskBuilder().
                    delayTicks(scrollDelay).
                    execute(this).
                    submit(GWMCrates.getInstance());
        }
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public List<ItemStack> getDecorativeItems() {
        return decorativeItems;
    }

    public int getScrollDelay() {
        return scrollDelay;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }
}
