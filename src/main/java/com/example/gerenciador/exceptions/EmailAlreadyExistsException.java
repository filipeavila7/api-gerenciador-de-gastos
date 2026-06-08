package com.example.gerenciador.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException() {
        super("Email já cadastrado");
    }
}
