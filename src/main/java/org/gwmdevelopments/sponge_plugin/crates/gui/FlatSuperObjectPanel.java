package org.gwmdevelopments.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class FlatSuperObjectPanel extends JPanel {

    private SuperObjectType super_object_type;
    private DeleteButton delete_button;
    private AdvancedComboBox combo_box;
    private AdvancedConfigurationButton configure_button;
    private ConfigurationNode node;

    public FlatSuperObjectPanel(boolean full, SuperObjectType super_object_type, String[] strings) {
        this.super_object_type = super_object_type;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        delete_button = new DeleteButton();
        add(delete_button);
        combo_box = new AdvancedComboBox(strings);
        combo_box.setEditable(true);
        Dimension combo_box_dimension = full ? new Dimension(210, 20) : new Dimension(100, 20);
        combo_box.setPreferredSize(combo_box_dimension);
        combo_box.setMaximumSize(combo_box_dimension);
        combo_box.addActionListener(e -> {
            combo_box.setEnabled(false);
            node.getNode("TYPE").setValue(getType());
        });
        add(combo_box);
        configure_button = new AdvancedConfigurationButton(super_object_type, this::getType, this::getNode);
        add(configure_button);
        node = SimpleConfigurationNode.root();
    }

    public void clear() {
        combo_box.setSelectedIndex(0);
        combo_box.setEnabled(true);
        node = SimpleConfigurationNode.root();
    }

    public SuperObjectType getSuperObjectType() {
        return super_object_type;
    }

    public @Nullable String getType() {
        return combo_box.isEnabled() ? null : combo_box.getSelectedItem().toString().toUpperCase();
    }


    public DeleteButton getDeleteButton() {
        return delete_button;
    }

    public AdvancedComboBox getComboBox() {
        return combo_box;
    }

    public AdvancedConfigurationButton getConfigureButton() {
        return configure_button;
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
