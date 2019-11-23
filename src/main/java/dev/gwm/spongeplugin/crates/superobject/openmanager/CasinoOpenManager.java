package dev.gwm.spongeplugin.crates.superobject.openmanager;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenCrateEvent;
import dev.gwm.spongeplugin.crates.event.PlayerOpenedCrateEvent;
import dev.gwm.spongeplugin.crates.superobject.changemode.base.DecorativeItemsChangeMode;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.openmanager.base.AbstractOpenManager;
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
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;

import java.util.*;

public final class CasinoOpenManager extends AbstractOpenManager {

    public static final String TYPE = "CASINO";

    public static final Map<Container, Pair<CasinoOpenManager, Manager>> CASINO_GUI_CONTAINERS = new HashMap<>();
    public static final Set<Container> SHOWN_GUI = new HashSet<>();

    public static final List<List<Integer>> ROW_INDICES;

    public static final List<Integer> DEFAULT_SCROLL_DELAYS;

    public static final List<Integer> DECORATIVE_ITEMS_INDICES;

    static {
        List<Integer> firstRowIndices = new ArrayList<>();
        List<Integer> secondRowIndices = new ArrayList<>();
        List<Integer> thirdRowIndices = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            firstRowIndices.add(9 * i + 3);
            secondRowIndices.add(9 * i + 4);
            thirdRowIndices.add(9 * i + 5);
        }
        List<List<Integer>> rowIndices = new ArrayList<>();
        rowIndices.add(Collections.unmodifiableList(firstRowIndices));
        rowIndices.add(Collections.unmodifiableList(secondRowIndices));
        rowIndices.add(Collections.unmodifiableList(thirdRowIndices));
        ROW_INDICES = Collections.unmodifiableList(rowIndices);
        List<Integer> defaultScrollDelays = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                defaultScrollDelays.add(i);
            }
        }
        defaultScrollDelays.add(10);
        defaultScrollDelays.add(20);
        DEFAULT_SCROLL_DELAYS = Collections.unmodifiableList(defaultScrollDelays);
        List<Integer> decorativeItemsIndices = new ArrayList<>();
        for (int i = 0; i < 9 * 5; i++) {
            if (!firstRowIndices.contains(i) &&
                    !secondRowIndices.contains(i) &&
                    !thirdRowIndices.contains(i)) {
                decorativeItemsIndices.add(i);
            }
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
    private final Drop loseDrop;
    private final Optional<SoundType> firstRowSound;
    private final Optional<SoundType> secondRowSound;
    private final Optional<SoundType> thirdRowSound;
    private final Optional<SoundType> winSound;
    private final Optional<SoundType> consolationSound;
    private final Optional<SoundType> loseSound;
    private final Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode;
    private final Optional<Drop> defaultConsolationDrop;
    private final Map<String, Drop> consolationDrops;

    public CasinoOpenManager(ConfigurationNode node) {
        super(node);
        try {
            SuperObjectService superObjectService = Sponge.getServiceManager().provide(SuperObjectService.class).get();
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorativeItemsNode = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scrollDelaysNode = node.getNode("SCROLL_DELAYS");
            ConfigurationNode clearDecorativeItemsNode = node.getNode("CLEAR_DECORATIVE_ITEMS");
            ConfigurationNode clearOtherDropsNode = node.getNode("CLEAR_OTHER_DROPS");
            ConfigurationNode closeDelayNode = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbidCloseNode = node.getNode("FORBID_CLOSE");
            ConfigurationNode loseDropNode = node.getNode("LOSE_DROP");
            ConfigurationNode firstRowSoundNode = node.getNode("FIRST_ROW_SOUND");
            ConfigurationNode secondRowSoundNode = node.getNode("SECOND_ROW_SOUND");
            ConfigurationNode thirdRowSoundNode = node.getNode("THIRD_ROW_SOUND");
            ConfigurationNode winSoundNode = node.getNode("WIN_SOUND");
            ConfigurationNode consolationSoundNode = node.getNode("CONSOLATION_SOUND");
            ConfigurationNode loseSoundNode = node.getNode("LOSE_SOUND");
            ConfigurationNode defaultConsolationDropNode = node.getNode("DEFAULT_CONSOLATION_DROP");
            ConfigurationNode consolationDropsNode = node.getNode("CONSOLATION_DROPS");
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
            if (!loseDropNode.isVirtual()) {
                loseDrop = superObjectService.create(GWMCratesSuperObjectCategories.DROP, loseDropNode);
            } else {
                loseDrop = GWMCratesUtils.EMPTY_DROP;
            }
            if (!firstRowSoundNode.isVirtual()) {
                firstRowSound = Optional.of(firstRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                firstRowSound = Optional.empty();
            }
            if (!secondRowSoundNode.isVirtual()) {
                secondRowSound = Optional.of(secondRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                secondRowSound = Optional.empty();
            }
            if (!thirdRowSoundNode.isVirtual()) {
                thirdRowSound = Optional.of(thirdRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                thirdRowSound = Optional.empty();
            }
            if (!winSoundNode.isVirtual()) {
                winSound = Optional.of(winSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                winSound = Optional.empty();
            }
            if (!consolationSoundNode.isVirtual()) {
                consolationSound = Optional.of(consolationSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                consolationSound = Optional.empty();
            }
            if (!loseSoundNode.isVirtual()) {
                loseSound = Optional.of(loseSoundNode.getValue(TypeToken.of(SoundType.class)));
            } else {
                loseSound = Optional.empty();
            }
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of(superObjectService.create(GWMCratesSuperObjectCategories.DECORATIVE_ITEMS_CHANGE_MODE, decorativeItemsChangeModeNode));
            } else {
                decorativeItemsChangeMode = Optional.empty();
            }
            if (!defaultConsolationDropNode.isVirtual()) {
                defaultConsolationDrop = Optional.of(superObjectService.create(GWMCratesSuperObjectCategories.DROP, defaultConsolationDropNode));
            } else {
                defaultConsolationDrop = Optional.empty();
            }
            Map<String, Drop> tempConsolationDrops = new HashMap<>();
            if (!consolationDropsNode.isVirtual()) {
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : consolationDropsNode.getChildrenMap().entrySet()) {
                    tempConsolationDrops.put(entry.getKey().toString(), superObjectService.create(GWMCratesSuperObjectCategories.DROP, entry.getValue()));
                }
            }
            consolationDrops = Collections.unmodifiableMap(tempConsolationDrops);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public CasinoOpenManager(String id, Optional<SoundType> openSound,
                             Optional<Text> displayName, List<ItemStack> decorativeItems, List<Integer> scrollDelays,
                             boolean clearDecorativeItems, boolean clearOtherDrops, int closeDelay, boolean forbidClose,
                             Drop loseDrop, Optional<SoundType> firstRowSound, Optional<SoundType> secondRowSound,
                             Optional<SoundType> thirdRowSound, Optional<SoundType> winSound,
                             Optional<SoundType> consolationSound, Optional<SoundType> loseSound,
                             Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode,
                             Optional<Drop> defaultConsolationDrop, Map<String, Drop> consolationDrops) {
        super(id, openSound);
        this.displayName = displayName;
        this.decorativeItems = Collections.unmodifiableList(decorativeItems);
        this.scrollDelays = Collections.unmodifiableList(scrollDelays);
        this.clearDecorativeItems = clearDecorativeItems;
        this.clearOtherDrops = clearOtherDrops;
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.loseDrop = loseDrop;
        this.firstRowSound = firstRowSound;
        this.secondRowSound = secondRowSound;
        this.thirdRowSound = thirdRowSound;
        this.winSound = winSound;
        this.consolationSound = consolationSound;
        this.loseSound = loseSound;
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
        this.defaultConsolationDrop = defaultConsolationDrop;
        this.consolationDrops = Collections.unmodifiableMap(consolationDrops);
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
                of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 5));
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        List<List<Drop>> dropList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dropList.add(new ArrayList<>());
        }
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
        ROW_INDICES.forEach(list ->
                list.forEach(i ->
                        ordered.getSlot(new SlotIndex(i)).get().
                                set(((Drop) manager.getRandomManager().choose(manager.getDrops(), player, true)).
                                        getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM))));
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        CASINO_GUI_CONTAINERS.put(container, new ImmutablePair<>(this, manager));
        if (!decorativeItems.isEmpty()) {
            decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                    createTaskBuilder().delayTicks(mode.getChangeDelay()).
                    execute(new DecorativeItemsChangeRunnable(player, container, ordered, mode, DECORATIVE_ITEMS_INDICES, decorativeItems)).
                    submit(GWMCrates.getInstance()));
        }
        int waitTime = 0;
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            int scrollDelay = scrollDelays.get(i);
            for (int j = 0; j < scrollDelay; j++) {
                scheduleScroll(ROW_INDICES.get(1), ordered, waitTime + j,
                        (Drop) manager.getRandomManager().choose(manager.getDrops(), player, true));
                scheduleScroll(ROW_INDICES.get(2), ordered, waitTime + j,
                        (Drop) manager.getRandomManager().choose(manager.getDrops(), player, true));
            }
            waitTime += scrollDelay;
            Drop newDrop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, i != scrollDelays.size() - 4);
            scheduleScroll(ROW_INDICES.get(0), ordered, waitTime, newDrop);
            dropList.get(0).add(newDrop);
        }
        waitTime++;
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() ->
                        firstRowSound.ifPresent(sound ->
                                player.playSound(sound, player.getLocation().getPosition(), 1.))).
                submit(GWMCrates.getInstance());
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            int scrollDelay = scrollDelays.get(i);
            for (int j = 0; j < scrollDelay; j++) {
                scheduleScroll(ROW_INDICES.get(2), ordered, waitTime + j,
                        (Drop) manager.getRandomManager().choose(manager.getDrops(), player, true));
            }
            waitTime += scrollDelay;
            Drop newDrop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, i != scrollDelays.size() - 4);
            scheduleScroll(ROW_INDICES.get(1), ordered, waitTime, newDrop);
            dropList.get(1).add(newDrop);
        }
        waitTime++;
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() ->
                        secondRowSound.ifPresent(sound ->
                                player.playSound(sound, player.getLocation().getPosition(), 1.))).
                submit(GWMCrates.getInstance());
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            int scrollDelay = scrollDelays.get(i);
            waitTime += scrollDelay;
            Drop newDrop = (Drop) manager.getRandomManager().choose(manager.getDrops(), player, i != scrollDelays.size() - 4);
            scheduleScroll(ROW_INDICES.get(2), ordered, waitTime, newDrop);
            dropList.get(2).add(newDrop);
        }
        waitTime++;
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() ->
                        thirdRowSound.ifPresent(sound ->
                                player.playSound(sound, player.getLocation().getPosition(), 1.))).
                submit(GWMCrates.getInstance());
        waitTime += scrollDelays.get(scrollDelays.size() - 1);
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() -> {
                    Drop drop0 = dropList.get(0).get(dropList.get(0).size() - 3);
                    Drop drop1 = dropList.get(1).get(dropList.get(1).size() - 3);
                    Drop drop2 = dropList.get(2).get(dropList.get(2).size() - 3);
                    if (drop0.equals(drop1) &&
                            drop0.equals(drop2)) { //Play won
                        drop0.give(player, 1);
                        winSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    } else {
                        Drop tempDrop = drop0.equals(drop1) ?
                                drop0 : drop1.equals(drop2) ?
                                drop1 : drop2.equals(drop0) ?
                                drop2 : null;
                        if (tempDrop != null) { //Player didn't win, give him a consolation drop (if it exist)
                            String id = tempDrop.id();
                            consolationDrops.getOrDefault(id, defaultConsolationDrop.orElse(loseDrop)).give(player, 1);
                            consolationSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                        } else { //Player didn't win, give him the lose drop
                            loseDrop.give(player, 1);
                            loseSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                        }
                    }
                    if (clearDecorativeItems) {
                        for (Integer decorative_item_index : DECORATIVE_ITEMS_INDICES) {
                            ordered.getSlot(new SlotIndex(decorative_item_index)).get().
                                    set(GWMCratesUtils.EMPTY_ITEM);
                        }
                    }
                    if (clearOtherDrops) {
                        ROW_INDICES.forEach(list -> {
                            for (int i = 0; i < list.size(); i++) {
                                if (i == 2) {
                                    continue;
                                }
                                ordered.getSlot(new SlotIndex(list.get(i))).get().
                                        set(GWMCratesUtils.EMPTY_ITEM);
                            }
                        });
                    }
                    SHOWN_GUI.add(container);
                    PlayerOpenedCrateEvent openedEvent = new PlayerOpenedCrateEvent(player, manager, Collections.singletonList(drop0));
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
                    CASINO_GUI_CONTAINERS.remove(container);
                }).submit(GWMCrates.getInstance());
    }

    private void scheduleScroll(List<Integer> list, OrderedInventory ordered, int waitTime, Drop newDrop) {
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(waitTime).
                execute(() -> {
                    for (int j = 0; j < list.size() - 1; j++) {
                        int g1 = list.get(j);
                        int g2 = list.get(j + 1);
                        ordered.getSlot(new SlotIndex(g1)).get().set(ordered.getSlot(new SlotIndex(g2)).get().peek().
                                orElse(GWMCratesUtils.EMPTY_ITEM));
                    }
                    ordered.getSlot(new SlotIndex(list.get(list.size() - 1))).get().
                            set(newDrop.getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM));
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

    public Drop getLoseDrop() {
        return loseDrop;
    }

    public Optional<SoundType> getFirstRowSound() {
        return firstRowSound;
    }

    public Optional<SoundType> getSecondRowSound() {
        return secondRowSound;
    }

    public Optional<SoundType> getThirdRowSound() {
        return thirdRowSound;
    }

    public Optional<SoundType> getWinSound() {
        return winSound;
    }

    public Optional<SoundType> getConsolationSound() {
        return consolationSound;
    }

    public Optional<SoundType> getLoseSound() {
        return loseSound;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public Optional<Drop> getDefaultConsolationDrop() {
        return defaultConsolationDrop;
    }

    public Map<String, Drop> getConsolationDrops() {
        return consolationDrops;
    }
}
