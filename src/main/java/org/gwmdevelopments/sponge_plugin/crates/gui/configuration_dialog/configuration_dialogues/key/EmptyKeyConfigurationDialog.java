package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.KeyConfigurationDialog;

public class EmptyKeyConfigurationDialog extends KeyConfigurationDialog {

    public EmptyKeyConfigurationDialog(ConfigurationNode node) {
        super("EMPTY", node);
    }

    @Override
    public void save() {
        super.save();
    }
}
