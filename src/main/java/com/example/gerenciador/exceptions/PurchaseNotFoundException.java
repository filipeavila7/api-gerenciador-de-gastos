package com.example.gerenciador.exceptions;

public class PurchaseNotFoundException extends RuntimeException{
    public PurchaseNotFoundException() {
        super("Purchase não encontrada");
    }
}
