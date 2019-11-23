package dev.gwm.spongeplugin.crates.superobject.drop;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.superobject.drop.base.AbstractDrop;
import dev.gwm.spongeplugin.crates.superobject.drop.base.Drop;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.DefaultRandomableData;
import dev.gwm.spongeplugin.library.util.GiveableData;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class DelayDrop extends AbstractDrop {

    public static final String TYPE = "DELAY";

    private final Drop childDrop;
    private final long delay;

    public DelayDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childDropNode = node.getNode("CHILD_DROP");
            ConfigurationNode delayNode = node.getNode("DELAY");
            if (childDropNode.isVirtual()) {
                throw new IllegalArgumentException("CHILD_DROP node does not exist!");
            }
            if (delayNode.isVirtual()) {
                throw new IllegalArgumentException("DELAY node does not exist!");
            }
            childDrop = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                    create(GWMCratesSuperObjectCategories.DROP, childDropNode);
            delay = delayNode.getLong();
            if (delay <= 0) {
                throw new IllegalArgumentException("Delay is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public DelayDrop(String id,
                     GiveableData giveableData,
                     Optional<ItemStack> dropItem, Optional<String> customName, boolean showInPreview,
                     DefaultRandomableData defaultRandomableData,
                     Drop childDrop, long delay) {
        super(id, giveableData, dropItem, customName, showInPreview, defaultRandomableData);
        this.childDrop = childDrop;
        if (delay <= 0) {
            throw new IllegalArgumentException("Delay is equal to or less than 0!");
        }
        this.delay = delay;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(childDrop);
        return set;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void give(Player player, int amount) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).
                execute(() -> childDrop.give(player, amount)).
                submit(GWMCrates.getInstance());
    }

    public Drop getChildDrop() {
        return childDrop;
    }

    public long getDelay() {
        return delay;
    }
}
