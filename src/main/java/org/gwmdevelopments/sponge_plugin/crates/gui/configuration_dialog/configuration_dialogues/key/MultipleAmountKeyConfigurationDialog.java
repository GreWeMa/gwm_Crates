package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.KeyConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.Optional;

public class MultipleAmountKeyConfigurationDialog extends KeyConfigurationDialog {

    private SuperObjectPanel child_key_panel;
    private JLabel amount_label;
    private AdvancedTextField amount_field;

    public MultipleAmountKeyConfigurationDialog(ConfigurationNode node) {
        super("MULTIPLE-AMOUNT", node);
        child_key_panel = new SuperObjectPanel(false, "Child Key", SuperObjectType.KEY, GUIConstants.KEY_TYPES);
        child_key_panel.setLocation(8, 110);
        add(child_key_panel);
        amount_label = new JLabel("Amount");
        amount_label.setLocation(8, 160);
        amount_label.setSize(190, 20);
        add(amount_label);
        amount_field = new AdvancedTextField("Enter amount here...");
        amount_field.setLocation(8, 180);
        amount_field.setSize(190, 20);
        amount_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(amount_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode child_key_node = node.getNode("CHILD_KEY");
            ConfigurationNode amount_node = node.getNode("AMOUNT");
            if (!child_key_node.isVirtual()) {
                child_key_panel.setNode(child_key_node);
            }
            if (!amount_node.isVirtual()) {
                amount_field.setText(String.valueOf(amount_node.getInt()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Multiple Amount Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode child_key_node = node.getNode("CHILD_KEY");
            ConfigurationNode amount_node = node.getNode("AMOUNT");
            child_key_node.setValue(child_key_panel.getNode());
            amount_node.setValue(amount_field.hasText() ? amount_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Multiple Amount Configuration Dialog!", e);
        }
    }
}
