package com.example.gerenciador.exceptions;

public class ShoppingListNotFoundException extends RuntimeException{
    public ShoppingListNotFoundException() {
        super("Lista de compras não encontrada");
    }
}
