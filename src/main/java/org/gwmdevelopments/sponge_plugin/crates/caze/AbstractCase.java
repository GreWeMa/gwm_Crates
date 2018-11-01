package org.gwmdevelopments.sponge_plugin.crates.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;

import java.util.Optional;

public abstract class AbstractCase extends AbstractSuperObject implements Case {

    public AbstractCase(ConfigurationNode node) {
        super(node);
    }

    public AbstractCase(String type, Optional<String> id) {
        super(type, id);
    }
}
