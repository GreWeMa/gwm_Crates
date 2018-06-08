package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;

public class CommandPanel extends JPanel {

    private DeleteButton delete_button;
    private AdvancedTextField command_field;
    private JCheckBox console_check_box;

    public CommandPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        delete_button = new DeleteButton();
        add(delete_button);
        command_field = new AdvancedTextField("Enter command here...");
        command_field.setPreferredSize(new Dimension(130, 20));
        command_field.setMaximumSize(new Dimension(130, 20));
        add(command_field);
        console_check_box = new JCheckBox();
        console_check_box.setPreferredSize(new Dimension(20, 20));
        console_check_box.setMaximumSize(new Dimension(20, 20));
        console_check_box.setSelected(true);
        add(console_check_box);
    }

    public DeleteButton getDeleteButton() {
        return delete_button;
    }

    public AdvancedTextField getCommandField() {
        return command_field;
    }

    public JCheckBox getConsoleCheckBox() {
        return console_check_box;
    }
}
