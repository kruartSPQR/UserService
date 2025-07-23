package com.innowise.user_service.userService.exception;

public class ResourceNotFoundCustomException extends RuntimeException {
    public ResourceNotFoundCustomException(String message) {
        super(message);
    }
}
