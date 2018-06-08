package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.DropConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;

public class PermissionDropConfigurationDialog extends DropConfigurationDialog {

    private JLabel permission_label;
    private AdvancedTextField permission_field;
    private SuperObjectPanel drop1_panel;
    private SuperObjectPanel drop2_panel;

    public PermissionDropConfigurationDialog(ConfigurationNode node) {
        super("PERMISSION", node);
        permission_label = new JLabel("Permission");
        permission_label.setLocation(404, 10);
        permission_label.setSize(190, 20);
        add(permission_label);
        permission_field = new AdvancedTextField("Enter permission here...");
        permission_field.setLocation(404, 30);
        permission_field.setSize(190, 20);
        add(permission_field);
        drop1_panel = new SuperObjectPanel(false, "Drop 1", SuperObjectType.DROP, GUIConstants.DROP_TYPES);
        drop1_panel.setLocation(404, 60);
        add(drop1_panel);
        drop2_panel = new SuperObjectPanel(false, "Drop 2", SuperObjectType.DROP, GUIConstants.DROP_TYPES);
        drop2_panel.setLocation(404, 110);
        add(drop2_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode drop1_node = node.getNode("DROP1");
            ConfigurationNode drop2_node = node.getNode("DROP2");
            if (!permission_node.isVirtual()) {
                permission_field.setText(permission_node.getString());
            }
            if (!drop1_node.isVirtual()) {
                drop1_panel.setNode(drop1_node);
            }
            if (!drop2_node.isVirtual()) {
                drop2_panel.setNode(drop2_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Permission Drop Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode drop1_node = node.getNode("DROP1");
            ConfigurationNode drop2_node = node.getNode("DROP2");
            permission_node.setValue(permission_field.hasText() ? permission_field.getText() : null);
            drop1_node.setValue(drop1_panel.getNode());
            drop2_node.setValue(drop2_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Permission Drop Configuration Dialog!", e);
        }
    }
}
