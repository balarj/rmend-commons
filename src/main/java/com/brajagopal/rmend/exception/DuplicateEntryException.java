package com.brajagopal.rmend.exception;

/**
 * @author <bxr4261>
 */
public class DuplicateEntryException extends Throwable {

    private final String identifier;

    public DuplicateEntryException(String _identifier) {
        this.identifier = _identifier;
    }

    @Override
    public String toString() {
        return "DuplicateEntryException{" +
                "identifier=" + identifier +
                '}';
    }

    @Override
    public String getMessage() {
        return "Identifier with ID: " + getIdentifier() + " already exists.";
    }

    public String getIdentifier() {
        return identifier;
    }
}
