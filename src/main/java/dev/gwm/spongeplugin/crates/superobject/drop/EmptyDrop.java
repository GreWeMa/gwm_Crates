package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.library.util.DefaultRandomableData;
import dev.gwm.spongeplugin.library.util.GiveableData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

public final class EmptyDrop extends AbstractDrop {

    public static final String TYPE = "EMPTY";

    public EmptyDrop(ConfigurationNode node) {
        super(node);
    }

    public EmptyDrop(String id,
                     GiveableData giveableData,
                     Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                     DefaultRandomableData defaultRandomableData) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
    }

    public EmptyDrop(String id) {
        super(id,
                new GiveableData(Optional.empty(), Optional.empty()),
                Optional.empty(), Optional.empty(), false,
                new DefaultRandomableData(1, Optional.empty(),
                        Collections.emptyMap(), Collections.emptyMap(),
                        1L, Optional.empty(),
                        Collections.emptyMap(), Collections.emptyMap()));
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
    }
}
