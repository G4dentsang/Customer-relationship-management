package com.b2b.b2b.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException() {
    }
    public ResourceAlreadyExistsException(String resourceName, String fieldName) {
        super(String.format("%s already exists : %s", resourceName, fieldName));

    }
    public ResourceAlreadyExistsException(String resourceName, String field, Integer fieldName) {
        super(String.format("%s already exists with %s: %s", resourceName, field, fieldName));

    }

}
