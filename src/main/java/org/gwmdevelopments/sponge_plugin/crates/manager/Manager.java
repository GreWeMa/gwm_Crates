package org.gwmdevelopments.sponge_plugin.crates.manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.ManagerCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Manager {

    private final String id;
    private final String name;
    private final Case caze;
    private final Key key;
    private final OpenManager openManager;
    private final List<Drop> drops;
    private final Optional<Preview> preview;
    private final boolean sendOpenMessage;
    private final Optional<String> customOpenMessage;
    private final Optional<Text> customInfo;

    public Manager(ConfigurationNode node) {
        try {
            ConfigurationNode idNode = node.getNode("ID");
            ConfigurationNode nameNode = node.getNode("NAME");
            ConfigurationNode caseNode = node.getNode("CASE");
            ConfigurationNode keyNode = node.getNode("KEY");
            ConfigurationNode openManagerNode = node.getNode("OPEN_MANAGER");
            ConfigurationNode dropsNode = node.getNode("DROPS");
            ConfigurationNode previewNode = node.getNode("PREVIEW");
            ConfigurationNode sendOpenMessageNode = node.getNode("SEND_OPEN_MESSAGE");
            ConfigurationNode customOpenMessageNode = node.getNode("CUSTOM_OPEN_MESSAGE");
            ConfigurationNode customInfoNode = node.getNode("CUSTOM_INFO");
            if (idNode.isVirtual()) {
                throw new IllegalArgumentException("ID node does not exist!");
            }
            if (nameNode.isVirtual()) {
                throw new IllegalArgumentException("NAME node does not exist!");
            }
            if (caseNode.isVirtual()) {
                throw new IllegalArgumentException("CASE node does not exist!");
            }
            if (keyNode.isVirtual()) {
                throw new IllegalArgumentException("KEY node does not exist!");
            }
            if (openManagerNode.isVirtual()) {
                throw new IllegalArgumentException("OPEN_MANGER node does not exist!");
            }
            if (dropsNode.isVirtual()) {
                throw new IllegalArgumentException("DROPS node does not exist!");
            }
            id = idNode.getString();
            name = nameNode.getString();
            caze = (Case) GWMCratesUtils.createSuperObject(caseNode, SuperObjectType.CASE);
            key = (Key) GWMCratesUtils.createSuperObject(keyNode, SuperObjectType.KEY);
            drops = new ArrayList<>();
            for (ConfigurationNode drop_node : dropsNode.getChildrenList()) {
                drops.add((Drop) GWMCratesUtils.createSuperObject(drop_node, SuperObjectType.DROP));
            }
            openManager = (OpenManager) GWMCratesUtils.createSuperObject(openManagerNode, SuperObjectType.OPEN_MANAGER);
            if (!previewNode.isVirtual()) {
                preview = Optional.of((Preview) GWMCratesUtils.createSuperObject(previewNode, SuperObjectType.PREVIEW));
            } else {
                preview = Optional.empty();
            }
            sendOpenMessage = sendOpenMessageNode.getBoolean(true);
            if (!customOpenMessageNode.isVirtual()) {
                customOpenMessage = Optional.of(customOpenMessageNode.getString());
            } else {
                customOpenMessage = Optional.empty();
            }
            if (!customInfoNode.isVirtual()) {
                customInfo = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(customInfoNode.getString()));
            } else {
                customInfo = Optional.empty();
            }
        } catch (Exception e) {
            throw new ManagerCreationException("Failed to create Manager!", e);
        }
    }

    public Manager(String id, String name, Case caze, Key key, OpenManager openManager, List<Drop> drops,
                   Optional<Preview> preview, boolean sendOpenMessage, Optional<String> customOpenMessage,
                   Optional<Text> customInfo) {
        this.id = id;
        this.name = name;
        this.caze = caze;
        this.key = key;
        this.openManager = openManager;
        this.drops = drops;
        this.preview = preview;
        this.sendOpenMessage = sendOpenMessage;
        this.customOpenMessage = customOpenMessage;
        this.customInfo = customInfo;
    }

    public Optional<Drop> getDropById(String id) {
        for (Drop drop : drops) {
            if (drop.getId().isPresent() && drop.getId().get().equals(id)) {
                return Optional.of(drop);
            }
        }
        return Optional.empty();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Case getCase() {
        return caze;
    }

    public Key getKey() {
        return key;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public OpenManager getOpenManager() {
        return openManager;
    }

    public Optional<Preview> getPreview() {
        return preview;
    }

    public boolean isSendOpenMessage() {
        return sendOpenMessage;
    }

    public Optional<String> getCustomOpenMessage() {
        return customOpenMessage;
    }

    public Optional<Text> getCustomInfo() {
        return customInfo;
    }
}
