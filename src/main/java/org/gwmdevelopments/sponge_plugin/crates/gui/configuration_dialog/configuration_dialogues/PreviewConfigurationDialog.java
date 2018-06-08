package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues;

import ninja.leaping.configurate.ConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

public abstract class PreviewConfigurationDialog extends ConfigurationDialog {

    public PreviewConfigurationDialog(String type, ConfigurationNode node) {
        super(SuperObjectType.PREVIEW, type, node);
    }

    @Override
    public void save() {
        super.save();
    }
}
