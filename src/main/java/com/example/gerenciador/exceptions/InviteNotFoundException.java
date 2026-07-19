package com.example.gerenciador.exceptions;

public class InviteNotFoundException extends RuntimeException{
    public InviteNotFoundException() {
        super("Token não encontrado");
    }
}
