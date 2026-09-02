package com.denofdevelopers.mktolls.exception;

/**
 * Created by BuktopMKD on 30-Jun-17.
 */

public class FirstLocationException extends RuntimeException{
    public FirstLocationException() {
        super();
    }

    public FirstLocationException(String message) {
        super(message);
    }

    public FirstLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirstLocationException(Throwable cause) {
        super(cause);
    }
}
