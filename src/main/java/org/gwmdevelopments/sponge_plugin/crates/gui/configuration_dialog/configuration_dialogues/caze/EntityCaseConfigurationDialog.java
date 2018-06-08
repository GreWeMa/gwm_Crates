package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;

import javax.swing.*;
import java.awt.*;

public class EntityCaseConfigurationDialog extends CaseConfigurationDialog {

    private JLabel entity_uuid_label;
    private AdvancedTextField entity_uuid_field;
    private JLabel start_preview_on_left_click_label;
    private JCheckBox start_preview_on_left_click_check_box;

    public EntityCaseConfigurationDialog(ConfigurationNode node) {
        super("ENTITY", node);
        entity_uuid_label = new JLabel("UUID");
        entity_uuid_label.setLocation(8, 110);
        entity_uuid_label.setSize(190, 20);
        add(entity_uuid_label);
        entity_uuid_field = new AdvancedTextField("Enter uuid here...");
        entity_uuid_field.setLocation(8, 130);
        entity_uuid_field.setSize(190, 20);
        add(entity_uuid_field);
        start_preview_on_left_click_label = new JLabel("Start preview on left click");
        start_preview_on_left_click_label.setLocation(8, 150);
        start_preview_on_left_click_label.setSize(170, 20);
        start_preview_on_left_click_label.setFont(new Font("Arial", Font.BOLD, 11));
        add(start_preview_on_left_click_label);
        start_preview_on_left_click_check_box = new JCheckBox();
        start_preview_on_left_click_check_box.setLocation(178, 150);
        start_preview_on_left_click_check_box.setSize(20, 20);
        add(start_preview_on_left_click_check_box);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode entity_uuid_node = node.getNode("ENTITY_UUID");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (!entity_uuid_node.isVirtual()) {
                entity_uuid_field.setText(entity_uuid_node.getString());
            }
            if (!start_preview_on_left_click_node.isVirtual()) {
                start_preview_on_left_click_check_box.setSelected(start_preview_on_left_click_node.getBoolean());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Entity Case Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode entity_uuid_node = node.getNode("ENTITY_UUID");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            entity_uuid_node.setValue(entity_uuid_field.hasText() ? entity_uuid_field.getText() : null);
            start_preview_on_left_click_node.setValue(start_preview_on_left_click_check_box.isSelected() ? true : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving  Entity Case Configuration Dialog!", e);
        }
    }
}
