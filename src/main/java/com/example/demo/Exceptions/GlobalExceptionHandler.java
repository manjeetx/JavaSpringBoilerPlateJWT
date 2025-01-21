package com.example.demo.Exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Validation failed");

        // Extract field errors
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        response.put("errors", fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle duplicate key violations
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKeyException(DuplicateKeyException ex) {
        // Extract duplicate key error details from the exception message
        String message = ex.getMessage();
        String errorMessage = "Duplicate key error";
        String duplicateField = null;

        // Try to extract the field name from the exception message
        if (message != null && message.contains("dup key:")) {
            int start = message.indexOf("dup key:") + 9; // Skip "dup key:" text
            int end = message.indexOf("}", start);
            if (start >= 0 && end > start) {
                String keyDetails = message.substring(start, end).trim();
                String[] keyParts = keyDetails.split(":");
                if (keyParts.length == 2) {
                    duplicateField = keyParts[0].trim().replace("{", "").replace("}", "");  // Fix field extraction
                }
            }
        }

        // Prepare response with simpler message
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Duplicate value found for field: " + (duplicateField != null ? duplicateField : "unknown"));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
