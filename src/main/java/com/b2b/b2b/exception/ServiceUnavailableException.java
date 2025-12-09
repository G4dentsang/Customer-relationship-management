package com.b2b.b2b.exception;

public class ServiceUnavailableException extends RuntimeException
{
    public ServiceUnavailableException(String message)
    {
        super(message);
    }
}
