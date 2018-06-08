package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;

public class PermissionLevelsPanel extends JPanel {

    private DeleteButton delete_button;
    private AdvancedTextField permission_field;
    private JSpinner level_spinner;

    public PermissionLevelsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        delete_button = new DeleteButton();
        add(delete_button);
        permission_field = new AdvancedTextField("Enter permission here...");
        permission_field.setPreferredSize(new Dimension(110, 20));
        permission_field.setMaximumSize(new Dimension(110, 20));
        add(permission_field);
        level_spinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        level_spinner.setPreferredSize(new Dimension(40, 20));
        level_spinner.setMaximumSize(new Dimension(40, 20));
        add(level_spinner);
    }

    public DeleteButton getDeleteButton() {
        return delete_button;
    }

    public AdvancedTextField getPermissionField() {
        return permission_field;
    }

    public JSpinner getLevelSpinner() {
        return level_spinner;
    }
}
