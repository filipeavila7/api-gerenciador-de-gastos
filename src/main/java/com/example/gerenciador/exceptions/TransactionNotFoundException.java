package com.example.gerenciador.exceptions;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException() {
        super("Transação não encontrada");
    }
}
