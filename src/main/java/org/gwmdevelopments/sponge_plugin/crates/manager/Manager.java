package org.gwmdevelopments.sponge_plugin.crates.manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.exception.IdFormatException;
import org.gwmdevelopments.sponge_plugin.crates.exception.ManagerCreationException;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.random_manager.RandomManager;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Manager {

    private final String id;
    private final String name;
    private final RandomManager randomManager;
    private final Case caze;
    private final Key key;
    private final OpenManager openManager;
    private final List<Drop> drops;
    private final Optional<Preview> preview;
    private final boolean sendOpenMessage;
    private final Optional<String> customOpenMessage;
    private final Optional<Text> customInfo;
    private final boolean sendCaseMissingMessage;
    private final boolean sendKeyMissingMessage;
    private final Optional<Text> customCaseMissingMessage;
    private final Optional<Text> customKeyMissingMessage;

    public Manager(ConfigurationNode node) {
        try {
            ConfigurationNode idNode = node.getNode("ID");
            ConfigurationNode nameNode = node.getNode("NAME");
            ConfigurationNode randomManagerNode = node.getNode("RANDOM_MANAGER");
            ConfigurationNode caseNode = node.getNode("CASE");
            ConfigurationNode keyNode = node.getNode("KEY");
            ConfigurationNode openManagerNode = node.getNode("OPEN_MANAGER");
            ConfigurationNode dropsNode = node.getNode("DROPS");
            ConfigurationNode previewNode = node.getNode("PREVIEW");
            ConfigurationNode sendOpenMessageNode = node.getNode("SEND_OPEN_MESSAGE");
            ConfigurationNode customOpenMessageNode = node.getNode("CUSTOM_OPEN_MESSAGE");
            ConfigurationNode customInfoNode = node.getNode("CUSTOM_INFO");
            ConfigurationNode sendCaseMissingMessageNode = node.getNode("SEND_CASE_MISSING_MESSAGE");
            ConfigurationNode sendKeyMissingMessageNode = node.getNode("SEND_KEY_MISSING_MESSAGE");
            ConfigurationNode customCaseMissingMessageNode = node.getNode("CUSTOM_CASE_MISSING_MESSAGE");
            ConfigurationNode customKeyMissingMessageNode = node.getNode("CUSTOM_KEY_MISSING_MESSAGE");
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
            if (!GWMCratesUtils.ID_PATTERN.matcher(id).matches()) {
                throw new IdFormatException(id);
            }
            name = nameNode.getString();
            if (randomManagerNode.isVirtual()) {
                randomManager = GWMCrates.getInstance().getDefaultRandomManager();
            } else {
                randomManager = (RandomManager) GWMCratesUtils.createSuperObject(randomManagerNode, SuperObjectType.RANDOM_MANAGER);
            }
            caze = (Case) GWMCratesUtils.createSuperObject(caseNode, SuperObjectType.CASE);
            key = (Key) GWMCratesUtils.createSuperObject(keyNode, SuperObjectType.KEY);
            List<Drop> tempDrops = new ArrayList<>();
            for (ConfigurationNode dropNode : dropsNode.getChildrenList()) {
                tempDrops.add((Drop) GWMCratesUtils.createSuperObject(dropNode, SuperObjectType.DROP));
            }
            if (tempDrops.isEmpty()) {
                throw new IllegalArgumentException("No drops are configured! At least one drop is required!");
            }
            drops = Collections.unmodifiableList(tempDrops);
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
            sendCaseMissingMessage = sendCaseMissingMessageNode.getBoolean(true);
            sendKeyMissingMessage = sendKeyMissingMessageNode.getBoolean(true);
            if (!customCaseMissingMessageNode.isVirtual()) {
                customCaseMissingMessage = Optional.of(TextSerializers.FORMATTING_CODE.
                        deserialize(customCaseMissingMessageNode.getString()));
            } else {
                customCaseMissingMessage = Optional.empty();
            }
            if (!customKeyMissingMessageNode.isVirtual()) {
                customKeyMissingMessage = Optional.of(TextSerializers.FORMATTING_CODE.
                        deserialize(customKeyMissingMessageNode.getString()));
            } else {
                customKeyMissingMessage = Optional.empty();
            }
        } catch (Exception e) {
            throw new ManagerCreationException(e);
        }
    }

    public Manager(String id, String name, RandomManager randomManager,
                   Case caze, Key key, OpenManager openManager, List<Drop> drops,
                   Optional<Preview> preview, boolean sendOpenMessage, Optional<String> customOpenMessage,
                   Optional<Text> customInfo, boolean sendCaseMissingMessage, boolean sendKeyMissingMessage,
                   Optional<Text> customCaseMissingMessage, Optional<Text> customKeyMissingMessage) {
        this.id = id;
        this.name = name;
        this.randomManager = randomManager;
        this.caze = caze;
        this.key = key;
        this.openManager = openManager;
        this.drops = drops;
        this.preview = preview;
        this.sendOpenMessage = sendOpenMessage;
        this.customOpenMessage = customOpenMessage;
        this.customInfo = customInfo;
        this.sendCaseMissingMessage = sendCaseMissingMessage;
        this.sendKeyMissingMessage = sendKeyMissingMessage;
        this.customCaseMissingMessage = customCaseMissingMessage;
        this.customKeyMissingMessage = customKeyMissingMessage;
    }

    public void shutdown() {
        openManager.shutdown();
        preview.ifPresent(SuperObject::shutdown);
        drops.forEach(SuperObject::shutdown);
        caze.shutdown();
        key.shutdown();
    }

    public Optional<Drop> getDropById(String id) {
        for (Drop drop : drops) {
            if (drop.id().isPresent() && drop.id().get().equals(id)) {
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

    public RandomManager getRandomManager() {
        return randomManager;
    }

    public Case getCase() {
        return caze;
    }

    public Key getKey() {
        return key;
    }

    public OpenManager getOpenManager() {
        return openManager;
    }

    public List<Drop> getDrops() {
        return drops;
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

    public boolean isSendCaseMissingMessage() {
        return sendCaseMissingMessage;
    }

    public boolean isSendKeyMissingMessage() {
        return sendKeyMissingMessage;
    }

    public Optional<Text> getCustomCaseMissingMessage() {
        return customCaseMissingMessage;
    }

    public Optional<Text> getCustomKeyMissingMessage() {
        return customKeyMissingMessage;
    }
}
