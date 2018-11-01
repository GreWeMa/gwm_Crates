package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AddButton;
import org.gwmdevelopments.sponge_plugin.crates.gui.FlatSuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.KeyConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MultiKeyConfigurationDialog extends KeyConfigurationDialog {

    private JLabel keys_label;
    private AddButton add_key_button;
    private JPanel keys_panel;
    private JScrollPane keys_scroll_pane;
    private JLabel all_keys_needed_label;
    private JCheckBox all_keys_needed_checkbox;

    private ArrayList<FlatSuperObjectPanel> keys = new ArrayList<>();

    public MultiKeyConfigurationDialog(ConfigurationNode node) {
        super("MULTI", node);
        keys_label = new JLabel("Keys");
        keys_label.setLocation(8, 110);
        keys_label.setSize(170, 20);
        add(keys_label);
        add_key_button = new AddButton();
        add_key_button.setLocation(178, 110);
        add_key_button.addActionListener(e -> addKey());
        add(add_key_button);
        keys_panel = new JPanel();
        keys_panel.setLayout(new BoxLayout(keys_panel, BoxLayout.Y_AXIS));
        keys_scroll_pane = new JScrollPane(keys_panel);
        keys_scroll_pane.setLocation(8, 130);
        keys_scroll_pane.setSize(190, 100);
        add(keys_scroll_pane);
        all_keys_needed_label = new JLabel("All keys needed");
        all_keys_needed_label.setLocation(8, 240);
        all_keys_needed_label.setSize(170, 20);
        add(all_keys_needed_label);
        all_keys_needed_checkbox = new JCheckBox();
        all_keys_needed_checkbox.setLocation(178, 240);
        all_keys_needed_checkbox.setSize(20, 20);
        all_keys_needed_checkbox.setSelected(true);
        add(all_keys_needed_checkbox);
        load();
    }

    public FlatSuperObjectPanel addKey() {
        FlatSuperObjectPanel key = new FlatSuperObjectPanel(false, SuperObjectType.KEY, GUIConstants.KEY_TYPES);
        key.getDeleteButton().addActionListener(e -> {
            if (key.getType() != null) {
                key.clear();
            } else {
                removeKey(key);
            }
        });
        keys_panel.add(key);
        keys.add(key);
        keys_panel.revalidate();
        return key;
    }

    public void removeKey(FlatSuperObjectPanel key) {
        keys_panel.remove(key);
        keys.remove(key);
        keys_panel.revalidate();
        keys_panel.repaint();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode keys_node = node.getNode("KEYS");
            ConfigurationNode all_keys_needed_node = node.getNode("ALL_KEYS_NEEDED");
            keys_node.getChildrenList().forEach(key_node -> addKey().setNode(key_node));
            all_keys_needed_checkbox.setSelected(all_keys_needed_node.getBoolean());
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Multi Key Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode keys_node = node.getNode("KEYS");
            ConfigurationNode all_keys_needed_node = node.getNode("ALL_KEYS_NEEDED");
            keys_node.setValue(keys.stream().
                    filter(key -> key.getType() != null).
                    map(FlatSuperObjectPanel::getNode).
                    collect(Collectors.toList()));
            all_keys_needed_node.setValue(all_keys_needed_checkbox.isSelected());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Multi Key Configuration Dialog!", e);
        }
    }
}
