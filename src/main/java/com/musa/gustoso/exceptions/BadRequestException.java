package com.musa.gustoso.exceptions;

// Lanciata quando la richiesta del client non è valida tipo file upload ecc..
public class BadRequestException extends RuntimeException {
    public BadRequestException(String messaggio) {
        super(messaggio);
    }
}
