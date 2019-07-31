package org.gwmdevelopments.sponge_plugin.crates.event;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObject;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectStorage;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.HashSet;
import java.util.Set;

public class GWMCratesRegistrationEvent extends AbstractEvent {

    private final HashSet<SuperObjectStorage> superObjectStorage =
            new HashSet<>();

    public void register(SuperObjectType superObjectType, String type, Class<? extends SuperObject> superObjectClass) {
        for (SuperObjectStorage storage : superObjectStorage) {
            if (storage.getType().equals(type) &&
                    storage.getSuperObjectType().equals(superObjectType)) {
                throw new IllegalArgumentException("Super Object with Type \"" + type + "\" and Super Object Type \"" + superObjectType + "\" already registered!");
            } else if (storage.getSuperObjectClass().equals(superObjectClass)) {
                throw new IllegalArgumentException("Super Object class \"" + superObjectClass.getName() + "\" already registered!");
            }
        }
        superObjectStorage.add(new SuperObjectStorage(superObjectType, type, superObjectClass));
    }

    public void unregister(SuperObjectType superObjectType, String type) {
        superObjectStorage.removeIf(storage ->
                storage.getSuperObjectType().equals(superObjectType) &&
                        storage.getType().equals(type));
    }

    public void unregister(Class<? extends SuperObject> clazz) {
        superObjectStorage.removeIf(storage ->
            storage.getSuperObjectClass().equals(clazz));
    }

    @Override
    public Cause getCause() {
        return GWMCrates.getInstance().getCause();
    }

    public Set<SuperObjectStorage> getSuperObjectStorage() {
        return superObjectStorage;
    }
}