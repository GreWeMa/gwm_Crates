package dev.gwm.spongeplugin.crates.superobject.preview;

import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.superobject.preview.base.AbstractPreview;
import dev.gwm.spongeplugin.crates.util.GWMCratesUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

public final class SecondGuiPreview extends AbstractPreview {

    public static final String TYPE = "SECOND";

    public static final Map<Container, Pair<SecondGuiPreview, Manager>> SECOND_GUI_CONTAINERS = new HashMap<>();

    private final Optional<Text> displayName;

    public SecondGuiPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(displayNameNode.getValue(TypeToken.of(Text.class)));
            } else {
                displayName = Optional.empty();
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public SecondGuiPreview(String id, Optional<List<Drop>> customDrops,
                            Optional<Text> displayName) {
        super(id, customDrops);
        this.displayName = displayName;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void preview(Player player, Manager manager) {
        InventoryDimension dimension = new InventoryDimension(9, GWMCratesUtils.getInventoryHeight(manager.getDrops().size()));
        Inventory.Builder builder = Inventory.builder().
                of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, dimension);
        displayName.ifPresent(title ->
                builder.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(title)));
        Inventory inventory = builder.build(GWMCrates.getInstance());
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        Iterator<Drop> dropIterator = getCustomDrops().orElse(manager.getDrops()).
                stream().
                filter(Drop::isShowInPreview).
                iterator();
        int size = 9 * dimension.getRows();
        for (int i = 0; i < size && dropIterator.hasNext();) {
            Drop next = dropIterator.next();
            Optional<ItemStack> optionalDropItem = next.getDropItem();
            if (!optionalDropItem.isPresent()) {
                continue;
            }
            ordered.getSlot(new SlotIndex(i)).get().set(optionalDropItem.get());
            i++;
        }
        Container container = player.openInventory(inventory).get();
        SECOND_GUI_CONTAINERS.put(container, new ImmutablePair<>(this, manager));
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }
}
