package com.example.gerenciador.exceptions;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException() {
        super("Categoria não encontrada");
    }
}
