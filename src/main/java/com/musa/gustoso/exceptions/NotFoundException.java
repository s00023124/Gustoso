package com.musa.gustoso.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String messaggio){
        super(messaggio);
    }
}
