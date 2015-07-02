package com.brajagopal.rmend.exception.beans;

/**
 * @author <bxr4261>
 */
public class DSErrorBean {
    private final int code;
    private final String message;
    private final String methodName;

    private DSErrorBean(int _code, String _message, String _methodName) {
        this.code = _code;
        this.message = _message;
        this.methodName = _methodName;
    }

    public static DSErrorBean createInstance(int _code, String _message, String _methodName) {
        return new DSErrorBean(_code, _message, _methodName);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "DSErrorBean { " +
            methodName + ": " +
            "(" + code +
            "): '" + message + "'" +
            " }";
    }
}
