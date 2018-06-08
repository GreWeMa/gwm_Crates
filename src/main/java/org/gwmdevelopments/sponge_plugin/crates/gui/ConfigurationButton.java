package org.gwmdevelopments.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;

public class ConfigurationButton extends JButton {

    public ConfigurationButton() {
        setSize(50, 20);
        setPreferredSize(new Dimension(50, 20));
        setMaximumSize(new Dimension(50, 20));
        setIcon(new ImageIcon(this.getClass().getResource("/configure.png")));
    }
}
