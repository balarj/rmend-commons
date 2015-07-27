package com.brajagopal.rmend.exception;

/**
 * @author <bxr4261>
 */
public class UserNotFoundException extends Throwable {

    private final String message;

    public UserNotFoundException(String _type, String _value) {
        message = "User with " + _type + ": " + _value + " was not found.";
    }

    public UserNotFoundException(String _uuid) {
        message = "User with UUID: " + _uuid + " was not found.";
    }

    public UserNotFoundException(Long _uid) {
        message = "User with UID: " + _uid + " was not found.";
    }


    @Override
    public String toString() {
        return "UserNotFoundException{" +
                "message=" + message +
                '}';
    }

    @Override
    public String getMessage() {
        return message;
    }
}
