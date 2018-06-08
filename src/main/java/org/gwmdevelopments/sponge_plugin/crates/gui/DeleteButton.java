package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;

public class DeleteButton extends JButton {

    public DeleteButton() {
        setToolTipText("Remove/Clear");
        setIcon(new ImageIcon(this.getClass().getResource("/remove.png")));
        setSize(20, 20);
        setPreferredSize(new Dimension(20, 20));
        setMaximumSize(new Dimension(20, 20));
    }
}
