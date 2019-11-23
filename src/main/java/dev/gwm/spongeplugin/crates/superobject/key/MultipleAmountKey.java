package dev.gwm.spongeplugin.crates.superobject.key;

import dev.gwm.spongeplugin.crates.superobject.key.base.GiveableKey;
import dev.gwm.spongeplugin.crates.superobject.key.base.Key;
import dev.gwm.spongeplugin.crates.util.GWMCratesSuperObjectCategories;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.GiveableData;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Set;

public final class MultipleAmountKey extends GiveableKey {

    public static final String TYPE = "MULTIPLE-AMOUNT";

    private final Key childKey;
    private final int amount;

    public MultipleAmountKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode childKeyNode = node.getNode("CHILD_KEY");
            ConfigurationNode amountNode = node.getNode("AMOUNT");
            if (childKeyNode.isVirtual()) {
                throw new IllegalArgumentException("CHILD_KEY node does not exist!");
            }
            if (amountNode.isVirtual()) {
                throw new IllegalArgumentException("AMOUNT node does not exist!");
            }
            childKey = Sponge.getServiceManager().provide(SuperObjectService.class).get().
                    create(GWMCratesSuperObjectCategories.KEY, childKeyNode);
            amount = amountNode.getInt();
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public MultipleAmountKey(String id, boolean doNotWithdraw,
                             GiveableData giveableData, boolean doNotAdd,
                             Key childKey, int amount) {
        super(id, doNotWithdraw, giveableData, doNotAdd);
        this.childKey = childKey;
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount is equal to or less than 0!");
        }
        this.amount = amount;
    }

    @Override
    public Set<SuperObject> getInternalSuperObjects() {
        Set<SuperObject> set = super.getInternalSuperObjects();
        set.add(childKey);
        return set;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void withdraw(Player player, int amount, boolean force) {
        if (!isDoNotWithdraw() || force) {
            childKey.withdraw(player, amount * this.amount, force);
        }
    }

    @Override
    public void give(Player player, int amount, boolean force) {
        if (!isDoNotAdd() || force) {
            if (childKey instanceof Giveable) {
                ((Giveable) childKey).give(player, amount * this.amount, force);
            }
        }
    }

    @Override
    public int get(Player player) {
        return childKey.get(player) / amount;
    }

    public Key getChildKey() {
        return childKey;
    }

    public int getAmount() {
        return amount;
    }
}
