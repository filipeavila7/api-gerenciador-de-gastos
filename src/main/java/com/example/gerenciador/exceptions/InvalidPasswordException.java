package com.example.gerenciador.exceptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException() {
        super("Senha inválida");
    }
}
