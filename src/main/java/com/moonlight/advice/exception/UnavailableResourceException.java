package com.moonlight.advice.exception;

public class UnavailableResourceException  extends RuntimeException{
    public UnavailableResourceException(String message) {
        super(message);
    }
}
