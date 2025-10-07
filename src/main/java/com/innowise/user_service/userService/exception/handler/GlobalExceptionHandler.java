package com.innowise.user_service.userService.exception.handler;

import com.innowise.common.exception.DuplicateResourceCustomException;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundCustomException.class)
    public ResponseEntity<String> resourceNotFoundCustomException(ResourceNotFoundCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateResourceCustomException.class)
    public ResponseEntity<String> duplicateResourceCustomException(DuplicateResourceCustomException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
