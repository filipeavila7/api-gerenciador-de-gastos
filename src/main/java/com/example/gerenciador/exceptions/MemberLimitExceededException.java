package com.example.gerenciador.exceptions;

public class MemberLimitExceededException extends RuntimeException{
    public MemberLimitExceededException() {
        super("Família cheia");
    }
}
