package com.example.stripe.exception;

public class ProductException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductException(String mess) {
        super(mess);
    }
}
