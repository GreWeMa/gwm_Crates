package org.gwmdevelopments.sponge_plugin.crates.util;

public class SuperObjectStorage {

    private final SuperObjectType superObjectType;
    private final String type;
    private final Class<? extends SuperObject> superObjectClass;

    public SuperObjectStorage(SuperObjectType superObjectType, String type,
                              Class<? extends SuperObject> superObjectClass) {
        this.superObjectType = superObjectType;
        this.type = type;
        this.superObjectClass = superObjectClass;
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
}
