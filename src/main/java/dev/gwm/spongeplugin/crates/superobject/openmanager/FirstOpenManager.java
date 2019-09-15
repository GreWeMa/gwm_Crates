package dev.gwm.spongeplugin.crates.superobject.openmanager;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenCrateEvent;
import dev.gwm.spongeplugin.crates.event.PlayerOpenedCrateEvent;
import dev.gwm.spongeplugin.crates.superobject.changemode.base.DecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
import dev.gwm.spongeplugin.crates.utils.DecorativeItemsChangeRunnable;
import dev.gwm.spongeplugin.crates.utils.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.Pair;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
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

import java.util.*;

public final class FirstOpenManager extends AbstractOpenManager {

    public static final String TYPE = "FIRST";

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

    private final Optional<Text> displayName;
    private final List<ItemStack> decorativeItems;
    private final List<Integer> scrollDelays;
    private final boolean clearDecorativeItems;
    private final boolean clearOtherDrops;
    private final int closeDelay;
    private final boolean forbidClose;
    private final Optional<SoundType> scrollSound;
    private final Optional<SoundType> winSound;
    private final Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode;

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
            if (!scrollDelaysNode.isVirtual()) {
                scrollDelays = Collections.unmodifiableList(scrollDelaysNode.getList(TypeToken.of(Integer.class)));
            } else {
                scrollDelays = DEFAULT_SCROLL_DELAYS;
            }
            clearDecorativeItems = clearDecorativeItemsNode.getBoolean(false);
            clearOtherDrops = clearOtherDropsNode.getBoolean(true);
            closeDelay = closeDelayNode.getInt(60);
            forbidClose = forbidCloseNode.getBoolean(true);
            if (!scrollSoundNode.isVirtual()) {
                scrollSound = Optional.of(scrollSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                scrollSound = Optional.empty();
            }
            if (!winSoundNode.isVirtual()) {
                winSound = Optional.of(winSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                winSound = Optional.empty();
            }
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of(Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE, decorativeItemsChangeModeNode));
            } else {
                decorativeItemsChangeMode = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public FirstOpenManager(Optional<String> id, Optional<SoundType> openSound, Optional<Text> displayName,
                            List<ItemStack> decorativeItems, List<Integer> scrollDelays,
                            boolean clearDecorativeItems, boolean clearOtherDrops,
                            int closeDelay, boolean forbidClose, Optional<SoundType> scrollSound,
                            Optional<SoundType> winSound, Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        super(id, openSound);
        this.displayName = displayName;
        this.decorativeItems = Collections.unmodifiableList(decorativeItems);
        this.scrollDelays = Collections.unmodifiableList(scrollDelays);
        this.clearDecorativeItems = clearDecorativeItems;
        this.clearOtherDrops = clearOtherDrops;
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.scrollSound = scrollSound;
        this.winSound = winSound;
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
                    set(((Drop) manager.getRandomManager().choose(manager.getDrops(), player, true)).
                            getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
        }
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        FIRST_GUI_CONTAINERS.put(container, new Pair<>(this, manager));
        if (!decorativeItems.isEmpty()) {
            decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                    createTaskBuilder().delayTicks(mode.getChangeDelay()).
                    execute(new DecorativeItemsChangeRunnable(player, container, ordered, mode, DECORATIVE_ITEMS_INDICES, decorativeItems)).
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
                        Drop newDrop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, finalI != scrollDelays.size() - 5);
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
                    drop.give(player, 1);
                    winSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    if (clearDecorativeItems) {
                        for (Integer decorative_item_index : DECORATIVE_ITEMS_INDICES) {
                            ordered.getSlot(new SlotIndex(decorative_item_index)).get().
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
                    PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, Collections.singletonList(drop));
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

    public List<ItemStack> getDecorativeItems() {
        return decorativeItems;
    }

    public List<Integer> getScrollDelays() {
        return scrollDelays;
    }

    public boolean isClearDecorativeItems() {
        return clearDecorativeItems;
    }

    public boolean isClearOtherDrops() {
        return clearOtherDrops;
    }

    public int getCloseDelay() {
        return closeDelay;
    }

    public boolean isForbidClose() {
        return forbidClose;
    }

    public Optional<SoundType> getScrollSound() {
        return scrollSound;
    }

    public Optional<SoundType> getWinSound() {
        return winSound;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }
}
