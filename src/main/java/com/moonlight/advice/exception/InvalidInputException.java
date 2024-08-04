package com.moonlight.advice.exception;

import org.apache.coyote.BadRequestException;

public class InvalidInputException extends BadRequestException {
    public InvalidInputException(String message){
        message = "Invalid Input. Bad user request.";
    }
}