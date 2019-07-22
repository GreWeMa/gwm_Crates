package org.gwmdevelopments.sponge_plugin.crates.exception;

public class ManagerCreationException extends RuntimeException {

    public static final String MESSAGE = "Failed to create Manager!";

    public ManagerCreationException(Throwable throwable) {
        super(MESSAGE, throwable);
    }
}
