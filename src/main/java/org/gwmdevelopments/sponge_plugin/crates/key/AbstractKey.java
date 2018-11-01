package org.gwmdevelopments.sponge_plugin.crates.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;

import java.util.Optional;

public abstract class AbstractKey extends AbstractSuperObject implements Key {

    public AbstractKey(ConfigurationNode node) {
        super(node);
    }

    public AbstractKey(String type, Optional<String> id) {
        super(type, id);
    }
}
