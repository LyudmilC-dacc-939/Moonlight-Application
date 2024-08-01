package com.moonlight.advice.exception;

import org.apache.coyote.BadRequestException;

public class InvalidInputException extends BadRequestException {
    //public class RecordNotFoundException extends RuntimeException {
    //    public RecordNotFoundException(String message) {
    //        super(message);
    //    }
    //}
    public InvalidInputException(){
        String message = "Invalid Input. Bad user request.";
    }
}
