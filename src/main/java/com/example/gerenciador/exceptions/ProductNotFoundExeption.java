package com.example.gerenciador.exceptions;

public class ProductNotFoundExeption extends RuntimeException{
    public ProductNotFoundExeption() {
        super("Produto não encontrado");
    }
}
