package dev.gwm.spongeplugin.crates.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class ManagerCustomMessageData {

    private final Optional<List<Text>> customInfo;
    private final boolean sendOpenMessage;
    private final Optional<List<Text>> customOpenMessage;
    private final boolean sendCaseMissingMessage;
    private final Optional<List<Text>> customCaseMissingMessage;
    private final boolean sendKeyMissingMessage;
    private final Optional<List<Text>> customKeyMissingMessage;
    private final boolean sendPreviewIsNotAvailableMessage;
    private final Optional<List<Text>> customPreviewIsNotAvailableMessage;
    private final boolean sendNoPermissionToOpenMessage;
    private final Optional<List<Text>> customNoPermissionToOpenMessage;
    private final boolean sendNoPermissionToPreviewMessage;
    private final Optional<List<Text>> customNoPermissionToPreviewMessage;
    private final boolean sendCrateDelayMessage;
    private final Optional<List<Text>> customCrateDelayMessage;
    private final boolean sendCannotOpenManagerMessage;
    private final Optional<List<Text>> customCannotOpenManagerMessage;

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
        customInfo = GWMCratesUtils.parseOptionalList(customInfoNode, TypeToken.of(Text.class));
        sendOpenMessage = sendOpenMessageNode.getBoolean(true);
        customOpenMessage = GWMCratesUtils.parseOptionalList(customOpenMessageNode, TypeToken.of(Text.class));
        sendCaseMissingMessage = sendCaseMissingMessageNode.getBoolean(true);
        customCaseMissingMessage = GWMCratesUtils.parseOptionalList(customCaseMissingMessageNode, TypeToken.of(Text.class));
        sendKeyMissingMessage = sendKeyMissingMessageNode.getBoolean(true);
        customKeyMissingMessage = GWMCratesUtils.parseOptionalList(customKeyMissingMessageNode, TypeToken.of(Text.class));
        sendPreviewIsNotAvailableMessage = sendPreviewIsNotAvailableMessageNode.getBoolean(true);
        customPreviewIsNotAvailableMessage = GWMCratesUtils.parseOptionalList(customPreviewIsNotAvailableMessageNode, TypeToken.of(Text.class));
        sendNoPermissionToOpenMessage = sendNoPermissionToOpenMessageNode.getBoolean(true);
        customNoPermissionToOpenMessage = GWMCratesUtils.parseOptionalList(customNoPermissionToOpenMessageNode, TypeToken.of(Text.class));
        sendNoPermissionToPreviewMessage = sendNoPermissionToPreviewMessageNode.getBoolean(true);
        customNoPermissionToPreviewMessage = GWMCratesUtils.parseOptionalList(customNoPermissionToPreviewMessageNode, TypeToken.of(Text.class));
        sendCrateDelayMessage = sendCrateDelayMessageNode.getBoolean(true);
        customCrateDelayMessage = GWMCratesUtils.parseOptionalList(customCrateDelayMessageNode, TypeToken.of(Text.class));
        sendCannotOpenManagerMessage = sendCannotOpenMessageNode.getBoolean(true);
        customCannotOpenManagerMessage = GWMCratesUtils.parseOptionalList(customCannotOpenMessageNode, TypeToken.of(Text.class));
    }

    public ManagerCustomMessageData(Optional<List<Text>> customInfo,
                                    boolean sendOpenMessage, Optional<List<Text>> customOpenMessage,
                                    boolean sendCaseMissingMessage, Optional<List<Text>> customCaseMissingMessage,
                                    boolean sendKeyMissingMessage, Optional<List<Text>> customKeyMissingMessage,
                                    boolean sendPreviewIsNotAvailableMessage, Optional<List<Text>> customPreviewIsNotAvailableMessage,
                                    boolean sendNoPermissionToOpenMessage, Optional<List<Text>> customNoPermissionToOpenMessage,
                                    boolean sendNoPermissionToPreviewMessage, Optional<List<Text>> customNoPermissionToPreviewMessage,
                                    boolean sendCrateDelayMessage, Optional<List<Text>> customCrateDelayMessage,
                                    boolean sendCannotOpenManagerMessage, Optional<List<Text>> customCannotOpenManagerMessage) {
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

    public Optional<List<Text>> getCustomInfo() {
        return customInfo;
    }

    public boolean isSendOpenMessage() {
        return sendOpenMessage;
    }

    public Optional<List<Text>> getCustomOpenMessage() {
        return customOpenMessage;
    }

    public boolean isSendCaseMissingMessage() {
        return sendCaseMissingMessage;
    }

    public Optional<List<Text>> getCustomCaseMissingMessage() {
        return customCaseMissingMessage;
    }

    public boolean isSendKeyMissingMessage() {
        return sendKeyMissingMessage;
    }

    public Optional<List<Text>> getCustomKeyMissingMessage() {
        return customKeyMissingMessage;
    }

    public boolean isSendPreviewIsNotAvailableMessage() {
        return sendPreviewIsNotAvailableMessage;
    }

    public Optional<List<Text>> getCustomPreviewIsNotAvailableMessage() {
        return customPreviewIsNotAvailableMessage;
    }

    public boolean isSendNoPermissionToOpenMessage() {
        return sendNoPermissionToOpenMessage;
    }

    public Optional<List<Text>> getCustomNoPermissionToOpenMessage() {
        return customNoPermissionToOpenMessage;
    }

    public boolean isSendNoPermissionToPreviewMessage() {
        return sendNoPermissionToPreviewMessage;
    }

    public Optional<List<Text>> getCustomNoPermissionToPreviewMessage() {
        return customNoPermissionToPreviewMessage;
    }

    public boolean isSendCrateDelayMessage() {
        return sendCrateDelayMessage;
    }

    public Optional<List<Text>> getCustomCrateDelayMessage() {
        return customCrateDelayMessage;
    }

    public boolean isSendCannotOpenManagerMessage() {
        return sendCannotOpenManagerMessage;
    }

    public Optional<List<Text>> getCustomCannotOpenManagerMessage() {
        return customCannotOpenManagerMessage;
    }
}
