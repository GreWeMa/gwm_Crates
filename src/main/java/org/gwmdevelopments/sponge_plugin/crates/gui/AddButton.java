package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;

public class AddButton extends JButton{

    public AddButton() {
        setToolTipText("Add");
        setIcon(new ImageIcon(this.getClass().getResource("/add.png")));
        setSize(20, 20);
        setPreferredSize(new Dimension(20, 20));
        setMaximumSize(new Dimension(20, 20));
    }
}
