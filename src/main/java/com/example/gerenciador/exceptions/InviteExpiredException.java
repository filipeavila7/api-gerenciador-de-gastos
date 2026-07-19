package com.example.gerenciador.exceptions;

public class InviteExpiredException extends RuntimeException{
    public InviteExpiredException() {
        super("Convite expirado");
    }
}
