package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.DropConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;

public class DelayDropConfigurationDialog extends DropConfigurationDialog {

    private SuperObjectPanel child_drop_panel;
    private JLabel delay_label;
    private AdvancedTextField delay_field;

    public DelayDropConfigurationDialog(ConfigurationNode node) {
        super("DELAY", node);
        child_drop_panel = new SuperObjectPanel(false, "Child Drop", SuperObjectType.DROP, GUIConstants.DROP_TYPES);
        child_drop_panel.setLocation(404, 10);
        add(child_drop_panel);
        delay_label = new JLabel("DELAY");
        delay_label.setLocation(404, 60);
        delay_label.setSize(190, 20);
        delay_label.setToolTipText("Milliseconds");
        add(delay_label);
        delay_field = new AdvancedTextField("Enter delay here...");
        delay_field.setLocation(404, 80);
        delay_field.setSize(190, 20);
        add(delay_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode child_drop_node = node.getNode("CHILD_DROP");
            ConfigurationNode delay_node = node.getNode("DELAY");
            if (!child_drop_node.isVirtual()) {
                child_drop_panel.setNode(child_drop_node);
            }
            if (!delay_node.isVirtual()) {
                delay_field.setText(String.valueOf(delay_node.getInt()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Delay Drop Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode child_drop_node = node.getNode("CHILD_DROP");
            ConfigurationNode delay_node = node.getNode("DELAY");
            child_drop_node.setValue(child_drop_panel.getNode());
            delay_node.setValue(delay_field.hasText() ? delay_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Delay Drop Configuration Dialog!", e);
        }
    }
}
