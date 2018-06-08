package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedComboBox;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.ItemPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.OpenManagerConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class SecondOpenManagerConfigurationDialog extends OpenManagerConfigurationDialog {

    private JLabel display_name_label;
    private AdvancedTextField display_name_field;
    private JLabel hidden_item_label;
    private ItemPanel hidden_item_panel;
    private JLabel increase_hidden_item_quantity_label;
    private JCheckBox increase_hidden_item_quantity_check_box;
    private JLabel rows_label;
    private JSpinner rows_spinner;
    private JLabel show_other_drops_label;
    private JCheckBox show_other_drops_check_box;
    private JLabel show_other_drops_delay_label;
    private AdvancedTextField show_other_drops_delay_field;
    private JLabel close_delay_label;
    private AdvancedTextField close_delay_field;
    private JLabel forbid_close_label;
    private JCheckBox forbid_close_check_box;
    private JLabel give_random_on_close_label;
    private JCheckBox give_random_on_close_check_box;
    private JLabel click_sound_label;
    private AdvancedComboBox click_sound_combo_box;

    public SecondOpenManagerConfigurationDialog(ConfigurationNode node) {
        super("SECOND", node);
        display_name_label = new JLabel("Display name");
        display_name_label.setLocation(8, 110);
        display_name_label.setSize(190, 20);
        add(display_name_label);
        display_name_field = new AdvancedTextField("Enter display name here...");
        display_name_field.setLocation(8, 130);
        display_name_field.setSize(190, 20);
        add(display_name_field);
        hidden_item_label = new JLabel("Hidden item");
        hidden_item_label.setLocation(206, 10);
        hidden_item_label.setSize(190, 20);
        add(hidden_item_label);
        hidden_item_panel = new ItemPanel();
        hidden_item_panel.setLocation(206, 30);
        add(hidden_item_panel);
        increase_hidden_item_quantity_label = new JLabel("Increase hidden item quantity");
        increase_hidden_item_quantity_label.setLocation(8, 160);
        increase_hidden_item_quantity_label.setSize(170, 20);
        increase_hidden_item_quantity_label.setFont(new Font("Arial", Font.BOLD, 10));
        add(increase_hidden_item_quantity_label);
        increase_hidden_item_quantity_check_box = new JCheckBox();
        increase_hidden_item_quantity_check_box.setLocation(178, 160);
        increase_hidden_item_quantity_check_box.setSize(20, 20);
        add(increase_hidden_item_quantity_check_box);
        rows_label = new JLabel("Rows");
        rows_label.setLocation(8, 190);
        rows_label.setSize(190, 20);
        add(rows_label);
        rows_spinner = new JSpinner(new SpinnerNumberModel(6, 1, 6, 1));
        rows_spinner.setLocation(8, 210);
        rows_spinner.setSize(190, 20);
        add(rows_spinner);
        show_other_drops_label = new JLabel("Show other drops");
        show_other_drops_label.setLocation(8, 240);
        show_other_drops_label.setSize(170, 20);
        add(show_other_drops_label);
        show_other_drops_check_box = new JCheckBox();
        show_other_drops_check_box.setLocation(178, 240);
        show_other_drops_check_box.setSize(20, 20);
        add(show_other_drops_check_box);
        show_other_drops_delay_label = new JLabel("Show other drops delay");
        show_other_drops_delay_label.setLocation(8, 270);
        show_other_drops_delay_label.setSize(190, 20);
        add(show_other_drops_delay_label);
        show_other_drops_delay_field = new AdvancedTextField("Enter delay here...");
        show_other_drops_delay_field.setLocation(8, 290);
        show_other_drops_delay_field.setSize(190, 20);
        show_other_drops_delay_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(show_other_drops_delay_field);
        close_delay_label = new JLabel("Close delay");
        close_delay_label.setLocation(8, 320);
        close_delay_label.setSize(190, 20);
        add(close_delay_label);
        close_delay_field = new AdvancedTextField("Enter delay here...");
        close_delay_field.setLocation(8, 340);
        close_delay_field.setSize(190, 20);
        close_delay_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(close_delay_field);
        forbid_close_label = new JLabel("Forbid close");
        forbid_close_label.setLocation(8, 370);
        forbid_close_label.setSize(170, 20);
        add(forbid_close_label);
        forbid_close_check_box = new JCheckBox();
        forbid_close_check_box.setLocation(178, 370);
        forbid_close_check_box.setSize(20, 20);
        add(forbid_close_check_box);
        give_random_on_close_label = new JLabel("Give random on close");
        give_random_on_close_label.setLocation(8, 400);
        give_random_on_close_label.setSize(170, 20);
        add(give_random_on_close_label);
        give_random_on_close_check_box = new JCheckBox();
        give_random_on_close_check_box.setLocation(178, 400);
        give_random_on_close_check_box.setSize(20, 20);
        add(give_random_on_close_check_box);
        click_sound_label = new JLabel("Click sound");
        click_sound_label.setLocation(8, 430);
        click_sound_label.setSize(190, 20);
        add(click_sound_label);
        click_sound_combo_box = new AdvancedComboBox(GWMCratesUtils.getSoundTypes(true));
        click_sound_combo_box.setLocation(8, 450);
        click_sound_combo_box.setSize(190, 20);
        add(click_sound_combo_box);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode hidden_item_node = node.getNode("HIDDEN_ITEM");
            ConfigurationNode increase_hidden_item_quantity_node = node.getNode("INCREASE_HIDDEN_ITEM_QUANTITY");
            ConfigurationNode rows_node = node.getNode("ROWS");
            ConfigurationNode show_other_drops_node = node.getNode("SHOW_OTHER_DROPS");
            ConfigurationNode show_other_drops_delay_node = node.getNode("SHOW_OTHER_DROPS_DELAY");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbid_close_node = node.getNode("FORBID_CLOSE");
            ConfigurationNode give_random_on_close_node = node.getNode("GIVE_RANDOM_ON_CLOSE");
            ConfigurationNode click_sound_node = node.getNode("CLICK_SOUND");
            if (!display_name_node.isVirtual()) {
                display_name_field.setText(display_name_node.getString());
            }
            if (!hidden_item_node.isVirtual()) {
                hidden_item_panel.load(hidden_item_node);
            }
            if (!increase_hidden_item_quantity_node.isVirtual()) {
                increase_hidden_item_quantity_check_box.setSelected(increase_hidden_item_quantity_node.getBoolean());
            }
            if (!rows_node.isVirtual()) {
                rows_spinner.setValue(rows_node.getInt());
            }
            if (!show_other_drops_node.isVirtual()) {
                show_other_drops_check_box.setSelected(show_other_drops_node.getBoolean());
            }
            if (!show_other_drops_delay_node.isVirtual()) {
                show_other_drops_delay_field.setText(String.valueOf(show_other_drops_delay_node.getInt()));
            }
            if (!close_delay_node.isVirtual()) {
                close_delay_field.setText(String.valueOf(close_delay_node.getInt()));
            }
            if (!forbid_close_node.isVirtual()) {
                forbid_close_check_box.setSelected(forbid_close_node.getBoolean());
            }
            if (!give_random_on_close_node.isVirtual()) {
                give_random_on_close_check_box.setSelected(give_random_on_close_node.getBoolean());
            }
            if (!click_sound_node.isVirtual()) {
                click_sound_combo_box.setSelectedItem(click_sound_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Second Open Manager Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode hidden_item_node = node.getNode("HIDDEN_ITEM");
            ConfigurationNode increase_hidden_item_quantity_node = node.getNode("INCREASE_HIDDEN_ITEM_QUANTITY");
            ConfigurationNode rows_node = node.getNode("ROWS");
            ConfigurationNode show_other_drops_node = node.getNode("SHOW_OTHER_DROPS");
            ConfigurationNode show_other_drops_delay_node = node.getNode("SHOW_OTHER_DROPS_DELAY");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbid_close_node = node.getNode("FORBID_CLOSE");
            ConfigurationNode give_random_on_close_node = node.getNode("GIVE_RANDOM_ON_CLOSE");
            ConfigurationNode click_sound_node = node.getNode("CLICK_SOUND");
            display_name_node.setValue(display_name_field.hasText() ? display_name_field.getText() : null);
            hidden_item_node.setValue(hidden_item_panel.save());
            increase_hidden_item_quantity_node.setValue(increase_hidden_item_quantity_check_box.isSelected());
            rows_node.setValue(rows_spinner.getValue().toString());
            show_other_drops_node.setValue(show_other_drops_check_box.isSelected());
            show_other_drops_delay_node.setValue(show_other_drops_delay_field.hasText() ? show_other_drops_delay_field.getText() : null);
            close_delay_node.setValue(close_delay_field.hasText() ? close_delay_field.getText() : null);
            forbid_close_node.setValue(forbid_close_check_box.isSelected());
            give_random_on_close_node.setValue(give_random_on_close_check_box.isSelected());
            click_sound_node.setValue(click_sound_combo_box.getSelectedText().equals("NO SOUND") ? null : click_sound_combo_box.getSelectedText());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Second Open Manager Configuration Dialog!", e);
        }
    }
}
