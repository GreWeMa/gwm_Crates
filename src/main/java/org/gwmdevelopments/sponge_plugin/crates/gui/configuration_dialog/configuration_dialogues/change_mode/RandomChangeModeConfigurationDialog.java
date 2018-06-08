package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.ChangeModeConfigurationDialog;

public class RandomChangeModeConfigurationDialog extends ChangeModeConfigurationDialog {

    public RandomChangeModeConfigurationDialog(ConfigurationNode node) {
        super("RANDOM", node);
    }

    @Override
    public void save() {
        super.save();
    }
}
