package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.ItemPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;

import javax.swing.*;

public class ItemCaseConfigurationDialog extends CaseConfigurationDialog {

    private JLabel item_label;
    private ItemPanel item_configuration_panel;

    public ItemCaseConfigurationDialog(ConfigurationNode node) {
        super("ITEM", node);
        item_label = new JLabel("Item");
        item_label.setLocation(206, 10);
        item_label.setSize(190, 20);
        add(item_label);
        item_configuration_panel = new ItemPanel();
        item_configuration_panel.setLocation(206, 30);
        add(item_configuration_panel);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode item_node = node.getNode("ITEM");
            if (!item_node.isVirtual()) {
                item_configuration_panel.load(item_node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Item Case Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode item_node = node.getNode("ITEM");
            item_node.setValue(item_configuration_panel.save());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Item Case Configuration Dialog!", e);
        }
    }
}
