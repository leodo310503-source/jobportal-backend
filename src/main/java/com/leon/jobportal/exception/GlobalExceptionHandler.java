package com.leon.jobportal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiException> handleRuntimeException(RuntimeException e) {
        ApiException error = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleException(Exception e) {
        ApiException error = new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong",
                LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(error);
    }
}