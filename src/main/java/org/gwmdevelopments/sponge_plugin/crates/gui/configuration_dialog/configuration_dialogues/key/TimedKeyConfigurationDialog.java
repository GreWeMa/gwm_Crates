package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckLongFunction;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckLongFunction;

import javax.swing.*;
import java.util.Optional;

public class TimedKeyConfigurationDialog extends CaseConfigurationDialog {

    private JLabel virtual_name_label;
    private AdvancedTextField virtual_name_field;
    private JLabel delay_label;
    private AdvancedTextField delay_field;

    public TimedKeyConfigurationDialog(ConfigurationNode node) {
        super("TIMED", node);
        virtual_name_label = new JLabel("Virtual name");
        virtual_name_label.setLocation(8, 110);
        virtual_name_label.setSize(190, 20);
        add(virtual_name_label);
        virtual_name_field = new AdvancedTextField("Enter virtual name here...");
        virtual_name_field.setLocation(8, 130);
        virtual_name_field.setSize(190, 20);
        add(virtual_name_field);
        delay_label = new JLabel("Delay");
        delay_label.setLocation(8, 160);
        delay_label.setSize(190, 20);
        add(delay_label);
        delay_field = new AdvancedTextField("Enter delay here...");
        delay_field.setLocation(8, 180);
        delay_field.setSize(190, 20);
        delay_field.setFunction(Optional.of(new CheckLongFunction()));
        add(delay_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            ConfigurationNode delay_node = node.getNode("DELAY");
            if (!virtual_name_node.isVirtual()) {
                virtual_name_field.setText(virtual_name_node.getString());
            }
            if (!delay_node.isVirtual()) {
                delay_field.setText(String.valueOf(delay_node.getLong()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Timed Key Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            ConfigurationNode delay_node = node.getNode("DELAY");
            virtual_name_node.setValue(virtual_name_field.hasText() ? virtual_name_field.getText() : null);
            delay_node.setValue(delay_field.hasText() ? delay_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Timed Key Configuration Dialog!", e);
        }
    }
}
