package com.example.gerenciador.exceptions;

public class FamilyLimitExceededException extends RuntimeException{
    public FamilyLimitExceededException() {
        super(" já atingiu o limite de 3 famílias");
    }
}
