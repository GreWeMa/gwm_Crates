package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.DropConfigurationDialog;

public class EmptyDropConfigurationDialog extends DropConfigurationDialog {

    public EmptyDropConfigurationDialog(ConfigurationNode node) {
        super("EMPTY", node);
    }

    @Override
    public void save() {
        super.save();
    }
}
