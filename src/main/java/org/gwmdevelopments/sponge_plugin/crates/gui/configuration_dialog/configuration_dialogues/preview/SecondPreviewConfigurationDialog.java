package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.PreviewConfigurationDialog;

import javax.swing.*;

public class SecondPreviewConfigurationDialog extends PreviewConfigurationDialog {

    private JLabel display_name_label;
    private AdvancedTextField display_name_field;

    public SecondPreviewConfigurationDialog(ConfigurationNode node) {
        super("SECOND", node);
        display_name_label = new JLabel("Display name");
        display_name_label.setLocation(8, 60);
        display_name_label.setSize(190, 20);
        add(display_name_label);
        display_name_field = new AdvancedTextField("Enter display name here...");
        display_name_field.setLocation(8, 90);
        display_name_field.setSize(190, 20);
        add(display_name_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            if (!display_name_node.isVirtual()) {
                display_name_field.setText(display_name_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Second Preview Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            display_name_node.setValue(display_name_field.hasText() ? display_name_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Second Preview Configuration Dialog!", e);
        }
    }
}
