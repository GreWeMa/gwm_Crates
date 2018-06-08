package org.gwmdevelopments.sponge_plugin.crates.manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
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

public class Manager {

    private String id;
    private String name;
    private Case caze;
    private Key key;
    private OpenManager openManager;
    private List<Drop> drops;
    private Optional<Preview> preview = Optional.empty();
    private boolean sendOpenMessage;
    private Optional<String> customOpenMessage = Optional.empty();
    private Optional<Text> customInfo = Optional.empty();

    public Manager(ConfigurationNode node) {
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
            throw new RuntimeException("ID node does not exist!");
        }
        if (nameNode.isVirtual()) {
            throw new RuntimeException("NAME node does not exist!");
        }
        if (caseNode.isVirtual()) {
            throw new RuntimeException("CASE node does not exist!");
        }
        if (keyNode.isVirtual()) {
            throw new RuntimeException("KEY node does not exist!");
        }
        if (openManagerNode.isVirtual()) {
            throw new RuntimeException("OPEN_MANGER node does not exist!");
        }
        if (dropsNode.isVirtual()) {
            throw new RuntimeException("DROPS node does not exist!");
        }
        id = idNode.getString();
        name = nameNode.getString();
        caze = (Case) GWMCratesUtils.createSuperObject(caseNode, SuperObjectType.CASE);
        key = (Key) GWMCratesUtils.createSuperObject(keyNode, SuperObjectType.KEY);
        drops = new ArrayList<Drop>();
        for (ConfigurationNode drop_node : dropsNode.getChildrenList()) {
            drops.add((Drop) GWMCratesUtils.createSuperObject(drop_node, SuperObjectType.DROP));
        }
        openManager = (OpenManager) GWMCratesUtils.createSuperObject(openManagerNode, SuperObjectType.OPEN_MANAGER);
        if (!previewNode.isVirtual()) {
            preview = Optional.of((Preview) GWMCratesUtils.createSuperObject(previewNode, SuperObjectType.PREVIEW));
        }
        sendOpenMessage = sendOpenMessageNode.getBoolean(true);
        if (!customOpenMessageNode.isVirtual()) {
            customOpenMessage = Optional.of(customOpenMessageNode.getString());
        }
        if (!customInfoNode.isVirtual()) {
            customInfo = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(customInfoNode.getString()));
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

    public void setCase(Case caze) {
        this.caze = caze;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void setDrops(List<Drop> drops) {
        this.drops = drops;
    }

    public OpenManager getOpenManager() {
        return openManager;
    }

    public void setOpenManager(OpenManager open_manager) {
        this.openManager = open_manager;
    }

    public Optional<Preview> getPreview() {
        return preview;
    }

    public void setPreview(Optional<Preview> preview) {
        this.preview = preview;
    }

    public boolean isSendOpenMessage() {
        return sendOpenMessage;
    }

    public void setSendOpenMessage(boolean send_open_message) {
        this.sendOpenMessage = send_open_message;
    }

    public Optional<String> getCustomOpenMessage() {
        return customOpenMessage;
    }

    public void setCustomOpenMessage(Optional<String> custom_open_message) {
        this.customOpenMessage = custom_open_message;
    }

    public Optional<Text> getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Optional<Text> custom_info) {
        this.customInfo = custom_info;
    }
}
