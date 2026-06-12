package com.example.gerenciador.exceptions;

public class CannotRemoveAdminException extends RuntimeException{
    public CannotRemoveAdminException() {
        super("Não é possível remover administradores da família");
    }
}
