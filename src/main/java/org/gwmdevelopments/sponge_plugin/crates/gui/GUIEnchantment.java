package org.gwmdevelopments.sponge_plugin.crates.gui;

import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;
import org.gwmdevelopments.sponge_plugin.crates.util.GWMCratesUtils;

import javax.swing.*;
import java.awt.*;

public class GUIEnchantment extends JPanel {

    private DeleteButton delete_button;
    private AdvancedComboBox enchantment_combo_box;
    private JSpinner enchantment_level_spinner;

    public GUIEnchantment() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        delete_button = new DeleteButton();
        add(delete_button);
        enchantment_combo_box = new AdvancedComboBox(GWMCratesUtils.getEnchantments(false));
        enchantment_combo_box.setPreferredSize(new Dimension(100, 20));
        enchantment_combo_box.setMaximumSize(new Dimension(100, 20));
        add(enchantment_combo_box);
        enchantment_level_spinner = new JSpinner(new SpinnerNumberModel(1, 1, Short.MAX_VALUE, 1));
        enchantment_level_spinner.setPreferredSize(new Dimension(40, 20));
        enchantment_level_spinner.setMaximumSize(new Dimension(40, 20));
        add(enchantment_level_spinner);
    }

    public DeleteButton getDeleteButton() {
        return delete_button;
    }

    public AdvancedComboBox getEnchantmentComboBox() {
        return enchantment_combo_box;
    }

    public JSpinner getEnchantmentLevelSpinner() {
        return enchantment_level_spinner;
    }
}
