package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedComboBox;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.SuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.OpenManagerConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.util.Optional;

public class Animation1OpenManagerConfigurationDialog extends OpenManagerConfigurationDialog {

    private JLabel floor_block_type_label;
    private AdvancedComboBox floor_block_type_combo_box;
    private JLabel fence_block_type_label;
    private AdvancedComboBox fence_block_type_combo_box;
    private JLabel crate_block_type_label;
    private AdvancedComboBox crate_block_type_combo_box;
    private JLabel hologram_label;
    private AdvancedTextField hologram_field;
    private JLabel close_delay_label;
    private AdvancedTextField close_delay_field;
    private SuperObjectPanel open_manager_panel;

    public Animation1OpenManagerConfigurationDialog(ConfigurationNode node) {
        super("ANIMATION1", node);
        floor_block_type_label = new JLabel("Floor block type");
        floor_block_type_label.setLocation(8, 110);
        floor_block_type_label.setSize(190, 20);
        add(floor_block_type_label);
        floor_block_type_combo_box = new AdvancedComboBox(GWMCratesUtils.getBlockTypes(false));
        floor_block_type_combo_box.setLocation(8, 130);
        floor_block_type_combo_box.setSize(190, 20);
        add(floor_block_type_combo_box);
        fence_block_type_label = new JLabel("Fence block type");
        fence_block_type_label.setLocation(8, 160);
        fence_block_type_label.setSize(190, 20);
        add(fence_block_type_label);
        fence_block_type_combo_box = new AdvancedComboBox(GWMCratesUtils.getBlockTypes(false));
        fence_block_type_combo_box.setLocation(8, 180);
        fence_block_type_combo_box.setSize(190, 20);
        add(fence_block_type_combo_box);
        crate_block_type_label = new JLabel("Crate block type");
        crate_block_type_label.setLocation(8, 210);
        crate_block_type_label.setSize(190, 20);
        add(crate_block_type_label);
        crate_block_type_combo_box = new AdvancedComboBox(GWMCratesUtils.getBlockTypes(false));
        crate_block_type_combo_box.setLocation(8, 230);
        crate_block_type_combo_box.setSize(190, 20);
        add(crate_block_type_combo_box);
        hologram_label = new JLabel("Hologram");
        hologram_label.setLocation(8, 260);
        hologram_label.setSize(190, 20);
        add(hologram_label);
        hologram_field = new AdvancedTextField("Enter hologram text here...");
        hologram_field.setLocation(8, 280);
        hologram_field.setSize(190, 20);
        add(hologram_field);
        close_delay_label = new JLabel("Close delay");
        close_delay_label.setLocation(8, 310);
        close_delay_label.setSize(190, 20);
        add(close_delay_label);
        close_delay_field = new AdvancedTextField("Enter delay here...");
        close_delay_field.setLocation(8, 330);
        close_delay_field.setSize(190, 20);
        close_delay_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(close_delay_field);
        open_manager_panel = new SuperObjectPanel(false, "Open Manager", SuperObjectType.OPEN_MANAGER, GUIConstants.OPEN_MANAGER_TYPES);
        open_manager_panel.setLocation(8, 360);
        add(open_manager_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode floor_block_type_node = node.getNode("FLOOR_BLOCK_TYPE");
            ConfigurationNode fence_block_type_node = node.getNode("FENCE_BLOCK_TYPE");
            ConfigurationNode crate_block_type_node = node.getNode("CRATE_BLOCK_TYPE");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode open_manager_node = node.getNode("OPEN_MANAGER");
            if (!floor_block_type_node.isVirtual()) {
                floor_block_type_combo_box.setSelectedItem(floor_block_type_node.getString());
            }
            if (!fence_block_type_node.isVirtual()) {
                fence_block_type_combo_box.setSelectedItem(fence_block_type_node.getString());
            }
            if (!crate_block_type_node.isVirtual()) {
                crate_block_type_combo_box.setSelectedItem(crate_block_type_node.getString());
            }
            if (!hologram_node.isVirtual()) {
                hologram_field.setText(hologram_node.getString());
            }
            if (!close_delay_node.isVirtual()) {
                close_delay_field.setText(String.valueOf(close_delay_node.getInt()));
            }
            if (!open_manager_node.isVirtual()) {
                open_manager_panel.setNode(open_manager_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Animation1 Open Manger Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode floor_block_type_node = node.getNode("FLOOR_BLOCK_TYPE");
            ConfigurationNode fence_block_type_node = node.getNode("FENCE_BLOCK_TYPE");
            ConfigurationNode crate_block_type_node = node.getNode("CRATE_BLOCK_TYPE");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode open_manager_node = node.getNode("OPEN_MANAGER");
            floor_block_type_node.setValue(floor_block_type_combo_box.getSelectedText());
            fence_block_type_node.setValue(fence_block_type_combo_box.getSelectedText());
            crate_block_type_node.setValue(crate_block_type_combo_box.getSelectedText());
            hologram_node.setValue(hologram_field.hasText() ? hologram_field.getText() : null);
            close_delay_node.setValue(close_delay_field.hasText() ? close_delay_field.getText() : null);
            open_manager_node.setValue(open_manager_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Animation1 Open Manager Configuration Dialog!", e);
        }
    }
}
