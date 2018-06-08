package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.ChangeModeConfigurationDialog;

import javax.swing.*;

public class OrderedChangeModeConfigurationDialog extends ChangeModeConfigurationDialog {

    private JLabel right_label;
    private JCheckBox right_check_box;

    public OrderedChangeModeConfigurationDialog(ConfigurationNode node) {
        super("ORDERED", node);
        right_label = new JLabel("Right");
        right_label.setLocation(8, 160);
        right_label.setSize(170, 20);
        add(right_label);
        right_check_box = new JCheckBox();
        right_check_box.setLocation(178, 160);
        right_check_box.setSize(20, 20);
        add(right_check_box);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode right_node = node.getNode("RIGHT");
            if (!right_node.isVirtual()) {
                right_check_box.setSelected(right_node.getBoolean());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Ordered Change Mode Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode right_node = node.getNode("RIGHT");
            right_node.setValue(right_check_box.isSelected());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Ordered Change Mode Configuration Dialog!", e);
        }
    }
}
