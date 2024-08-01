package com.moonlight.advice;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UserAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Operation(summary = "Handles RecordNotFoundException", description = "Returned when a requested record is not found in the database.")
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?> handleRecordNotFoundException(RecordNotFoundException recordNotFoundException) {
        return new ResponseEntity<>(recordNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> userAlreadyExistsException(UserAlreadyExistsException userAlreadyExistsException) {
        return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> invalidInputException(InvalidInputException invalidInputException) {
        return new ResponseEntity<>(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
