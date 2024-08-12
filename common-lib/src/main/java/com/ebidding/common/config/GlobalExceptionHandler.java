package com.ebidding.common.config;

import com.ebidding.common.auth.PermissionDenyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PermissionDenyException.class)
    public ResponseEntity<?> handlePermissionDenyException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INCORRECT ROLE");
    }
}