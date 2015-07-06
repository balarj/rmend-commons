package com.brajagopal.rmend.exception;

/**
 * @author <bxr4261>
 */
@SuppressWarnings("unused")
public class DocumentNotFoundException extends Throwable {

    private final Long documentNumber;

    public DocumentNotFoundException(long _docNumber) {
        this.documentNumber = _docNumber;
    }

    @Override
    public String toString() {
        return "DocumentNotFoundException{" +
                "documentNumber=" + documentNumber +
                '}';
    }

    @Override
    public String getMessage() {
        return "Document with ID: " + getDocumentNumber() + " was not found.";
    }

    public Long getDocumentNumber() {
        return documentNumber;
    }
}
