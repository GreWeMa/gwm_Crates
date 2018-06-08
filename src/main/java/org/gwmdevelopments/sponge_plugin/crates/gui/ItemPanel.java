package org.gwmdevelopments.sponge_plugin.crates.gui;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPanel extends JPanel {

    private JLabel item_type_label;
    private AdvancedComboBox item_type_combo_box;
    private JLabel quantity_label;
    private AdvancedTextField quantity_field;
    private JLabel sub_id_label;
    private AdvancedTextField sub_id_field;
    private JLabel nbt_label;
    private AdvancedTextArea nbt_text_area;
    private JScrollPane nbt_scroll_pane;
    private JLabel durability_label;
    private AdvancedTextField durability_field;
    private JLabel display_name_label;
    private AdvancedTextField display_name_field;
    private JLabel lore_label;
    private AdvancedTextArea lore_text_area;
    private JScrollPane lore_scroll_pane;
    private JLabel enchantments_label;
    private AddButton add_enchantment_button;
    private JPanel enchantments_panel;
    private JScrollPane enchantments_scroll_pane;
    private JLabel hide_enchantments_label;
    private JCheckBox hide_enchantments_check_box;

    private ArrayList<GUIEnchantment> enchantments = new ArrayList<GUIEnchantment>();

    public ItemPanel() {
        Dimension size = new Dimension(190, 530);
        setSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        setPreferredSize(size);
        setLayout(null);
        setBorder(BorderFactory.createDashedBorder(Color.DARK_GRAY));
        item_type_label = new JLabel("Item type");
        item_type_label.setLocation(5, 8);
        item_type_label.setSize(180, 20);
        add(item_type_label);
        item_type_combo_box = new AdvancedComboBox(GWMCratesUtils.getItemTypes(true));
        item_type_combo_box.setLocation(5, 28);
        item_type_combo_box.setSize(180, 20);
        add(item_type_combo_box);
        quantity_label = new JLabel("Quantity");
        quantity_label.setLocation(5, 58);
        quantity_label.setSize(180, 20);
        add(quantity_label);
        quantity_field = new AdvancedTextField("Enter quantity here...");
        quantity_field.setLocation(5, 78);
        quantity_field.setSize(180, 20);
        quantity_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(quantity_field);
        sub_id_label = new JLabel("Sub ID");
        sub_id_label.setLocation(5, 108);
        sub_id_label.setSize(180, 20);
        add(sub_id_label);
        sub_id_field = new AdvancedTextField("Enter sub id here...");
        sub_id_field.setLocation(5, 128);
        sub_id_field.setSize(180, 20);
        sub_id_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(sub_id_field);
        nbt_label = new JLabel("NBT");
        nbt_label.setLocation(5, 158);
        nbt_label.setSize(180, 20);
        nbt_label.setToolTipText("Only for advanced users!");
        add(nbt_label);
        nbt_text_area = new AdvancedTextArea("Enter NBT here...");
        nbt_text_area.setEnabled(false);
        nbt_text_area.setToolTipText("Currently UNSUPPORTED");
        nbt_scroll_pane = new JScrollPane(nbt_text_area);
        nbt_scroll_pane.setLocation(5, 178);
        nbt_scroll_pane.setSize(180, 50);
        add(nbt_scroll_pane);
        durability_label = new JLabel("Durability");
        durability_label.setLocation(5, 238);
        durability_label.setSize(180, 20);
        add(durability_label);
        durability_field = new AdvancedTextField("Enter durability here...");
        durability_field.setLocation(5, 258);
        durability_field.setSize(180, 20);
        durability_field.setFunction(Optional.of(new CheckIntegerFunction()));
        add(durability_field);
        display_name_label = new JLabel("Display name");
        display_name_label.setLocation(5, 288);
        display_name_label.setSize(180, 20);
        add(display_name_label);
        display_name_field = new AdvancedTextField("Enter display name here...");
        display_name_field.setLocation(5, 308);
        display_name_field.setSize(180, 20);
        add(display_name_field);
        lore_label = new JLabel("Lore");
        lore_label.setLocation(5, 338);
        lore_label.setSize(180, 20);
        add(lore_label);
        lore_text_area = new AdvancedTextArea("Enter lore here...");
        lore_scroll_pane = new JScrollPane(lore_text_area);
        lore_scroll_pane.setLocation(5, 358);
        lore_scroll_pane.setSize(180, 50);
        add(lore_scroll_pane);
        enchantments_label = new JLabel("Enchantments");
        enchantments_label.setLocation(5, 418);
        enchantments_label.setSize(160, 20);
        add(enchantments_label);
        add_enchantment_button = new AddButton();
        add_enchantment_button.setLocation(165, 418);
        add_enchantment_button.addActionListener(e -> {
            GUIEnchantment enchantment = new GUIEnchantment();
            enchantment.getDeleteButton().addActionListener(e2 -> {
                enchantments_panel.remove(enchantment);
                enchantments.remove(enchantment);
                enchantments_panel.revalidate();
                enchantments_panel.repaint();
            });
            enchantments.add(enchantment);
            enchantments_panel.add(enchantment);
            enchantments_panel.revalidate();
            enchantments_panel.repaint();
        });
        add(add_enchantment_button);
        enchantments_panel = new JPanel();
        enchantments_panel.setLayout(new BoxLayout(enchantments_panel, BoxLayout.Y_AXIS));
        enchantments_scroll_pane = new JScrollPane(enchantments_panel);
        enchantments_scroll_pane.setLocation(5, 438);
        enchantments_scroll_pane.setSize(180, 60);
        add(enchantments_scroll_pane);
        hide_enchantments_label = new JLabel("Hide enchantments?");
        hide_enchantments_label.setLocation(5, 508);
        hide_enchantments_label.setSize(160, 20);
        add(hide_enchantments_label);
        hide_enchantments_check_box = new JCheckBox();
        hide_enchantments_check_box.setLocation(165, 508);
        hide_enchantments_check_box.setSize(20, 20);
        add(hide_enchantments_check_box);
    }

    public void load(ConfigurationNode node) {
        try {
            ConfigurationNode item_type_node = node.getNode("ITEM_TYPE");
            ConfigurationNode quantity_node = node.getNode("QUANTITY");
            ConfigurationNode sub_id_node = node.getNode("SUB_ID");
            ConfigurationNode nbt_node = node.getNode("NBT");
            ConfigurationNode durability_node = node.getNode("DURABILITY");
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode lore_node = node.getNode("LORE");
            ConfigurationNode enchantments_node = node.getNode("ENCHANTMENTS");
            ConfigurationNode hide_enchantments_node = node.getNode("HIDE_ENCHANTMENTS");
            item_type_combo_box.setSelectedItem(item_type_node.getString("NO ITEM"));
            if (!quantity_node.isVirtual()) {
                quantity_field.setText(String.valueOf(quantity_node.getInt()));
            }
            if (!sub_id_node.isVirtual()) {
                sub_id_field.setText(String.valueOf(sub_id_node.getInt()));
            }
            if (!durability_node.isVirtual()) {
                durability_field.setText(String.valueOf(durability_node.getInt()));
            }
            if (!display_name_node.isVirtual()) {
                display_name_field.setText(display_name_node.getString());
            }
            if (!lore_node.isVirtual()) {
                lore_text_area.setText(GWMCratesUtils.listToString(lore_node.getList(TypeToken.of(String.class))));
            }
            if (!enchantments_node.isVirtual()) {
                for (ConfigurationNode enchantment_node : enchantments_node.getChildrenList()) {
                    GUIEnchantment gui_enchantment = new GUIEnchantment();
                    gui_enchantment.getDeleteButton().addActionListener(e2 -> {
                        enchantments_panel.remove(gui_enchantment);
                        enchantments.remove(gui_enchantment);
                        enchantments_panel.revalidate();
                        enchantments_panel.repaint();
                    });
                    gui_enchantment.getEnchantmentComboBox().setSelectedItem(enchantment_node.getNode("ENCHANTMENT").getString());
                    gui_enchantment.getEnchantmentLevelSpinner().setValue(enchantment_node.getNode("LEVEL").getInt());
                    enchantments_panel.add(gui_enchantment);
                    enchantments.add(gui_enchantment);
                }
                enchantments_panel.revalidate();
                enchantments_panel.repaint();
            }
            if (!hide_enchantments_node.isVirtual()) {
                hide_enchantments_check_box.setSelected(hide_enchantments_node.getBoolean());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Item Panel!", e);
        }
    }

    public ConfigurationNode save() {
        try {
            ConfigurationNode node = SimpleConfigurationNode.root();
            ConfigurationNode item_type_node = node.getNode("ITEM_TYPE");
            ConfigurationNode quantity_node = node.getNode("QUANTITY");
            ConfigurationNode sub_id_node = node.getNode("SUB_ID");
            ConfigurationNode nbt_node = node.getNode("NBT");
            ConfigurationNode durability_node = node.getNode("DURABILITY");
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode lore_node = node.getNode("LORE");
            ConfigurationNode enchantments_node = node.getNode("ENCHANTMENTS");
            ConfigurationNode hide_enchantments_node = node.getNode("HIDE_ENCHANTMENTS");
            String item_type = item_type_combo_box.getSelectedText();
            item_type_node.setValue(item_type.equals("NO ITEM") ? null : item_type);
            quantity_node.setValue(quantity_field.hasText() ? quantity_field.getText() : null);
            sub_id_node.setValue(sub_id_field.hasText() ? sub_id_field.getText() : null);
            durability_node.setValue(durability_field.hasText() ? durability_field.getText() : null);
            display_name_node.setValue(display_name_field.hasText() ? display_name_field.getText() : null);
            lore_node.setValue(lore_text_area.hasText() ? GWMCratesUtils.stringToList(lore_text_area.getText()) : null);
            if (!enchantments.isEmpty()) {
                List<ConfigurationNode> enchantments_node_list = new ArrayList<ConfigurationNode>();
                for (GUIEnchantment enchantment : enchantments) {
                    ConfigurationNode enchantment_node = SimpleConfigurationNode.root();
                    enchantment_node.getNode("ENCHANTMENT").setValue(enchantment.getEnchantmentComboBox().getSelectedText());
                    enchantment_node.getNode("LEVEL").setValue(Integer.valueOf(enchantment.getEnchantmentLevelSpinner().getValue().toString()));
                    enchantments_node_list.add(enchantment_node);
                }
                enchantments_node.setValue(enchantments_node_list);
            } else {
                enchantments_node.setValue(null);
            }
            hide_enchantments_node.setValue(hide_enchantments_check_box.isSelected() ? true : null);
            return node;
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Item Panel!", e);
        }
    }
}
