package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;

public class SavedSuperObjectConfigurationDialog extends ConfigurationDialog {

    private JLabel saved_id_label;
    private AdvancedTextField saved_id_field;

    public SavedSuperObjectConfigurationDialog(SuperObjectType super_object_type, String type, ConfigurationNode node) {
        super(super_object_type, type, node);
        saved_id_label = new JLabel("SAVED_ID");
        saved_id_label.setLocation(8, 60);
        saved_id_label.setSize(190, 20);
        add(saved_id_label);
        saved_id_field = new AdvancedTextField("Enter saved id here...");
        saved_id_field.setLocation(8, 80);
        saved_id_field.setSize(190, 20);
        add(saved_id_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode saved_id_node = node.getNode("SAVED_ID");
            if (!saved_id_node.isVirtual()) {
                saved_id_field.setText(saved_id_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Saved Super Object Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode saved_id_node = node.getNode("SAVED_ID");
            saved_id_node.setValue(saved_id_field.hasText() ? saved_id_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Saved Super Object Configuration Dialog!", e);
        }
    }
}
