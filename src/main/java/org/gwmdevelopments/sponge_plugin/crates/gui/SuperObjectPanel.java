package org.gwmdevelopments.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.annotation.Nullable;
import javax.swing.*;

public class SuperObjectPanel extends JPanel {

    private SuperObjectType super_object_type;
    private JLabel label;
    private DeleteButton delete_button;
    private AdvancedConfigurationButton configure_button;
    private AdvancedComboBox combo_box;
    private ConfigurationNode node;

    public SuperObjectPanel(boolean full, String string, SuperObjectType super_object_type, String[] strings) {
        this.super_object_type = super_object_type;
        setSize(300, 40);
        setLayout(null);
        label = new JLabel(string);
        label.setLocation(0, 0);
        if (full) {
            label.setSize(180, 20);
        } else {
            label.setSize(120, 20);
        }
        add(label);
        delete_button = new DeleteButton();
        if (full) {
            delete_button.setLocation(230, 0);
        } else {
            delete_button.setLocation(120, 0);
        }
        delete_button.addActionListener(e -> clear());
        add(delete_button);
        configure_button = new AdvancedConfigurationButton(super_object_type, this::getType, this::getNode);
        if (full) {
            configure_button.setLocation(250, 0);
        } else {
            configure_button.setLocation(140, 0);
        }
        add(configure_button);
        combo_box = new AdvancedComboBox(strings);
        combo_box.setEditable(true);
        combo_box.setLocation(0, 20);
        if (full) {
            combo_box.setSize(300, 20);
        } else {
            combo_box.setSize(190, 20);
        }
        combo_box.addActionListener(e -> {
            combo_box.setEnabled(false);
            node.getNode("TYPE").setValue(getType());
        });
        add(combo_box);
        node = SimpleConfigurationNode.root();
    }

    public void clear() {
        node = SimpleConfigurationNode.root();
        combo_box.setSelectedIndex(0);
        combo_box.setEnabled(true);
    }

    public SuperObjectType getSuperObjectType() {
        return super_object_type;
    }

    public @Nullable String getType() {
        return combo_box.isEnabled() ? null : combo_box.getSelectedText();
    }

    public JLabel getLabel() {
        return label;
    }

    public DeleteButton getDeleteButton() {
        return delete_button;
    }

    public JButton getConfigureButton() {
        return configure_button;
    }

    public AdvancedComboBox getComboBox() {
        return combo_box;
    }

    public ConfigurationNode getNode() {
        return node;
    }

    public void setNode(ConfigurationNode node) {
        this.node = node;
        combo_box.setSelectedItem(node.getNode("TYPE").getString());
        combo_box.setEnabled(false);
    }
}
