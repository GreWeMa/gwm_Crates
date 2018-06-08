package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckDoubleFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AddButton;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.ItemPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.PermissionLevelsPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckDoubleFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.*;

public abstract class DropConfigurationDialog extends ConfigurationDialog {

    private JLabel price_label;
    private AdvancedTextField price_field;
    private JLabel level_label;
    private JSpinner level_spinner;
    private JLabel fake_level_label;
    private JSpinner fake_level_spinner;
    private JLabel permission_levels_label;
    private JPanel permission_levels_panel;
    private JScrollPane permission_levels_scroll_pane;
    private AddButton add_permission_level_button;
    private JLabel permission_fake_levels_label;
    private JPanel permission_fake_levels_panel;
    private JScrollPane permission_fake_levels_scroll_pane;
    private AddButton add_permission_fake_level_button;
    private JLabel drop_item_label;
    private ItemPanel item_configuration_panel;

    private List<PermissionLevelsPanel> permission_levels = new ArrayList<PermissionLevelsPanel>();
    private List<PermissionLevelsPanel> permission_fake_levels = new ArrayList<PermissionLevelsPanel>();

    public DropConfigurationDialog(String type, ConfigurationNode node) {
        super(SuperObjectType.DROP, type, node);
        price_label = new JLabel("Price");
        price_label.setLocation(8, 60);
        price_label.setSize(190, 20);
        add(price_label);
        price_field = new AdvancedTextField("Enter price here...");
        price_field.setLocation(8, 80);
        price_field.setSize(190, 20);
        price_field.setToolTipText("Fractional number. Optional.");
        price_field.setFunction(Optional.of(new CheckDoubleFunction()));
        add(price_field);
        level_label = new JLabel("Level");
        level_label.setLocation(8, 110);
        level_label.setSize(190, 20);
        add(level_label);
        level_spinner = new JSpinner(new SpinnerNumberModel(1, 1, 30,1));
        level_spinner.setLocation(8, 130);
        level_spinner.setSize(190, 20);
        add(level_spinner);
        fake_level_label = new JLabel("Fake level");
        fake_level_label.setLocation(8, 160);
        fake_level_label.setSize(190, 20);
        add(fake_level_label);
        fake_level_spinner = new JSpinner(new SpinnerNumberModel(0, 0, 30,1));
        fake_level_spinner.setLocation(8, 180);
        fake_level_spinner.setSize(190, 20);
        add(fake_level_spinner);
        permission_levels_label = new JLabel("Permission levels");
        permission_levels_label.setLocation(8, 210);
        permission_levels_label.setSize(170, 20);
        add(permission_levels_label);
        permission_levels_panel = new JPanel();
        permission_levels_panel.setLayout(new BoxLayout(permission_levels_panel, BoxLayout.Y_AXIS));
        permission_levels_scroll_pane = new JScrollPane(permission_levels_panel);
        permission_levels_scroll_pane.setLocation(8, 230);
        permission_levels_scroll_pane.setSize(190, 100);
        add(permission_levels_scroll_pane);
        add_permission_level_button = new AddButton();
        add_permission_level_button.setLocation(178, 210);
        add_permission_level_button.addActionListener(e -> {
            PermissionLevelsPanel panel = new PermissionLevelsPanel();
            panel.getDeleteButton().addActionListener(e2 -> {
                permission_levels_panel.remove(panel);
                permission_levels.remove(panel);
                permission_levels_panel.revalidate();
                permission_levels_panel.repaint();
            });
            permission_levels_panel.add(panel);
            permission_levels.add(panel);
            permission_levels_panel.revalidate();
            permission_levels_panel.repaint();
        });
        add(add_permission_level_button);
        permission_fake_levels_label = new JLabel("Permission fake levels");
        permission_fake_levels_label.setLocation(8, 340);
        permission_fake_levels_label.setSize(170, 20);
        add(permission_fake_levels_label);
        permission_fake_levels_panel = new JPanel();
        permission_fake_levels_panel.setLayout(new BoxLayout(permission_fake_levels_panel, BoxLayout.Y_AXIS));
        permission_fake_levels_scroll_pane = new JScrollPane(permission_fake_levels_panel);
        permission_fake_levels_scroll_pane.setLocation(8, 360);
        permission_fake_levels_scroll_pane.setSize(190, 100);
        add(permission_fake_levels_scroll_pane);
        add_permission_fake_level_button = new AddButton();
        add_permission_fake_level_button.setLocation(178, 340);
        add_permission_fake_level_button.addActionListener(e -> {
            PermissionLevelsPanel panel = new PermissionLevelsPanel();
            panel.getDeleteButton().addActionListener(e2 -> {
                permission_fake_levels_panel.remove(panel);
                permission_fake_levels.remove(panel);
                permission_fake_levels_panel.revalidate();
                permission_fake_levels_panel.repaint();
            });
            permission_fake_levels_panel.add(panel);
            permission_fake_levels.add(panel);
            permission_fake_levels_panel.revalidate();
            permission_fake_levels_panel.repaint();
        });
        add(add_permission_fake_level_button);
        drop_item_label = new JLabel("Drop item");
        drop_item_label.setLocation(208, 10);
        drop_item_label.setSize(190, 20);
        drop_item_label.setToolTipText("Item that will be displayed in GUI Open Managers.");
        add(drop_item_label);
        item_configuration_panel = new ItemPanel();
        item_configuration_panel.setLocation(206, 30);
        add(item_configuration_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode price_node = node.getNode("PRICE");
            ConfigurationNode level_node = node.getNode("LEVEL");
            ConfigurationNode fake_level_node = node.getNode("FAKE_LEVEL");
            ConfigurationNode drop_item_node = node.getNode("DROP_ITEM");
            ConfigurationNode permission_levels_node = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permission_fake_levels_node = node.getNode("PERMISSION_FAKE_LEVELS");
            if (!price_node.isVirtual()) {
                price_field.setText(price_node.getString());
            }
            if (!level_node.isVirtual()) {
                level_spinner.setValue(Integer.valueOf(level_node.getString()));
            }
            if (!fake_level_node.isVirtual()) {
                fake_level_spinner.setValue(Integer.valueOf(fake_level_node.getString()));
            }
            if (!drop_item_node.isVirtual()) {
                item_configuration_panel.load(drop_item_node);
            }
            if (!permission_levels_node.isVirtual()) {
                for (Map.Entry<String, Integer> entry : permission_levels_node.getValue(new TypeToken<Map<String, Integer>>(){}).entrySet()) {
                    PermissionLevelsPanel panel = new PermissionLevelsPanel();
                    panel.getDeleteButton().addActionListener(e2 -> {
                        permission_levels_panel.remove(panel);
                        permission_levels.remove(panel);
                        permission_levels_panel.revalidate();
                        permission_levels_panel.repaint();
                    });
                    permission_levels_panel.add(panel);
                    permission_levels.add(panel);
                    panel.getPermissionField().setText(entry.getKey());
                    panel.getLevelSpinner().setValue(entry.getValue());
                    permission_levels_panel.revalidate();
                    permission_levels_panel.repaint();
                }
            }
            if (!permission_fake_levels_node.isVirtual()) {
                for (Map.Entry<String, Integer> entry : permission_fake_levels_node.getValue(new TypeToken<Map<String, Integer>>(){}).entrySet()) {
                    PermissionLevelsPanel panel = new PermissionLevelsPanel();
                    panel.getDeleteButton().addActionListener(e2 -> {
                        permission_fake_levels_panel.remove(panel);
                        permission_fake_levels.remove(panel);
                        permission_fake_levels_panel.revalidate();
                        permission_fake_levels_panel.repaint();
                    });
                    permission_fake_levels_panel.add(panel);
                    permission_fake_levels.add(panel);
                    panel.getPermissionField().setText(entry.getKey());
                    panel.getLevelSpinner().setValue(entry.getValue());
                    permission_fake_levels_panel.revalidate();
                    permission_fake_levels_panel.repaint();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Drop Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode price_node = node.getNode("PRICE");
            ConfigurationNode level_node = node.getNode("LEVEL");
            ConfigurationNode fake_level_node = node.getNode("FAKE_LEVEL");
            ConfigurationNode drop_item_node = node.getNode("DROP_ITEM");
            ConfigurationNode permission_levels_node = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permission_fake_levels_node = node.getNode("PERMISSION_FAKE_LEVELS");
            price_node.setValue(price_field.hasText() ? Double.parseDouble(price_field.getText()) : null);
            level_node.setValue(Integer.parseInt(level_spinner.getValue().toString()));
            int fake_level = Integer.parseInt(fake_level_spinner.getValue().toString());
            fake_level_node.setValue(fake_level == 0 ? null : fake_level);
            drop_item_node.setValue(item_configuration_panel.save());
            if (permission_levels.isEmpty()) {
                permission_levels_node.setValue(null);
            } else {
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                for (PermissionLevelsPanel panel : permission_levels) {
                    if (panel.getPermissionField().hasText()) {
                        map.put(panel.getPermissionField().getText(), Integer.valueOf(panel.getLevelSpinner().getValue().toString()));
                    }
                }
                permission_levels_node.setValue(map);
            }
            if (permission_fake_levels.isEmpty()) {
                permission_fake_levels_node.setValue(null);
            } else {
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                for (PermissionLevelsPanel panel : permission_fake_levels) {
                    if (panel.getPermissionField().hasText()) {
                        map.put(panel.getPermissionField().getText(), Integer.valueOf(panel.getLevelSpinner().getValue().toString()));
                    }
                }
                permission_fake_levels_node.setValue(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Drop Configuration Panel!", e);
        }
    }
}
