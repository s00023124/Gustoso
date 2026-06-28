package com.musa.gustoso.exceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String messaggio){
        super(messaggio);
    }
}