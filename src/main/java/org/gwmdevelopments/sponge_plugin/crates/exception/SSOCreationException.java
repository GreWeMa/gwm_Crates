package org.gwmdevelopments.sponge_plugin.crates.exception;

public class SSOCreationException extends RuntimeException {

    public SSOCreationException() {
    }

    public SSOCreationException(String s) {
        super(s);
    }

    public SSOCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SSOCreationException(Throwable throwable) {
        super(throwable);
    }

    public SSOCreationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
