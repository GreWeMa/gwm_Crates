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
import org.gwmdevelopments.sponge_plugin.crates.util.DecorativeDropChangeRunnable;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
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

import java.util.*;

public class FirstOpenManager extends OpenManager {

    public static final Map<Container, Pair<FirstOpenManager, Manager>> FIRST_GUI_CONTAINERS = new HashMap<>();
    public static final Set<Container> SHOWN_GUI = new HashSet<>();

    public static final List<Integer> DEFAULT_SCROLL_DELAYS;

    public static final List<Integer> DECORATIVE_ITEMS_INDICES;

    static {
        List<Integer> defaultScrollDelays = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j <= 10 - i; j++) {
                defaultScrollDelays.add(i);
            }
        }
        defaultScrollDelays.add(20);
        defaultScrollDelays.add(40);
        DEFAULT_SCROLL_DELAYS = Collections.unmodifiableList(defaultScrollDelays);
        List<Integer> decorativeItemsIndices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            decorativeItemsIndices.add(i);
        }
        for (int i = 17; i < 27; i++) {
            decorativeItemsIndices.add(i);
        }
        DECORATIVE_ITEMS_INDICES = Collections.unmodifiableList(decorativeItemsIndices);
    }

    private Optional<Text> displayName = Optional.empty();
    private List<ItemStack> decorativeItems;
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
            decorativeItems = new ArrayList<>();
            if (!decorativeItemsNode.isVirtual()) {
                for (ConfigurationNode decorativeItemNode : decorativeItemsNode.getChildrenList()) {
                    decorativeItems.add(GWMCratesUtils.parseItem(decorativeItemNode));
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
            throw new RuntimeException("Failed to create First Open Manager!", e);
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
        Inventory.Builder builder = Inventory.builder().
                of(InventoryArchetypes.CHEST);
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        List<Drop> dropList = new ArrayList<>();
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
        for (int i = 10; i < 17; i++) {
            ordered.getSlot(new SlotIndex(i)).get().
                    set(GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true).
                            getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
        }
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        FIRST_GUI_CONTAINERS.put(container, new Pair<>(this, manager));
        if (!decorativeItems.isEmpty()) {
            decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                    createTaskBuilder().delayTicks(mode.getChangeDelay()).
                    execute(new DecorativeDropChangeRunnable(player, container, ordered, new ArrayList<>(decorativeItems), mode, DECORATIVE_ITEMS_INDICES)).
                    submit(GWMCrates.getInstance()));
        }
        int waitTime = 0;
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            waitTime += scrollDelays.get(i);
            int finalI = i;
            Sponge.getScheduler().createTaskBuilder().
                    delayTicks(waitTime).
                    execute(() -> {
                        for (int j = 10; j < 16; j++) {
                            ordered.getSlot(new SlotIndex(j)).get().set(ordered.getSlot(new SlotIndex(j + 1)).get().peek().
                                    orElse(GWMCratesUtils.EMPTY_ITEM));
                        }
                        Drop newDrop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, finalI != scrollDelays.size() - 5);
                        dropList.add(newDrop);
                        ordered.getSlot(new SlotIndex(16)).get().set(newDrop.getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
                        scrollSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    }).submit(GWMCrates.getInstance());
        }
        waitTime += scrollDelays.get(scrollDelays.size() - 1);
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() -> {
                    Drop drop = dropList.get(dropList.size() - 4);
                    drop.apply(player);
                    winSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    if (clearDecorativeItems) {
                        for (int i = 0; i < DECORATIVE_ITEMS_INDICES.size(); i++) {
                            ordered.getSlot(new SlotIndex(DECORATIVE_ITEMS_INDICES.get(i))).get().
                                    set(GWMCratesUtils.EMPTY_ITEM);
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
        waitTime += closeDelay;
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() -> {
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
}
