package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;

import javax.swing.*;

public class VirtualCaseConfigurationDialog extends CaseConfigurationDialog {

    private JLabel virtual_name_label;
    private AdvancedTextField virtual_name_field;

    public VirtualCaseConfigurationDialog(ConfigurationNode node) {
        super("VIRTUAL", node);
        virtual_name_label = new JLabel("Virtual name");
        virtual_name_label.setLocation(8, 110);
        virtual_name_label.setSize(190, 20);
        add(virtual_name_label);
        virtual_name_field = new AdvancedTextField("Enter virtual name here...");
        virtual_name_field.setLocation(8, 130);
        virtual_name_field.setSize(190, 20);
        add(virtual_name_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            if (!virtual_name_node.isVirtual()) {
                virtual_name_field.setText(virtual_name_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Virtual Case Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            virtual_name_node.setValue(virtual_name_field.hasText() ? virtual_name_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Virtual Case Configuration Dialog!", e);
        }
    }
}
