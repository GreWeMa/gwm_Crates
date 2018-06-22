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
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.*;

public class CasinoOpenManager extends OpenManager {

    public static final Map<Container, Pair<CasinoOpenManager, Manager>> CASINO_GUI_CONTAINERS = new HashMap<>();
    public static final Set<Container> SHOWN_GUI = new HashSet<>();

    public static final List<List<Integer>> ROW_INDICES;

    public static final List<Integer> DEFAULT_SCROLL_DELAYS;

    public static final List<Integer> DECORATIVE_ITEMS_INDICES;

    public static final List<ItemStack> DEFAULT_CASINO_DECORATIVE_ITEMS;

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
        List<ItemStack> defaultCasinoDecorativeItems = new ArrayList<>();
        for (int i = 0; i < decorativeItemsIndices.size(); i++) {
            defaultCasinoDecorativeItems.add(GWMCratesUtils.EMPTY_ITEM);
        }
        DEFAULT_CASINO_DECORATIVE_ITEMS = Collections.unmodifiableList(defaultCasinoDecorativeItems);
    }

    private Optional<Text> displayName = Optional.empty();
    private List<ItemStack> decorativeItems = DEFAULT_CASINO_DECORATIVE_ITEMS;
    private List<Integer> scrollDelays = DEFAULT_SCROLL_DELAYS;
    private boolean clearDecorativeItems;
    private boolean clearOtherDrops;
    private int closeDelay;
    private boolean forbidClose;
    private Drop loseDrop = GWMCratesUtils.EMPTY_DROP;
    private Optional<SoundType> firstRowSound = Optional.empty();
    private Optional<SoundType> secondRowSound = Optional.empty();
    private Optional<SoundType> thirdRowSound = Optional.empty();
    private Optional<SoundType> winSound = Optional.empty();
    private Optional<SoundType> loseSound = Optional.empty();
    private Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode = Optional.empty();

    public CasinoOpenManager(ConfigurationNode node) {
        super(node);
        try {
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
            ConfigurationNode loseSoundNode = node.getNode("LOSE_SOUND");
            ConfigurationNode decorativeItemsChangeModeNode = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(displayNameNode.getString()));
            }
            if (!decorativeItemsNode.isVirtual()) {
                decorativeItems = new ArrayList<>();
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
            if (!loseDropNode.isVirtual()) {
                loseDrop = (Drop) GWMCratesUtils.createSuperObject(loseDropNode, SuperObjectType.DROP);
            }
            if (!firstRowSoundNode.isVirtual()) {
                firstRowSound = Optional.of(firstRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!secondRowSoundNode.isVirtual()) {
                secondRowSound = Optional.of(secondRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!thirdRowSoundNode.isVirtual()) {
                thirdRowSound = Optional.of(thirdRowSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!winSoundNode.isVirtual()) {
                winSound = Optional.of(winSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!loseSoundNode.isVirtual()) {
                loseSound = Optional.of(loseSoundNode.getValue(TypeToken.of(SoundType.class)));
            }
            if (!decorativeItemsChangeModeNode.isVirtual()) {
                decorativeItemsChangeMode = Optional.of((DecorativeItemsChangeMode) GWMCratesUtils.createSuperObject(decorativeItemsChangeModeNode, SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Casino Open Manager!", e);
        }
    }

    public CasinoOpenManager(String type, Optional<String> id, Optional<SoundType> openSound,
                             Optional<Text> displayName, List<ItemStack> decorativeItems,
                             List<Integer> scrollDelays, boolean clearDecorativeItems,
                             boolean clearOtherDrops, int closeDelay, boolean forbidClose,
                             Drop loseDrop, Optional<SoundType> firstRowSound,
                             Optional<SoundType> secondRowSound, Optional<SoundType> thirdRowSound) {
        super(type, id, openSound);
        this.displayName = displayName;
        this.decorativeItems = decorativeItems;
        this.scrollDelays = scrollDelays;
        this.clearDecorativeItems = clearDecorativeItems;
        this.clearOtherDrops = clearOtherDrops;
        this.closeDelay = closeDelay;
        this.forbidClose = forbidClose;
        this.loseDrop = loseDrop;
        this.firstRowSound = firstRowSound;
        this.secondRowSound = secondRowSound;
        this.thirdRowSound = thirdRowSound;
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
        int index = 0;
        for (int i = 0; i < DECORATIVE_ITEMS_INDICES.size(); i++, index++) {
            if (index == decorativeItems.size()) {
                index = 0;
            }
            ordered.getSlot(new SlotIndex(DECORATIVE_ITEMS_INDICES.get(i))).get().
                    set(decorativeItems.get(index));
        }
        ROW_INDICES.forEach(list ->
                list.forEach(i ->
                        ordered.getSlot(new SlotIndex(i)).get().
                                set(GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true).
                                        getDropItem().orElse(GWMCratesUtils.EMPTY_ITEM))));
        Container container = player.openInventory(inventory).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        CASINO_GUI_CONTAINERS.put(container, new Pair<>(this, manager));
        decorativeItemsChangeMode.ifPresent(mode -> Sponge.getScheduler().
                createTaskBuilder().delayTicks(mode.getChangeDelay()).
                execute(new DecorativeDropChangeRunnable(player, container, ordered, new ArrayList<>(decorativeItems), mode, DECORATIVE_ITEMS_INDICES)).
                submit(GWMCrates.getInstance()));
        int waitTime = 0;
        for (int i = 0; i < scrollDelays.size() - 1; i++) {
            int scrollDelay = scrollDelays.get(i);
            for (int j = 0; j < scrollDelay; j++) {
                scheduleScroll(ROW_INDICES.get(1), ordered, waitTime + j,
                        GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true));
                scheduleScroll(ROW_INDICES.get(2), ordered, waitTime + j,
                        GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true));
            }
            waitTime += scrollDelay;
            Drop newDrop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, i != scrollDelays.size() - 4);
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
                        GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, true));
            }
            waitTime += scrollDelay;
            Drop newDrop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, i != scrollDelays.size() - 4);
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
            Drop newDrop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, i != scrollDelays.size() - 4);
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
                    Drop drop = dropList.get(0).get(dropList.get(0).size() - 3);
                    if (drop.equals(dropList.get(1).get(dropList.get(1).size() - 3)) &&
                            drop.equals(dropList.get(2).get(dropList.get(2).size() - 3))) {
                        drop.apply(player);
                        winSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    } else {
                        loseDrop.apply(player);
                        loseSound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
                    }
                    if (clearDecorativeItems) {
                        for (int i = 0; i < DECORATIVE_ITEMS_INDICES.size(); i++) {
                            ordered.getSlot(new SlotIndex(DECORATIVE_ITEMS_INDICES.get(i))).get().
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
                    CASINO_GUI_CONTAINERS.remove(container);
                }).submit(GWMCrates.getInstance());
    }

    private void scheduleScroll(List<Integer> list, OrderedInventory ordered, int waitTime,
                                Drop newDrop) {
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

    public Drop getLoseDrop() {
        return loseDrop;
    }

    public void setLoseDrop(Drop loseDrop) {
        this.loseDrop = loseDrop;
    }

    public Optional<SoundType> getFirstRowSound() {
        return firstRowSound;
    }

    public void setFirstRowSound(Optional<SoundType> firstRowSound) {
        this.firstRowSound = firstRowSound;
    }

    public Optional<SoundType> getSecondRowSound() {
        return secondRowSound;
    }

    public void setSecondRowSound(Optional<SoundType> secondRowSound) {
        this.secondRowSound = secondRowSound;
    }

    public Optional<SoundType> getThirdRowSound() {
        return thirdRowSound;
    }

    public void setThirdRowSound(Optional<SoundType> thirdRowSound) {
        this.thirdRowSound = thirdRowSound;
    }

    public Optional<SoundType> getWinSound() {
        return winSound;
    }

    public void setWinSound(Optional<SoundType> winSound) {
        this.winSound = winSound;
    }

    public Optional<SoundType> getLoseSound() {
        return loseSound;
    }

    public void setLoseSound(Optional<SoundType> loseSound) {
        this.loseSound = loseSound;
    }

    public Optional<DecorativeItemsChangeMode> getDecorativeItemsChangeMode() {
        return decorativeItemsChangeMode;
    }

    public void setDecorativeItemsChangeMode(Optional<DecorativeItemsChangeMode> decorativeItemsChangeMode) {
        this.decorativeItemsChangeMode = decorativeItemsChangeMode;
    }
}
