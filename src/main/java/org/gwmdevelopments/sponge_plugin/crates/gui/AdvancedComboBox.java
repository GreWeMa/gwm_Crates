package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;

public class AdvancedComboBox extends JComboBox<String> {

    public AdvancedComboBox(String[] items) {
        super(items);
        setEditable(true);
    }

    public String getSelectedText() {
        return getSelectedItem().toString();
    }
}
