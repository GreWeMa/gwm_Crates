package org.gwmdevelopments.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckDoubleFunction;
import org.gwmdevelopments.sponge_plugin.crates.util.CheckIntegerFunction;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LocationPanel extends JPanel {

    private final boolean integer;
    private JLabel world_label;
    private AdvancedTextField world_field;
    private JLabel x_label;
    private AdvancedTextField x_field;
    private JLabel y_label;
    private AdvancedTextField y_field;
    private JLabel z_label;
    private AdvancedTextField z_field;

    public LocationPanel(boolean integer) {
        this.integer = integer;
        setLayout(null);
        setSize(190, 200);
        setBorder(BorderFactory.createDashedBorder(Color.DARK_GRAY));
        world_label = new JLabel("WORLD");
        world_label.setLocation(5, 0);
        world_label.setSize(190, 20);
        add(world_label);
        world_field = new AdvancedTextField("Enter world name here...");
        world_field.setLocation(5, 20);
        world_field.setSize(180, 20);
        add(world_field);
        x_label = new JLabel("X");
        x_label.setLocation(5, 50);
        x_label.setSize(180, 20);
        add(x_label);
        x_field = new AdvancedTextField("Enter X here...");
        x_field.setLocation(5, 70);
        x_field.setSize(180, 20);
        x_field.setFunction(Optional.of(integer ? new CheckIntegerFunction() : new CheckDoubleFunction()));
        add(x_field);
        y_label = new JLabel("Y");
        y_label.setLocation(5, 100);
        y_label.setSize(180, 20);
        add(y_label);
        y_field = new AdvancedTextField("Enter Y here...");
        y_field.setLocation(5, 120);
        y_field.setSize(180, 20);
        y_field.setFunction(Optional.of(integer ? new CheckIntegerFunction() : new CheckDoubleFunction()));
        add(y_field);
        z_label = new JLabel("Z");
        z_label.setLocation(5, 150);
        z_label.setSize(180, 20);
        add(z_label);
        z_field = new AdvancedTextField("Enter Z here...");
        z_field.setLocation(5, 170);
        z_field.setSize(180, 20);
        z_field.setFunction(Optional.of(integer ? new CheckIntegerFunction() : new CheckDoubleFunction()));
        add(z_field);
    }

    public void load(ConfigurationNode node) {
        try {
            ConfigurationNode world_node = node.getNode("WORLD_NAME");
            ConfigurationNode x_node = node.getNode("X");
            ConfigurationNode y_node = node.getNode("Y");
            ConfigurationNode z_node = node.getNode("Z");
            if (!world_node.isVirtual()) {
                world_field.setText(world_node.getString());
            }
            if (!x_node.isVirtual()) {
                x_field.setText(integer ? String.valueOf(x_node.getInt()) : String.valueOf(x_node.getDouble()));
            }
            if (!y_node.isVirtual()) {
                y_field.setText(integer ? String.valueOf(y_node.getInt()) : String.valueOf(y_node.getDouble()));
            }
            if (!z_node.isVirtual()) {
                z_field.setText(integer ? String.valueOf(z_node.getInt()) : String.valueOf(z_node.getDouble()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Location Panel!", e);
        }
    }

    public ConfigurationNode save() {
        try {
            ConfigurationNode node = SimpleConfigurationNode.root();
            ConfigurationNode world_node = node.getNode("WORLD_NAME");
            ConfigurationNode x_node = node.getNode("X");
            ConfigurationNode y_node = node.getNode("Y");
            ConfigurationNode z_node = node.getNode("Z");
            world_node.setValue(world_field.hasText() ? world_field.getText() : null);
            x_node.setValue(x_field.hasText() ? x_field.getText() : null);
            y_node.setValue(y_field.hasText() ? y_field.getText() : null);
            z_node.setValue(z_field.hasText() ? z_field.getText() : null);
            return node;
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Location Panel!", e);
        }
    }
}
