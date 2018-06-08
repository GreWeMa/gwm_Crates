package org.gwmdevelopments.sponge_plugin.crates.preview.previews;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
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
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class SecondGuiPreview extends Preview {

    public static final Map<Container, Pair<SecondGuiPreview, Manager>> SECOND_GUI_CONTAINERS = new HashMap<>();

    private Optional<Text> displayName = Optional.empty();

    public SecondGuiPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode displayNameNode = node.getNode("DISPLAY_NAME");
            if (!displayNameNode.isVirtual()) {
                displayName = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(displayNameNode.getString()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Second Gui Preview!", e);
        }
    }

    public SecondGuiPreview(Optional<String> id, Optional<Text> displayName) {
        super("SECOND", id);
        this.displayName = displayName;
    }

    @Override
    public void preview(Player player, Manager manager) {
        InventoryDimension dimension = new InventoryDimension(9, GWMCratesUtils.getInventoryHeight(manager.getDrops().size()));
        Inventory inventory = displayName.map(text -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, dimension).
                property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(text)).
                build(GWMCrates.getInstance())).orElseGet(() -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryDimension.PROPERTY_NAME, dimension).
                build(GWMCrates.getInstance()));
        OrderedInventory ordered = GWMCratesUtils.castToOrdered(inventory);
        Iterator<Drop> drop_iterator = manager.getDrops().iterator();
        int size = 9 * dimension.getRows();
        for (int i = 0; i < size && drop_iterator.hasNext();) {
            Drop next = drop_iterator.next();
            Optional<ItemStack> optionalDropItem = next.getDropItem();
            if (!optionalDropItem.isPresent()) {
                continue;
            }
            ordered.getSlot(new SlotIndex(i)).get().set(optionalDropItem.get());
            i++;
        }
        Container container = player.openInventory(inventory).get();
        SECOND_GUI_CONTAINERS.put(container, new Pair<SecondGuiPreview, Manager>(this, manager));
    }

    public Optional<Text> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Optional<Text> displayName) {
        this.displayName = displayName;
    }
}
