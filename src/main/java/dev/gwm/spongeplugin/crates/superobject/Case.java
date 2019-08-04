package dev.gwm.spongeplugin.crates.caze;

import dev.gwm.spongeplugin.crates.exception.SSOCreationException;
import ninja.leaping.configurate.ConfigurationNode;
import dev.gwm.spongeplugin.crates.util.SuperObject;
import dev.gwm.spongeplugin.crates.util.SuperObjectType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public abstract class Case extends SuperObject {

    private final boolean doNotWithdraw;

    public Case(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode doNotWithdrawNode = node.getNode("DO_NOT_WITHDRAW");
            doNotWithdraw = doNotWithdrawNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public Case(Optional<String> id, boolean doNotWithdraw) {
        super(id);
        this.doNotWithdraw = doNotWithdraw;
    }

    @Override
    public final SuperObjectType ssoType() {
        return SuperObjectType.CASE;
    }

    public abstract void withdraw(Player player, int amount, boolean force);

    public abstract int get(Player player);

    public boolean isDoNotWithdraw() {
        return doNotWithdraw;
    }
}
