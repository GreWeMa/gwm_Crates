package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.OpenManagerConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;

public class PermissionOpenManagerConfigurationDialog extends OpenManagerConfigurationDialog {

    private JLabel permission_label;
    private AdvancedTextField permission_field;
    private SuperObjectPanel open_manager1_panel;
    private SuperObjectPanel open_manager2_panel;

    public PermissionOpenManagerConfigurationDialog(ConfigurationNode node) {
        super("PERMISSION", node);
        permission_label = new JLabel("Permission");
        permission_label.setLocation(8, 110);
        permission_label.setSize(190, 20);
        add(permission_label);
        permission_field = new AdvancedTextField("Enter permission here...");
        permission_field.setLocation(8, 130);
        permission_field.setSize(190, 20);
        add(permission_field);
        open_manager1_panel = new SuperObjectPanel(false, "Open Manager 1", SuperObjectType.OPEN_MANAGER, GUIConstants.OPEN_MANAGER_TYPES);
        open_manager1_panel.setLocation(8, 160);
        add(open_manager1_panel);
        open_manager2_panel = new SuperObjectPanel(false, "Open Manager 2", SuperObjectType.OPEN_MANAGER, GUIConstants.OPEN_MANAGER_TYPES);
        open_manager2_panel.setLocation(8, 210);
        add(open_manager2_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode open_manager1_node = node.getNode("OPEN_MANAGER1");
            ConfigurationNode open_manager2_node = node.getNode("OPEN_MANAGER2");
            if (!permission_node.isVirtual()) {
                permission_field.setText(permission_node.getString());
            }
            if (!open_manager1_node.isVirtual()) {
                open_manager1_panel.setNode(open_manager1_node);
            }
            if (!open_manager2_node.isVirtual()) {
                open_manager2_panel.setNode(open_manager2_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Permission Open Manager Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode open_manager1_node = node.getNode("OPEN_MANAGER1");
            ConfigurationNode open_manager2_node = node.getNode("OPEN_MANAGER2");
            permission_node.setValue(permission_field.hasText() ? permission_field.getText() : null);
            open_manager1_node.setValue(open_manager1_panel.getNode());
            open_manager2_node.setValue(open_manager2_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Permission Open Manager Configuration Dialog!", e);
        }
    }
}
