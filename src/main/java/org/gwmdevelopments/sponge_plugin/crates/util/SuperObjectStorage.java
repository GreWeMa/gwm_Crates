package org.gwmdevelopments.sponge_plugin.crates.util;


import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;

import java.util.Optional;

public class SuperObjectStorage {

    private final SuperObjectType superObjectType;
    private final String type;
    private final Class<? extends SuperObject> superObjectClass;
    private final Optional<Class<? extends ConfigurationDialog>> configurationDialog;

    public SuperObjectStorage(SuperObjectType superObjectType, String type,
                              Class<? extends SuperObject> superObjectClass,
                              Optional<Class<? extends ConfigurationDialog>> configurationDialog) {
        this.superObjectType = superObjectType;
        this.type = type;
        this.superObjectClass = superObjectClass;
        this.configurationDialog = configurationDialog;
    }

    public SuperObjectType getSuperObjectType() {
        return superObjectType;
    }

    public String getType() {
        return type;
    }

    public Class<? extends SuperObject> getSuperObjectClass() {
        return superObjectClass;
    }

    public Optional<Class<? extends ConfigurationDialog>> getConfigurationDialog() {
        return configurationDialog;
    }
}
