package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.HashMap;

public abstract class SuperObjectType {

    private final String name;

    public SuperObjectType(String name) {
        this.name = name;
    }

    public static final SuperObjectType CASE = new SuperObjectType("CASE"){};
    public static final SuperObjectType KEY = new SuperObjectType("KEY"){};
    public static final SuperObjectType DROP = new SuperObjectType("DROP"){};
    public static final SuperObjectType OPEN_MANAGER = new SuperObjectType("OPEN_MANAGER"){};
    public static final SuperObjectType PREVIEW = new SuperObjectType("PREVIEW"){};
    public static final SuperObjectType DECORATIVE_ITEMS_CHANGE_MODE =
            new SuperObjectType("DECORATIVE_ITEMS_CHANGE_MODE"){};

    public static final HashMap<String, SuperObjectType> SUPER_OBJECT_TYPES =
            new HashMap<String, SuperObjectType>(){{
                put(CASE.name, CASE);
                put(KEY.name, KEY);
                put(DROP.name, DROP);
                put(OPEN_MANAGER.name, OPEN_MANAGER);
                put(PREVIEW.name, PREVIEW);
                put(DECORATIVE_ITEMS_CHANGE_MODE.name, DECORATIVE_ITEMS_CHANGE_MODE);
            }};

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuperObjectType)) {
            return false;
        }
        SuperObjectType type = (SuperObjectType) o;
        return type.name.equals(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
