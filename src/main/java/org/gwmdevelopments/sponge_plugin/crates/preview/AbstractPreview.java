package org.gwmdevelopments.sponge_plugin.crates.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.AbstractSuperObject;

import java.util.Optional;

public abstract class AbstractPreview extends AbstractSuperObject implements Preview {

    public AbstractPreview(ConfigurationNode node) {
        super(node);
    }

    public AbstractPreview(String type, Optional<String> id) {
        super(type, id);
    }
}
