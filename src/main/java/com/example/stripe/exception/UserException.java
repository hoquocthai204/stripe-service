package com.example.stripe.exception;

public class UserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserException(String mess) {
        super(mess);
    }
}
