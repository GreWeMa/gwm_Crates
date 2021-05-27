package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.DefaultRandomableData;
import dev.gwm.spongeplugin.library.util.GiveableData;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public final class ReplicatorDrop extends AbstractDrop {

    public static final String TYPE = "REPLICATOR";

    private final Drop childDrop;

    public ReplicatorDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childDropNode = node.getNode("CHILD_DROP");
            if (childDropNode.isVirtual()) {
                throw new IllegalArgumentException("CHILD_DROP node does not exist!");
            }
            childDrop = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                    create(GWMCratesSuperObjectCategories.DROP, childDropNode);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public ReplicatorDrop(String id,
                          GiveableData giveableData,
                          Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                          DefaultRandomableData defaultRandomableData,
                          Drop childDrop) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        this.childDrop = childDrop;
    }

    @Override
    public void give(Player ignored, int amount) {
        Sponge.getServer().getOnlinePlayers().forEach(player -> childDrop.give(player, amount));
    }

    @Override
    public String type() {
        return TYPE;
    }

    public Drop getChildDrop() {
        return childDrop;
    }
}
