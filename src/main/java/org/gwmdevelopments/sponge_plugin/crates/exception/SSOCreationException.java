package org.gwmdevelopments.sponge_plugin.crates.exception;

import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;

public class SSOCreationException extends RuntimeException {

    public static final String MESSAGE = "Failed to create SSO! SSO type: \"%s\", type: \"%s\"";

    public SSOCreationException(SuperObjectType ssoType, String type) {
        super(String.format(MESSAGE, ssoType.toString(), type));
    }

    public SSOCreationException(SuperObjectType ssoType, String type, Throwable throwable) {
        super(String.format(MESSAGE, ssoType.toString(), type), throwable);
    }
}
