package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.Optional;

public interface SuperObject {

    String getType();

    void setType(String type);

    Optional<String> getId();

    void setId(Optional<String> id);
}
