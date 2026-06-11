package com.example.gerenciador.exceptions;

public class FamilyNotFoundException extends RuntimeException{
    public FamilyNotFoundException() {
        super("Família não encontrada");
    }
}
