package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.OpenManagerConfigurationDialog;

public class NoGuiOpenManagerConfigurationDialog extends OpenManagerConfigurationDialog {

    public NoGuiOpenManagerConfigurationDialog(ConfigurationNode node) {
        super("NO-GUI", node);
    }

    @Override
    public void save() {
        super.save();
    }
}
