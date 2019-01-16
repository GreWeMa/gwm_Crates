package org.gwmdevelopments.sponge_plugin.crates.util;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.exception.SSOCreationException;

import java.util.Optional;

public abstract class AbstractSuperObject implements SuperObject {

    private String type;
    private Optional<String> id = Optional.empty();

    public AbstractSuperObject(ConfigurationNode node) {
        try {
            ConfigurationNode typeNode = node.getNode("TYPE");
            ConfigurationNode idNode = node.getNode("ID");
            if (typeNode.isVirtual()) {
                throw new IllegalArgumentException("TYPE node does not exist!");
            }
            type = typeNode.getString();
            if (!idNode.isVirtual()) {
                id = Optional.of(idNode.getString());
            }
        } catch (Exception e) {
            throw new SSOCreationException("Failed to create Abstract Super Object!", e);
        }
    }

    public AbstractSuperObject(String type, Optional<String> id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Optional<String> getId() {
        return id;
    }

    @Override
    public void setId(Optional<String> id) {
        this.id = id;
    }
}
