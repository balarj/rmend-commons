package com.brajagopal.rmend.exception;

/**
 * @author <bxr4261>
 */
public class UserNotFoundException extends Throwable {
    private final String uuid;

    public UserNotFoundException(String _uuid) {
        this.uuid = _uuid;
    }

    @Override
    public String toString() {
        return "UserNotFoundException{" +
                "uuid=" + uuid +
                '}';
    }

    @Override
    public String getMessage() {
        return "User with UUID: " + getUUID() + " was not found.";
    }

    public String getUUID() {
        return uuid;
    }
}
