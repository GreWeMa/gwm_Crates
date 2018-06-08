package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntListFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntListFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.util.Optional;

public abstract class ChangeModeConfigurationDialog extends ConfigurationDialog {

    private JLabel change_delay_label;
    private AdvancedTextField change_delay_field;
    private JLabel ignored_indices_label;
    private AdvancedTextField ignored_indices_field;

    public ChangeModeConfigurationDialog(String type, ConfigurationNode node) {
        super(SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, type, node);
        change_delay_label = new JLabel("Change delay");
        change_delay_label.setLocation(8, 60);
        change_delay_label.setSize(190, 20);
        add(change_delay_label);
        change_delay_field = new AdvancedTextField("Enter delay here...");
        change_delay_field.setLocation(8, 80);
        change_delay_field.setSize(190, 20);
        add(change_delay_field);
        ignored_indices_label = new JLabel("Ignored indices");
        ignored_indices_label.setLocation(8, 110);
        ignored_indices_label.setSize(190, 20);
        add(ignored_indices_label);
        ignored_indices_field = new AdvancedTextField("Enter ignored indices here...");
        ignored_indices_field.setLocation(8, 130);
        ignored_indices_field.setSize(190, 20);
        ignored_indices_field.setFunction(Optional.of(new CheckIntListFunction()));
        add(ignored_indices_field);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode change_delay_node = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignored_indices_node = node.getNode("IGNORED_INDICES");
            if (!change_delay_node.isVirtual()) {
                change_delay_field.setText(String.valueOf(change_delay_node.getInt()));
            }
            if (!ignored_indices_node.isVirtual()) {
                ignored_indices_field.setText(GWMCratesUtils.intListToString(ignored_indices_node.getList(TypeToken.of(Integer.class))));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Change Mode Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode change_delay_node = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignored_indices_node = node.getNode("IGNORED_INDICES");
            change_delay_node.setValue(change_delay_field.hasText() ? change_delay_field.getText() : null);
            ignored_indices_node.setValue(ignored_indices_field.hasText() ? GWMCratesUtils.stringToIntList(ignored_indices_field.getText()) : null);
         } catch (Exception e) {
            throw new RuntimeException("Exception saving Change Mode Configuration Dialog!", e);
        }
    }
}
