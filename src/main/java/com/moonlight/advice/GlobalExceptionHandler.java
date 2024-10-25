package com.moonlight.advice;

import com.moonlight.advice.exception.IllegalAccessException;
import com.moonlight.advice.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.format.DateTimeParseException;
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
    public ResponseEntity<?> invalidInputException(InvalidInputException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<?> IllegalAccessException(IllegalAccessException illegalAccessException) {
        return new ResponseEntity<>(illegalAccessException.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<?> handleRoomNotAvailableException(RoomNotAvailableException ex) {
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
    handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>>
    handleConstrainViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String fieldName = ((PathImpl) cv.getPropertyPath()).getLeafNode().getName();
            String errorMessage = cv.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>("Authentication is required to access this resource",
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>("Your session has expired, please log in again",
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Current user not authorized: " + ex.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    //@ExceptionHandler(Exception.class)
    //public ResponseEntity<Object> handleDateTimeParseException (DateTimeParseException ex){
      //  return new ResponseEntity<>("Invalid date format", HttpStatus.BAD_REQUEST);
    //}


    @ExceptionHandler(IllegalCurrentStateException.class)
    public ResponseEntity<String> handleIllegalCurrentStateException(IllegalCurrentStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PayPalServiceException.class)
    public ResponseEntity<String> handlePayPalServiceException(PayPalServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException (IOException ex, WebRequest request){
        System.err.println("IOException occurred: "+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process input/output operation. Please, try again");
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRunTimeException (RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ïnternal server error:" + ex.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllExceptions(Exception exception) {
//        // Log the exception for debugging purposes
//        log.error("An error occurred: ", exception);
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    // ^ Uncomment for testing Responses and bug fixing

}
