package com.example.A3_Sistemas_Distribuidos.exception;

public class IdNotFoundException extends  RuntimeException{
    public IdNotFoundException (String error){
        super(error);
    }
}
