package dev.gwm.spongeplugin.crates.util;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.List;
import java.util.Optional;

public class ManagerCustomMessageData {

    private final Optional<List<String>> customInfo;
    private final boolean sendOpenMessage;
    private final Optional<List<String>> customOpenMessage;
    private final boolean sendCaseMissingMessage;
    private final Optional<List<String>> customCaseMissingMessage;
    private final boolean sendKeyMissingMessage;
    private final Optional<List<String>> customKeyMissingMessage;
    private final boolean sendPreviewIsNotAvailableMessage;
    private final Optional<List<String>> customPreviewIsNotAvailableMessage;
    private final boolean sendNoPermissionToOpenMessage;
    private final Optional<List<String>> customNoPermissionToOpenMessage;
    private final boolean sendNoPermissionToPreviewMessage;
    private final Optional<List<String>> customNoPermissionToPreviewMessage;
    private final boolean sendCrateDelayMessage;
    private final Optional<List<String>> customCrateDelayMessage;
    private final boolean sendCannotOpenManagerMessage;
    private final Optional<List<String>> customCannotOpenManagerMessage;

    public ManagerCustomMessageData(ConfigurationNode node) throws ObjectMappingException {
        ConfigurationNode customInfoNode = node.getNode("CUSTOM_INFO");
        ConfigurationNode sendOpenMessageNode = node.getNode("SEND_OPEN_MESSAGE");
        ConfigurationNode customOpenMessageNode = node.getNode("CUSTOM_OPEN_MESSAGE");
        ConfigurationNode sendCaseMissingMessageNode = node.getNode("SEND_CASE_MISSING_MESSAGE");
        ConfigurationNode customCaseMissingMessageNode = node.getNode("CUSTOM_CASE_MISSING_MESSAGE");
        ConfigurationNode sendKeyMissingMessageNode = node.getNode("SEND_KEY_MISSING_MESSAGE");
        ConfigurationNode customKeyMissingMessageNode = node.getNode("CUSTOM_KEY_MISSING_MESSAGE");
        ConfigurationNode sendPreviewIsNotAvailableMessageNode = node.getNode("SEND_PREVIEW_IS_NOT_AVAILABLE_MESSAGE");
        ConfigurationNode customPreviewIsNotAvailableMessageNode = node.getNode("CUSTOM_PREVIEW_IS_NOT_AVAILABLE_MESSAGE");
        ConfigurationNode sendNoPermissionToOpenMessageNode = node.getNode("SEND_NO_PERMISSION_TO_OPEN_MESSAGE");
        ConfigurationNode customNoPermissionToOpenMessageNode = node.getNode("CUSTOM_NO_PERMISSION_TO_OPEN_MESSAGE");
        ConfigurationNode sendNoPermissionToPreviewMessageNode = node.getNode("SEND_NO_PERMISSION_TO_PREVIEW_MESSAGE");
        ConfigurationNode customNoPermissionToPreviewMessageNode = node.getNode("CUSTOM_NO_PERMISSION_TO_PREVIEW_MESSAGE");
        ConfigurationNode sendCrateDelayMessageNode = node.getNode("SEND_CRATE_DELAY_MESSAGE");
        ConfigurationNode customCrateDelayMessageNode = node.getNode("CUSTOM_CRATE_DELAY_MESSAGE");
        ConfigurationNode sendCannotOpenMessageNode = node.getNode("SEND_CANNOT_OPEN_MANAGER_MESSAGE");
        ConfigurationNode customCannotOpenMessageNode = node.getNode("CUSTOM_CANNOT_OPEN_MANAGER_MESSAGE");
        customInfo = GWMCratesUtils.parseOptionalList(customInfoNode, TypeToken.of(String.class));
        sendOpenMessage = sendOpenMessageNode.getBoolean(true);
        customOpenMessage = GWMCratesUtils.parseOptionalList(customOpenMessageNode, TypeToken.of(String.class));
        sendCaseMissingMessage = sendCaseMissingMessageNode.getBoolean(true);
        customCaseMissingMessage = GWMCratesUtils.parseOptionalList(customCaseMissingMessageNode, TypeToken.of(String.class));
        sendKeyMissingMessage = sendKeyMissingMessageNode.getBoolean(true);
        customKeyMissingMessage = GWMCratesUtils.parseOptionalList(customKeyMissingMessageNode, TypeToken.of(String.class));
        sendPreviewIsNotAvailableMessage = sendPreviewIsNotAvailableMessageNode.getBoolean(true);
        customPreviewIsNotAvailableMessage = GWMCratesUtils.parseOptionalList(customPreviewIsNotAvailableMessageNode, TypeToken.of(String.class));
        sendNoPermissionToOpenMessage = sendNoPermissionToOpenMessageNode.getBoolean(true);
        customNoPermissionToOpenMessage = GWMCratesUtils.parseOptionalList(customNoPermissionToOpenMessageNode, TypeToken.of(String.class));
        sendNoPermissionToPreviewMessage = sendNoPermissionToPreviewMessageNode.getBoolean(true);
        customNoPermissionToPreviewMessage = GWMCratesUtils.parseOptionalList(customNoPermissionToPreviewMessageNode, TypeToken.of(String.class));
        sendCrateDelayMessage = sendCrateDelayMessageNode.getBoolean(true);
        customCrateDelayMessage = GWMCratesUtils.parseOptionalList(customCrateDelayMessageNode, TypeToken.of(String.class));
        sendCannotOpenManagerMessage = sendCannotOpenMessageNode.getBoolean(true);
        customCannotOpenManagerMessage = GWMCratesUtils.parseOptionalList(customCannotOpenMessageNode, TypeToken.of(String.class));
    }

    public ManagerCustomMessageData(Optional<List<String>> customInfo,
                                    boolean sendOpenMessage, Optional<List<String>> customOpenMessage,
                                    boolean sendCaseMissingMessage, Optional<List<String>> customCaseMissingMessage,
                                    boolean sendKeyMissingMessage, Optional<List<String>> customKeyMissingMessage,
                                    boolean sendPreviewIsNotAvailableMessage, Optional<List<String>> customPreviewIsNotAvailableMessage,
                                    boolean sendNoPermissionToOpenMessage, Optional<List<String>> customNoPermissionToOpenMessage,
                                    boolean sendNoPermissionToPreviewMessage, Optional<List<String>> customNoPermissionToPreviewMessage,
                                    boolean sendCrateDelayMessage, Optional<List<String>> customCrateDelayMessage,
                                    boolean sendCannotOpenManagerMessage, Optional<List<String>> customCannotOpenManagerMessage) {
        this.customInfo = customInfo;
        this.sendOpenMessage = sendOpenMessage;
        this.customOpenMessage = customOpenMessage;
        this.sendCaseMissingMessage = sendCaseMissingMessage;
        this.customCaseMissingMessage = customCaseMissingMessage;
        this.sendKeyMissingMessage = sendKeyMissingMessage;
        this.customKeyMissingMessage = customKeyMissingMessage;
        this.sendPreviewIsNotAvailableMessage = sendPreviewIsNotAvailableMessage;
        this.customPreviewIsNotAvailableMessage = customPreviewIsNotAvailableMessage;
        this.sendNoPermissionToOpenMessage = sendNoPermissionToOpenMessage;
        this.customNoPermissionToOpenMessage = customNoPermissionToOpenMessage;
        this.sendNoPermissionToPreviewMessage = sendNoPermissionToPreviewMessage;
        this.customNoPermissionToPreviewMessage = customNoPermissionToPreviewMessage;
        this.sendCrateDelayMessage = sendCrateDelayMessage;
        this.customCrateDelayMessage = customCrateDelayMessage;
        this.sendCannotOpenManagerMessage = sendCannotOpenManagerMessage;
        this.customCannotOpenManagerMessage = customCannotOpenManagerMessage;
    }

    public Optional<List<String>> getCustomInfo() {
        return customInfo;
    }

    public boolean isSendOpenMessage() {
        return sendOpenMessage;
    }

    public Optional<List<String>> getCustomOpenMessage() {
        return customOpenMessage;
    }

    public boolean isSendCaseMissingMessage() {
        return sendCaseMissingMessage;
    }

    public Optional<List<String>> getCustomCaseMissingMessage() {
        return customCaseMissingMessage;
    }

    public boolean isSendKeyMissingMessage() {
        return sendKeyMissingMessage;
    }

    public Optional<List<String>> getCustomKeyMissingMessage() {
        return customKeyMissingMessage;
    }

    public boolean isSendPreviewIsNotAvailableMessage() {
        return sendPreviewIsNotAvailableMessage;
    }

    public Optional<List<String>> getCustomPreviewIsNotAvailableMessage() {
        return customPreviewIsNotAvailableMessage;
    }

    public boolean isSendNoPermissionToOpenMessage() {
        return sendNoPermissionToOpenMessage;
    }

    public Optional<List<String>> getCustomNoPermissionToOpenMessage() {
        return customNoPermissionToOpenMessage;
    }

    public boolean isSendNoPermissionToPreviewMessage() {
        return sendNoPermissionToPreviewMessage;
    }

    public Optional<List<String>> getCustomNoPermissionToPreviewMessage() {
        return customNoPermissionToPreviewMessage;
    }

    public boolean isSendCrateDelayMessage() {
        return sendCrateDelayMessage;
    }

    public Optional<List<String>> getCustomCrateDelayMessage() {
        return customCrateDelayMessage;
    }

    public boolean isSendCannotOpenManagerMessage() {
        return sendCannotOpenManagerMessage;
    }

    public Optional<List<String>> getCustomCannotOpenManagerMessage() {
        return customCannotOpenManagerMessage;
    }
}
