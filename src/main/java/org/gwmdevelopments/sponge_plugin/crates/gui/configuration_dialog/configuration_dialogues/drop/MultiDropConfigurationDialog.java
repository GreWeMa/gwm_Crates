package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.AddButton;
import org.gwmdevelopments.sponge_plugin.crates.gui.GUIConstants;
import org.gwmdevelopments.sponge_plugin.crates.gui.FlatSuperObjectPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.DropConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MultiDropConfigurationDialog extends DropConfigurationDialog {

    private JLabel drops_label;
    private AddButton add_drop_button;
    private JPanel drops_panel;
    private JScrollPane drops_scroll_pane;
    private JLabel give_all_label;
    private JCheckBox give_all_check_box;

    private ArrayList<FlatSuperObjectPanel> drops = new ArrayList<FlatSuperObjectPanel>();

    public MultiDropConfigurationDialog(ConfigurationNode node) {
        super("MULTI", node);
        drops_label = new JLabel("Drops");
        drops_label.setLocation(404, 10);
        drops_label.setSize(170, 20);
        add(drops_label);
        add_drop_button = new AddButton();
        add_drop_button.setLocation(574, 10);
        add_drop_button.addActionListener(e -> addDrop());
        add(add_drop_button);
        drops_panel = new JPanel();
        drops_panel.setLayout(new BoxLayout(drops_panel, BoxLayout.Y_AXIS));
        drops_scroll_pane = new JScrollPane(drops_panel);
        drops_scroll_pane.setLocation(404, 30);
        drops_scroll_pane.setSize(190, 100);
        add(drops_scroll_pane);
        give_all_label = new JLabel("Give All");
        give_all_label.setLocation(404, 140);
        give_all_label.setSize(170, 20);
        add(give_all_label);
        give_all_check_box = new JCheckBox();
        give_all_check_box.setLocation(574, 140);
        give_all_check_box.setSize(20, 20);
        give_all_check_box.setSelected(true);
        add(give_all_check_box);
        load();
    }

    public FlatSuperObjectPanel addDrop() {
        FlatSuperObjectPanel drop = new FlatSuperObjectPanel(false, SuperObjectType.DROP, GUIConstants.DROP_TYPES);
        drop.getDeleteButton().addActionListener(e -> {
            if (drop.getType() != null) {
                drop.clear();
            } else {
                removeDrop(drop);
            }
        });
        drops_panel.add(drop);
        drops.add(drop);
        drops_panel.revalidate();
        return drop;
    }

    public void removeDrop(FlatSuperObjectPanel drop) {
        drops_panel.remove(drop);
        drops.remove(drop);
        drops_panel.revalidate();
        drops_panel.repaint();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode drops_node = node.getNode("DROPS");
            ConfigurationNode give_all_node = node.getNode("GIVE_ALL");
            drops_node.getChildrenList().forEach(drop_node -> addDrop().setNode(drop_node));
            if (!give_all_node.isVirtual()) {
                give_all_check_box.setSelected(give_all_node.getBoolean());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Multi Drop Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode drops_node = node.getNode("DROPS");
            ConfigurationNode give_all_node = node.getNode("GIVE_ALL");
            drops_node.setValue(drops.stream().
                    filter(drop -> drop.getType() != null).
                    map(FlatSuperObjectPanel::getNode).
                    collect(Collectors.toList()));
            give_all_node.setValue(give_all_check_box.isSelected());
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Multi Drop Configuration Dialog!", e);
        }
    }
}
