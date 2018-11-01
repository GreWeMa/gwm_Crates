package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckDoubleFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.Optional;

public abstract class KeyConfigurationDialog extends ConfigurationDialog {

    private JLabel price_label;
    private AdvancedTextField price_field;

    public KeyConfigurationDialog(String type, ConfigurationNode node) {
        super(SuperObjectType.KEY, type, node);
        price_label = new JLabel("Price");
        price_label.setLocation(8, 60);
        price_label.setSize(190, 20);
        add(price_label);
        price_field = new AdvancedTextField("Enter price here...");
        price_field.setLocation(8, 80);
        price_field.setSize(190, 20);
        price_field.setToolTipText("Fractional number. Optional.");
        price_field.setFunction(Optional.of(new CheckDoubleFunction()));
        add(price_field);
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode price_node = node.getNode("PRICE");
            if (!price_node.isVirtual()) {
                price_field.setText(String.valueOf(price_node.getDouble()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Key Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode price_node = node.getNode("PRICE");
            price_node.setValue(price_field.hasText() ? price_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Key Configuration Dialog!", e);
        }
    }
}
