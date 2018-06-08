package org.gwmdevelopments.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.util.function.Supplier;

public class AdvancedConfigurationButton extends ConfigurationButton {

    private SuperObjectType super_object_type;
    private Supplier<String> type_supplier;
    private Supplier<ConfigurationNode> node_supplier;

    public AdvancedConfigurationButton(SuperObjectType super_object_type, Supplier<String> type_supplier, Supplier<ConfigurationNode> node_supplier) {
        this.super_object_type = super_object_type;
        this.type_supplier = type_supplier;
        this.node_supplier = node_supplier;
        addActionListener(e -> {
            String type = this.type_supplier.get();
            if (type != null) {
                GWMCratesUtils.createGUIConfigurationDialog(this.super_object_type, type, this.node_supplier.get());
            } else {
                JOptionPane.showMessageDialog(null, "Super Object \"" + this.super_object_type.toString() + "\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
