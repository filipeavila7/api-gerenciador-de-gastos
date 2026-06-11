package com.example.gerenciador.exceptions;

public class UserAlreadyInFamilyException extends RuntimeException{
    public UserAlreadyInFamilyException() {
        super("Esse usuário ja pertence a essa família.");
    }
}
