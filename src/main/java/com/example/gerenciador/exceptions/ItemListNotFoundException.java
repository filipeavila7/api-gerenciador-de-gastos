package com.example.gerenciador.exceptions;

public class ItemListNotFoundException extends RuntimeException{
    public ItemListNotFoundException() {
        super("Item não encontrado");
    }
}
