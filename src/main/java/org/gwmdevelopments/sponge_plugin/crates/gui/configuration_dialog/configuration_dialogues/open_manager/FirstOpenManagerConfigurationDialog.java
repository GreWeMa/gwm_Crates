package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntListFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.*;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.OpenManagerConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntListFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.util.Optional;

public class FirstOpenManagerConfigurationDialog extends OpenManagerConfigurationDialog {

    private JLabel display_name_label;
    private AdvancedTextField display_name_field;
    private JLabel decorative_items_label;
    private ConfigurationButton configure_decorative_items_button;
    private JLabel scroll_delays_label;
    private AdvancedTextField scroll_delays_field;
    private JLabel clear_decorative_items_label;
    private JCheckBox clear_decorative_items_check_box;
    private JLabel clear_other_drops_label;
    private JCheckBox clear_other_drops_check_box;
    private JLabel close_delay_label;
    private AdvancedTextField close_delay_field;
    private JLabel forbid_close_label;
    private JCheckBox forbid_close_check_box;
    private JLabel scroll_sound_label;
    private AdvancedComboBox scroll_sound_combo_box;
    private JLabel win_sound_label;
    private AdvancedComboBox win_sound_combo_box;
    private SuperObjectPanel decorative_items_change_mode_panel;

    public FirstOpenManagerConfigurationDialog(ConfigurationNode node) {
        super("FIRST", node);
        display_name_label = new JLabel("Display name");
        display_name_label.setLocation(8, 110);
        display_name_label.setSize(190, 20);
        add(display_name_label);
        display_name_field = new AdvancedTextField("Enter display name here...");
        display_name_field.setLocation(8, 130);
        display_name_field.setSize(190, 20);
        add(display_name_field);
        decorative_items_label = new JLabel("Decorative items");
        decorative_items_label.setLocation(8, 160);
        decorative_items_label.setSize(140, 20);
        add(decorative_items_label);
        configure_decorative_items_button = new ConfigurationButton();
        configure_decorative_items_button.setLocation(148, 160);
        configure_decorative_items_button.setToolTipText("Currently UNSUPPORTED");
        add(configure_decorative_items_button);
        scroll_delays_label = new JLabel("Scroll delays");
        scroll_delays_label.setLocation(8, 190);
        scroll_delays_label.setSize(190, 20);
        add(scroll_delays_label);
        scroll_delays_field = new AdvancedTextField("Enter scroll delays here...");
        scroll_delays_field.setLocation(8, 210);
        scroll_delays_field.setSize(190, 20);
        scroll_delays_field.setFunction(Optional.of(new CheckIntListFunction()));
        add(scroll_delays_field);
        clear_decorative_items_label = new JLabel("Clear decorative items");
        clear_decorative_items_label.setLocation(8, 240);
        clear_decorative_items_label.setSize(170, 20);
        add(clear_decorative_items_label);
        clear_decorative_items_check_box = new JCheckBox();
        clear_decorative_items_check_box.setLocation(178, 240);
        clear_decorative_items_check_box.setSize(20, 20);
        add(clear_decorative_items_check_box);
        clear_other_drops_label = new JLabel("Clear other drops");
        clear_other_drops_label.setLocation(8, 270);
        clear_other_drops_label.setSize(170, 20);
        add(clear_other_drops_label);
        clear_other_drops_check_box = new JCheckBox();
        clear_other_drops_check_box.setLocation(178, 270);
        clear_other_drops_check_box.setSize(20, 20);
        add(clear_other_drops_check_box);
        close_delay_label = new JLabel("Close delay");
        close_delay_label.setLocation(8, 300);
        close_delay_label.setSize(190, 20);
        add(close_delay_label);
        close_delay_field = new AdvancedTextField("Enter delay here...");
        close_delay_field.setLocation(8, 320);
        close_delay_field.setSize(190, 20);
        close_delay_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(close_delay_field);
        forbid_close_label = new JLabel("Forbid close");
        forbid_close_label.setLocation(8, 350);
        forbid_close_label.setSize(170, 20);
        add(forbid_close_label);
        forbid_close_check_box = new JCheckBox();
        forbid_close_check_box.setLocation(178, 350);
        forbid_close_check_box.setSize(20, 20);
        add(forbid_close_check_box);
        scroll_sound_label = new JLabel("Scroll sound");
        scroll_sound_label.setLocation(8, 380);
        scroll_sound_label.setSize(190, 20);
        add(scroll_sound_label);
        scroll_sound_combo_box = new AdvancedComboBox(GWMCratesUtils.getSoundTypes(true));
        scroll_sound_combo_box.setLocation(8, 400);
        scroll_sound_combo_box.setSize(190, 20);
        add(scroll_sound_combo_box);
        win_sound_label = new JLabel("Win sound");
        win_sound_label.setLocation(8, 430);
        win_sound_label.setSize(190, 20);
        add(win_sound_label);
        win_sound_combo_box = new AdvancedComboBox(GWMCratesUtils.getSoundTypes(true));
        win_sound_combo_box.setLocation(8, 450);
        win_sound_combo_box.setSize(190, 20);
        add(win_sound_combo_box);
        decorative_items_change_mode_panel = new SuperObjectPanel(false, "Change Mode", SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, GUIConstants.DECORATIVE_ITEMS_CHANGE_MODE_TYPES);
        decorative_items_change_mode_panel.setLocation(8, 480);
        add(decorative_items_change_mode_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorative_items_node = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scroll_delays_node = node.getNode("SCROLL_DELAYS");
            ConfigurationNode clear_decorative_items_node = node.getNode("CLEAR_DECORATIVE_ITEMS");
            ConfigurationNode clear_other_drops_node = node.getNode("CLEAR_OTHER_DROPS");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbid_close_node = node.getNode("FORBID_CLOSE");
            ConfigurationNode scroll_sound_node = node.getNode("SCROLL_SOUND");
            ConfigurationNode win_sound_node = node.getNode("WIN_SOUND");
            ConfigurationNode decorative_items_change_mode_node = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            if (!display_name_node.isVirtual()) {
                display_name_field.setText(display_name_node.getString());
            }
            if (!scroll_delays_node.isVirtual()) {
                scroll_delays_field.setText(GWMCratesUtils.intListToString(scroll_delays_node.getList(TypeToken.of(Integer.class))));
            }
            if (!clear_decorative_items_node.isVirtual()) {
                clear_decorative_items_check_box.setSelected(clear_decorative_items_node.getBoolean());
            }
            if (!clear_other_drops_node.isVirtual()) {
                clear_other_drops_check_box.setSelected(clear_other_drops_node.getBoolean());
            }
            if (!close_delay_node.isVirtual()) {
                close_delay_field.setText(String.valueOf(close_delay_node.getInt()));
            }
            if (!forbid_close_node.isVirtual()) {
                forbid_close_check_box.setSelected(forbid_close_node.getBoolean());
            }
            if (!scroll_sound_node.isVirtual()) {
                scroll_sound_combo_box.setSelectedItem(scroll_sound_node.getString());
            }
            if (!win_sound_node.isVirtual()) {
                win_sound_combo_box.setSelectedItem(win_sound_node.getString());
            }
            if (!decorative_items_change_mode_node.isVirtual()) {
                decorative_items_change_mode_panel.setNode(decorative_items_change_mode_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading First Open Manager Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorative_items_node = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scroll_delays_node = node.getNode("SCROLL_DELAYS");
            ConfigurationNode clear_decorative_items_node = node.getNode("CLEAR_DECORATIVE_ITEMS");
            ConfigurationNode clear_other_drops_node = node.getNode("CLEAR_OTHER_DROPS");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbid_close_node = node.getNode("FORBID_CLOSE");
            ConfigurationNode scroll_sound_node = node.getNode("SCROLL_SOUND");
            ConfigurationNode win_sound_node = node.getNode("WIN_SOUND");
            ConfigurationNode decorative_items_change_mode_node = node.getNode("DECORATIVE_ITEMS_CHANGE_MODE");
            display_name_node.setValue(display_name_field.hasText() ? display_name_field.getFocusTraversalKeysEnabled() : null);
            scroll_delays_node.setValue(scroll_delays_field.hasText() ? GWMCratesUtils.stringToIntList(scroll_delays_field.getText()) : null);
            clear_decorative_items_node.setValue(clear_decorative_items_check_box.isSelected());
            clear_other_drops_node.setValue(clear_other_drops_check_box.isSelected());
            close_delay_node.setValue(close_delay_field.hasText() ? close_delay_field.getText() : null);
            forbid_close_node.setValue(forbid_close_check_box.isSelected());
            scroll_sound_node.setValue(scroll_sound_combo_box.getSelectedText().equals("NO SOUND") ? null : scroll_sound_combo_box.getSelectedText());
            win_sound_node.setValue(win_sound_combo_box.getSelectedText().equals("NO SOUND") ? null : win_sound_combo_box.getSelectedText());
            decorative_items_change_mode_node.setValue(decorative_items_change_mode_panel.getNode());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving First Open Manager Configuration Dialog!", e);
        }
    }
}
