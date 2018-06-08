package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.ConfigurationButton;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.PreviewConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.Optional;

public class FirstPreviewConfigurationDialog extends PreviewConfigurationDialog {

    private JLabel display_name_label;
    private AdvancedTextField display_name_field;
    private JLabel decorative_items_label;
    private ConfigurationButton decorative_items_button;
    private JLabel scroll_delay_label;
    private AdvancedTextField scroll_delay_field;
    private SuperObjectPanel decorative_items_change_mode_panel;

    public FirstPreviewConfigurationDialog(ConfigurationNode node) {
        super("FIRST", node);
        display_name_label = new JLabel("Display name");
        display_name_label.setLocation(8, 60);
        display_name_label.setSize(190, 20);
        add(display_name_label);
        display_name_field = new AdvancedTextField("Enter display name here...");
        display_name_field.setLocation(8, 80);
        display_name_field.setSize(190, 20);
        add(display_name_field);
        decorative_items_label = new JLabel("Decorative items");
        decorative_items_label.setLocation(8, 110);
        decorative_items_label.setSize(140, 20);
        add(decorative_items_label);
        decorative_items_button = new ConfigurationButton();
        decorative_items_button.setLocation(148, 110);
        add(decorative_items_button);
        scroll_delay_label = new JLabel("Scroll delay");
        scroll_delay_label.setLocation(8, 140);
        scroll_delay_label.setSize(190, 20);
        add(scroll_delay_label);
        scroll_delay_field = new AdvancedTextField("Enter scroll delay here...");
        scroll_delay_field.setLocation(8, 160);
        scroll_delay_field.setSize(190, 20);
        scroll_delay_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(scroll_delay_field);
        decorative_items_change_mode_panel = new SuperObjectPanel(false, "Change mode", SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, GUIConstants.DECORATIVE_ITEMS_CHANGE_MODE_TYPES);
        decorative_items_change_mode_panel.setLocation(8, 190);
        add(decorative_items_change_mode_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorative_items_node = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scroll_delay_node = node.getNode("SCROLL_DELAY");
            ConfigurationNode decorative_items_change_mode_node = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!display_name_node.isVirtual()) {
                display_name_field.setText(display_name_node.getString());
            }
            if (!scroll_delay_node.isVirtual()) {
                scroll_delay_field.setText(String.valueOf(scroll_delay_node.getInt()));
            }
            if (!decorative_items_change_mode_node.isVirtual()) {
                decorative_items_change_mode_panel.setNode(decorative_items_change_mode_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading First Preview Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorative_items_node = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scroll_delay_node = node.getNode("SCROLL_DELAY");
            ConfigurationNode decorative_items_change_mode_node = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            display_name_node.setValue(display_name_field.hasText() ? display_name_field.getText() : null);
            scroll_delay_node.setValue(scroll_delay_field.hasText() ? scroll_delay_field.getText() : null);
            decorative_items_change_mode_node.setValue(decorative_items_change_mode_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving First Preview Configuration Dialog!", e);
        }
    }
}
