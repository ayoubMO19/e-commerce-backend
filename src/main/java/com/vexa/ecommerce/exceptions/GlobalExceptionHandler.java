package com.vexa.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponse(Exception ex, HttpStatus status, String type) {
        ErrorResponse body = new ErrorResponse(
                ex.getMessage(),
                status.value(),
                type,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "ResourceNotFoundException");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "BadRequestException");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manageGeneralException(Exception ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse body = new ErrorResponse(
                errors.toString(),
                HttpStatus.BAD_REQUEST.value(),
                "ValidationException",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
