package com.example.gerenciador.exceptions;

public class InviteNotFoundException extends RuntimeException{
    public InviteNotFoundException() {
        super("Convite não encontrado");
    }
}
