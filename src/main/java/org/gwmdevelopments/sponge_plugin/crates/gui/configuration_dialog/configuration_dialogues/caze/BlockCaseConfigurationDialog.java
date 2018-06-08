package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.LocationPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;

import javax.swing.*;
import java.awt.*;

public class BlockCaseConfigurationDialog extends CaseConfigurationDialog {

    private JLabel location_label;
    private LocationPanel location_panel;
    private JLabel hologram_label;
    private AdvancedTextField hologram_field;
    private JLabel start_preview_on_left_click_label;
    private JCheckBox start_preview_on_left_click_check_box;

    public BlockCaseConfigurationDialog(ConfigurationNode node) {
        super("BLOCK", node);
        location_label = new JLabel("Location");
        location_label.setLocation(8, 110);
        location_label.setSize(190, 20);
        add(location_label);
        location_panel = new LocationPanel(true);
        location_panel.setLocation(8, 130);
        add(location_panel);
        hologram_label = new JLabel("Hologram");
        hologram_label.setLocation(8, 340);
        hologram_label.setSize(190, 20);
        hologram_label.setToolTipText("Optional");
        add(hologram_label);
        hologram_field = new AdvancedTextField("Enter hologram name here...");
        hologram_field.setLocation(8, 360);
        hologram_field.setSize(190, 20);
        add(hologram_field);
        start_preview_on_left_click_label = new JLabel("Start preview on left click");
        start_preview_on_left_click_label.setLocation(8, 390);
        start_preview_on_left_click_label.setSize(170, 20);
        start_preview_on_left_click_label.setFont(new Font("Arial", Font.BOLD, 11));
        add(start_preview_on_left_click_label);
        start_preview_on_left_click_check_box = new JCheckBox();
        start_preview_on_left_click_check_box.setLocation(178, 390);
        start_preview_on_left_click_check_box.setSize(20, 20);
        add(start_preview_on_left_click_check_box);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode location_node = node.getNode("LOCATION");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            location_panel.load(location_node);
            if (!hologram_node.isVirtual()) {
                hologram_field.setText(hologram_node.getString());
            }
            if (!start_preview_on_left_click_node.isVirtual()) {
                start_preview_on_left_click_check_box.setSelected(start_preview_on_left_click_node.getBoolean());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Block Case Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode location_node = node.getNode("LOCATION");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode start_preview_on_left_click = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            location_node.setValue(location_panel.save());
            hologram_node.setValue(hologram_field.hasText() ? hologram_field.getText() : null);
            start_preview_on_left_click.setValue(start_preview_on_left_click_check_box.isSelected() ? true : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Block Case Configuration Dialog!", e);
        }
    }
}
