package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.PreviewConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;

public class PermissionPreviewConfigurationDialog extends PreviewConfigurationDialog {

    private JLabel permission_label;
    private AdvancedTextField permission_field;
    private SuperObjectPanel preview1_panel;
    private SuperObjectPanel preview2_panel;

    public PermissionPreviewConfigurationDialog(ConfigurationNode node) {
        super("PERMISSION", node);
        permission_label = new JLabel("Permission");
        permission_label.setLocation(8, 60);
        permission_label.setSize(190, 20);
        add(permission_label);
        permission_field = new AdvancedTextField("Enter permission here...");
        permission_field.setLocation(8, 80);
        permission_field.setSize(190, 20);
        add(permission_field);
        preview1_panel = new SuperObjectPanel(false, "Preview 1", SuperObjectType.PREVIEW, GUIConstants.PREVIEW_TYPES);
        preview1_panel.setLocation(8, 110);
        add(preview1_panel);
        preview2_panel = new SuperObjectPanel(false, "Preview 2", SuperObjectType.PREVIEW, GUIConstants.PREVIEW_TYPES);
        preview2_panel.setLocation(8, 160);
        add(preview2_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode preview1_node = node.getNode("PREVIEW1");
            ConfigurationNode preview2_node = node.getNode("PREVIEW2");
            if (!permission_node.isVirtual()) {
                permission_field.setText(permission_node.getString());
            }
            if (!preview1_node.isVirtual()) {
                preview1_panel.setNode(preview1_node);
            }
            if (!preview2_node.isVirtual()) {
                preview2_panel.setNode(preview2_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Permission Preview Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode preview1_node = node.getNode("PREVIEW1");
            ConfigurationNode preview2_node = node.getNode("PREVIEW2");
            permission_node.setValue(permission_field.hasText() ? permission_field.getText() : null);
            preview1_node.setValue(preview1_panel.getNode());
            preview2_node.setValue(preview2_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Permission Preview Configuration Dialog!", e);
        }
    }
}
