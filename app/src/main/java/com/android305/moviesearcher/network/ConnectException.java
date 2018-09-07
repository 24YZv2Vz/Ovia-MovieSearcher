package com.android305.moviesearcher.network;

public class ConnectException extends Exception {
    public ConnectException(Throwable cause) {
        super(cause);
    }

    public ConnectException(String message) {
        super(message);
    }
}