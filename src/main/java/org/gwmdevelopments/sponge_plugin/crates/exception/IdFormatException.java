package org.gwmdevelopments.sponge_plugin.crates.exception;

public class IdFormatException extends RuntimeException {

    public static final String MESSAGE = "Wrong id format: \"%s\"! Id should contain only letters, digits, '_' and '-'. It should not start with a digit, and should not contain two or more '_' or '-' in a row!";

    private final String id;

    public IdFormatException(String id) {
        super(String.format(MESSAGE, id));
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
