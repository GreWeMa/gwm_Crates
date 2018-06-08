package org.gwmdevelopments.sponge_plugin.crates.util;

import ninja.leaping.configurate.ConfigurationNode;

import java.util.Optional;

public abstract class SuperObject {

    private String type;
    private Optional<String> id = Optional.empty();

    public SuperObject(ConfigurationNode node) {
        try {
            ConfigurationNode typeNode = node.getNode("TYPE");
            ConfigurationNode idNode = node.getNode("ID");
            if (typeNode.isVirtual()) {
                throw new RuntimeException("TYPE node does not exist!");
            }
            type = typeNode.getString();
            if (!idNode.isVirtual()) {
                id = Optional.of(idNode.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Super Object!", e);
        }
    }

    public SuperObject(String type, Optional<String> id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Optional<String> getId() {
        return id;
    }

    public void setId(Optional<String> id) {
        this.id = id;
    }
}
