package com.example.gerenciador.exceptions;

public class InvalidInviteException extends RuntimeException{
    public InvalidInviteException() {
        super("Token inválido");
    }
}
