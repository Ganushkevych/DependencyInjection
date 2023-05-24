package org.fpm.di.example;

public class CannotCreateException extends RuntimeException {
    public CannotCreateException(String s) {
        super(s);
    }
}
