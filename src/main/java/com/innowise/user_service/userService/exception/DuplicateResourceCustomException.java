package com.innowise.user_service.userService.exception;

public class DuplicateResourceCustomException extends RuntimeException{

    public DuplicateResourceCustomException(String message) {
        super(message);
    }
}
