package com.example.gerenciador.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String m) {
        super(m);
    }
}
