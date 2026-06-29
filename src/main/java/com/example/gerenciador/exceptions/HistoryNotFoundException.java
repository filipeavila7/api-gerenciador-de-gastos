package com.example.gerenciador.exceptions;

public class HistoryNotFoundException extends RuntimeException{
    public HistoryNotFoundException() {
        super("Histórico não encontrado");
    }
}
