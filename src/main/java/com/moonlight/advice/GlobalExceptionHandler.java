package com.moonlight.advice;

import com.moonlight.advice.exception.*;
import com.moonlight.advice.exception.IllegalAccessException;
import com.moonlight.advice.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<?> IllegalAccessException(IllegalAccessException illegalAccessException) {
        return new ResponseEntity<>(illegalAccessException.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<?> handleRoomNotAvailableException (RoomNotAvailableException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<?> InvalidDateRangeException(InvalidDateRangeException invalidDateRangeException) {
        return new ResponseEntity<>(invalidDateRangeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnavailableResourceException.class)
    public ResponseEntity<?> UnavailableResourceException(UnavailableResourceException unavailableResourceException) {
        return new ResponseEntity<>(unavailableResourceException.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
    handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
            });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllExceptions(Exception exception) {
//        // Log the exception for debugging purposes
//        log.error("An error occurred: ", exception);
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception) {
//        log.error("Access denied: ", exception);
//        return new ResponseEntity<>("Access denied: " + exception.getMessage(), HttpStatus.FORBIDDEN);
//    }
    // ^ Uncomment these 2 for testing Responses and bug fixing

}
