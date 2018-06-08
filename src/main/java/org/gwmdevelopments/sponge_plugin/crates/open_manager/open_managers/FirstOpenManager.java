package org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
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
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.*;

public class FirstOpenManager extends OpenManager {

    public static final Map<Container, Pair<FirstOpenManager, Manager>> FIRST_GUI_CONTAINERS = new HashMap<>();
    public static final Set<Container> SHOWN_GUI = new HashSet<>();

    public static final List<Integer> DEFAULT_SCROLL_DELAYS = new ArrayList<>();

    static {
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j <= 10 - i; j++) {
                DEFAULT_SCROLL_DELAYS.add(i);
            }
        }
    }

    private Optional<Text> displayName = Optional.empty();
    private List<ItemStack> decorativeItems = GWMCratesUtils.DEFAULT_DECORATIVE_ITEMS;
    private List<Integer> scrollDelays = DEFAULT_SCROLL_DELAYS;
    private boolean clearDecorativeItems;
    private boolean clearOtherDrops;
    private int closeDelay;
    private boolean forbidClose;
    private Optional<SoundType> scrollSound = Optional.empty();
    private Optional<SoundType> winSound = Optional.empty();
    private Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode = Optional.empty();

    public FirstOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorativeItemsNode = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scrollDelaysNode = node.getNode("SCROLL_DELAYS");
            ConfigurationNode clearDecorativeItemsNode = node.getNode("CLEAR_DECORATIVE_ITEMS");
            ConfigurationNode clearOtherDropsNode = node.getNode("CLEAR_OTHER_DROPS");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbidCloseNode = node.getNode("FORBID_CLOSE");
            ConfigurationNode scrollSoundNode = node.getNode("SCROLL_SOUND");
            ConfigurationNode winSoundNode = node.getNode("WIN_SOUND");
            ConfigurationNode decorativeItemsChangeModeNode = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(displayNameNode.getString()));
            }
            if (!decorativeItemsNode.isVirtual()) {
                decorativeItems = new ArrayList<>();
                for (ConfigurationNode decorativeItemNode : decorativeItemsNode.getChildrenList()) {
                    decorativeItems.add(GWMCratesUtils.parseItem(decorativeItemNode));
                }
                if (decorativeItems.size() != 20) {
                    throw new RuntimeException("DECORATIVE_ITEMS size must be 20 instead of " + decorativeItems.size() + "!");
                }
            }
            if (!scrollDelaysNode.isVirtual()) {
                scrollDelays = scrollDelaysNode.getList(TypeToken.of(Integer.class));
            }
            clearDecorativeItems = clearDecorativeItemsNode.getBoolean(false);
            clearOtherDrops = clearOtherDropsNode.getBoolean(true);
            closeDelay = closeDelayNode.getInt(60);
            forbidClose = forbidCloseNode.getBoolean(true);
            if (!scrollSoundNode.isVirtual()) {
                scrollSound = Optional.of(scrollSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!winSoundNode.isVirtual()) {
                winSound = Optional.of(winSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of((DecorativeItemsChangeMode) GWMCratesUtils.createSuperObject(decorativeItemsChangeModeNode, SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create First Gui Open Manager!", e);
        }
    }

    public FirstOpenManager(Optional<String> id, Optional<SoundType> openSound, Optional<Text> displayName,
                            List<ItemStack> decorativeItems, List<Integer> scrollDelays,
                            boolean clearDecorativeItems, boolean clearOtherDrops,
                            int closeDelay, boolean forbidClose, Optional<SoundType> scrollSound,
                            Optional<SoundType> winSound, Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        super("FIRST", id, openSound);
        this.displayName = displayName;
        this.decorativeItems = decorativeItems;
        this.scrollDelays = scrollDelays;
        this.clearDecorativeItems = clearDecorativeItems;
        this.clearOtherDrops = clearOtherDrops;
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.scrollSound = scrollSound;
        this.winSound = winSound;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent openEvent = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(openEvent);
        if (openEvent.isCancelled()) {
            return;
        }
        Inventory inventory = displayName.map(text -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(text)).
                build(GWMCrates.getInstance())).orElseGet(() -> Inventory.builder().of(InventoryArchetypes.CHEST).
                build(GWMCrates.getInstance()));
        List<Drop> dropList = new ArrayList<>();
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        for (int i = 0; i < 10; i++) {
            ordered.getSlot(new SlotIndex(i)).get().set(decorativeItems.get(i));
        }
        for (int i = 10; i < 17; i++) {
            Drop new_drop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true);
            dropList.add(new_drop);
            ordered.getSlot(new SlotIndex(i)).get().set(new_drop.getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
        }
        for (int i = 17; i < 27; i++) {
            ordered.getSlot(new SlotIndex(i)).get().set(decorativeItems.get(i - 7));
        }
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        FIRST_GUI_CONTAINERS.put(container, new Pair<>(this, manager));
        decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                    createTaskBuilder().delayTicks(mode.getChangeDelay()).
                    execute(new DropChangeRunnable(player, container, ordered, new ArrayList<>(decorativeItems), mode)).
                    submit(GWMCrates.getInstance()));
        int waitTime = 0;
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            waitTime += scrollDelays.get(i);
            int finalI = i;
            Sponge.getScheduler().createTaskBuilder().delayTicks(waitTime).execute(() -> {
                for (int j = 10; j < 16; j++) {
                    ordered.getSlot(new SlotIndex(j)).get().set(ordered.getSlot(new SlotIndex(j + 1)).get().peek().
                            orElse(GWMCratesUtils.EMPTY_ITEM));
                }
                Drop newDrop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, !(finalI == scrollDelays.size() - 5));
                dropList.add(newDrop);
                ordered.getSlot(new SlotIndex(16)).get().set(newDrop.getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
                scrollSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
            }).submit(GWMCrates.getInstance());
        }
        Sponge.getScheduler().createTaskBuilder().delayTicks(waitTime + scrollDelays.get(scrollDelays.size() - 1)).execute(() -> {
            Drop drop = dropList.get(dropList.size() - 4);
            drop.apply(player);
            winSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
            if (clearDecorativeItems) {
                for (int i = 0; i < 10; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(GWMCratesUtils.EMPTY_ITEM);
                }
                for (int i = 17; i < 27; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(GWMCratesUtils.EMPTY_ITEM);
                }
            }
            if (clearOtherDrops) {
                for (int i = 10; i < 13; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(GWMCratesUtils.EMPTY_ITEM);
                }
                for (int i = 14; i < 17; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(GWMCratesUtils.EMPTY_ITEM);
                }
            }
            SHOWN_GUI.add(container);
            PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, drop);
            Sponge.getEventManager().post(openedEvent);
        }).submit(GWMCrates.getInstance());
        Sponge.getScheduler().createTaskBuilder().delayTicks(waitTime + scrollDelays.get(scrollDelays.size() - 1) + closeDelay).execute(() -> {
            Optional<Container> optionalOpenInventory = player.getOpenInventory();
            if (optionalOpenInventory.isPresent() && container.equals(optionalOpenInventory.get())) {
                player.closeInventory();
            }
            SHOWN_GUI.remove(container);
            FIRST_GUI_CONTAINERS.remove(container);
        }).submit(GWMCrates.getInstance());
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

    public List<Integer> getScrollDelays() {
        return scrollDelays;
    }

    public void setScrollDelays(List<Integer> scrollDelays) {
        this.scrollDelays = scrollDelays;
    }

    public boolean isClearDecorativeItems() {
        return clearDecorativeItems;
    }

    public void setClearDecorativeItems(boolean clearDecorativeItems) {
        this.clearDecorativeItems = clearDecorativeItems;
    }

    public boolean isClearOtherDrops() {
        return clearOtherDrops;
    }

    public void setClearOtherDrops(boolean clearOtherDrops) {
        this.clearOtherDrops = clearOtherDrops;
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

    public Optional<SoundType> getScrollSound() {
        return scrollSound;
    }

    public void setScrollSound(Optional<SoundType> scrollSound) {
        this.scrollSound = scrollSound;
    }

    public Optional<SoundType> getWinSound() {
        return winSound;
    }

    public void setWinSound(Optional<SoundType> winSound) {
        this.winSound = winSound;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public void setDecorativeItemsChangeMode(Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }

    public static class DropChangeRunnable implements Runnable {

        private Player player;
        private Container container;
        private OrderedInventory ordered;
        private List<ItemStack> decorativeItems;
        private DecorativeItemsChangeMode decorativeItemsChangeMode;

        public DropChangeRunnable(Player player, Container container, OrderedInventory ordered,
                                  List<ItemStack> decorativeItems,
                                  DecorativeItemsChangeMode decorativeItemsChangeMode) {
            this.player = player;
            this.container = container;
            this.ordered = ordered;
            this.decorativeItems = decorativeItems;
            this.decorativeItemsChangeMode = decorativeItemsChangeMode;
        }

        @Override
        public void run() {
            Optional<Container> openInventory = player.getOpenInventory();
            if (openInventory.isPresent() && openInventory.get().equals(container)) {
                decorativeItems = decorativeItemsChangeMode.shuffle(decorativeItems);
                for (int i = 0; i < 10; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(decorativeItems.get(i));
                }
                for (int i = 17; i < 27; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(decorativeItems.get(i - 7));
                }
                Sponge.getScheduler().createTaskBuilder().
                        delayTicks(decorativeItemsChangeMode.getChangeDelay()).
                        execute(this).submit(GWMCrates.getInstance());
            }
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public Container getContainer() {
            return container;
        }

        public void setContainer(Container container) {
            this.container = container;
        }

        public OrderedInventory getOrdered() {
            return ordered;
        }

        public void setOrdered(OrderedInventory ordered) {
            this.ordered = ordered;
        }

        public List<ItemStack> getDecorativeItems() {
            return decorativeItems;
        }

        public void setDecorativeItems(List<ItemStack> decorativeItems) {
            this.decorativeItems = decorativeItems;
        }

        public DecorativeItemsChangeMode getDecorativeItemsChangeMode() {
            return decorativeItemsChangeMode;
        }

        public void setDecorativeItemsChangeMode(DecorativeItemsChangeMode decorativeItemsChangeMode) {
            this.decorativeItemsChangeMode = decorativeItemsChangeMode;
        }
    }
}
