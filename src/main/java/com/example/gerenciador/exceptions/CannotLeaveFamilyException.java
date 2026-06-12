package com.example.gerenciador.exceptions;

public class CannotLeaveFamilyException extends RuntimeException{
    public CannotLeaveFamilyException() {
        super("Você é o único membro da família");
    }
}
