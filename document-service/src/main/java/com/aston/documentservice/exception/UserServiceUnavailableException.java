package com.aston.documentservice.exception;


public class UserServiceUnavailableException extends RuntimeException {


    public UserServiceUnavailableException(String message) {
        super(message);
    }
}