package com.b2b.b2b.exception;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));

    }

    public ResourceNotFoundException(String resourceName, String field, Integer fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
    }

}
