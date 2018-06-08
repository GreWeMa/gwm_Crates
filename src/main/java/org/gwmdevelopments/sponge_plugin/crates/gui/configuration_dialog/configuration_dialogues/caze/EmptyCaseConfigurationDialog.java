package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.CaseConfigurationDialog;

public class EmptyCaseConfigurationDialog extends CaseConfigurationDialog {

    public EmptyCaseConfigurationDialog(ConfigurationNode node) {
        super("EMPTY", node);
    }

    @Override
    public void save() {
        super.save();
    }
}
