package org.gwmdevelopments.sponge_plugin.crates.exception;

public class ManagerCreationException extends RuntimeException {

    public ManagerCreationException() {
    }

    public ManagerCreationException(String s) {
        super(s);
    }

    public ManagerCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ManagerCreationException(Throwable throwable) {
        super(throwable);
    }

    public ManagerCreationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
