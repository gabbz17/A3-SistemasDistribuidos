package com.example.A3_Sistemas_Distribuidos.exception;

public class NameUniqueException extends RuntimeException{
    public NameUniqueException(String error){
        super(error);
    }
}
