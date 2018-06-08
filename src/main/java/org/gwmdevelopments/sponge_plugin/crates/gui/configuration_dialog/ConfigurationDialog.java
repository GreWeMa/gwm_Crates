package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.gui.AdvancedTextField;
import org.gwmdevelopments.sponge_plugin.crates.gui.GWMCratesGUI;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class ConfigurationDialog extends JDialog {

    protected SuperObjectType super_object_type;
    protected String type;
    private JLabel id_label;
    private AdvancedTextField id_field;
    private ConfigurationNode node;

    public ConfigurationDialog(SuperObjectType super_object_type, String type, ConfigurationNode node) {
        super(GWMCratesGUI.getInstance(), "GWMCrates GUI v" + GWMCrates.VERSION + " Super Object \"" + super_object_type + "\" type \"" + type + "\" configuration!", true);
        this.super_object_type = super_object_type;
        this.type = type;
        this.node = node;
        setBounds(GWMCratesGUI.getInstance().getBounds());
        setLayout(null);
        setResizable(false);
        id_label = new JLabel("Super Object ID");
        id_label.setSize(190, 20);
        id_label.setLocation(8, 10);
        id_field = new AdvancedTextField("Enter id here...");
        id_field.setSize(190, 20);
        id_field.setLocation(8, 30);
        add(id_label);
        add(id_field);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(ConfigurationDialog.this, "Save configured settings?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result != JOptionPane.CANCEL_OPTION) {
                    close(result == JOptionPane.YES_OPTION);
                }
            }
        });
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode id_node = node.getNode("ID");
            if (!id_node.isVirtual()) {
                id_field.setText(id_node.getString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Configuration Dialog!", e);
        }
    }

    public void save() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode id_node = node.getNode("ID");
            id_node.setValue(id_field.hasText() ? id_field.getText() : null);
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Configuration Dialog!", e);
        }
    }

    public final void close(boolean save) {
        if (save) {
            save();
        }
        setVisible(false);
    }

    public ConfigurationNode getNode() {
        return node;
    }
}
