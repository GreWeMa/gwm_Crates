package org.gwmdevelopments.sponge_plugin.crates.util;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.IdFormatException;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;

import java.util.Optional;

public abstract class SuperObject {

    private final Optional<String> id;

    public SuperObject(ConfigurationNode node) {
        try {
            ConfigurationNode idNode = node.getNode("ID");
            if (!idNode.isVirtual()) {
                id = Optional.of(idNode.getString());
            } else {
                id = Optional.empty();
            }
            if (id.isPresent() && !GWMCratesUtils.ID_PATTERN.matcher(id.get()).matches()) {
                throw new IdFormatException(id.get());
            }
        } catch (Exception e) {
            throw new SSOCreationException(ssoType(), type(), e);
        }
    }

    public SuperObject(Optional<String> id) {
        this.id = id;
    }

    public void shutdown() {
    }

    public abstract SuperObjectType ssoType();

    public abstract String type();

    public final Optional<String> id() {
        return id;
    }
}
