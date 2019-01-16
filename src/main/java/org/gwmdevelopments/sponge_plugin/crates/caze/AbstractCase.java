package org.gwmdevelopments.sponge_plugin.crates.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;

import java.util.Optional;

public abstract class AbstractCase extends AbstractSuperObject implements Case {

    private boolean doNotWithdraw;

    public AbstractCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode doNotWithdrawNode = node.getNode("DO_NOT_WITHDRAW");
            doNotWithdraw = doNotWithdrawNode.getBoolean(false);
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Abstract Case!", e);
        }
    }

    public AbstractCase(String type, Optional<String> id, boolean doNotWithdraw) {
        super(type, id);
        this.doNotWithdraw = doNotWithdraw;
    }

    @Override
    public boolean isDoNotWithdraw() {
        return doNotWithdraw;
    }

    public void setDoNotWithdraw(boolean doNotWithdraw) {
        this.doNotWithdraw = doNotWithdraw;
    }
}
