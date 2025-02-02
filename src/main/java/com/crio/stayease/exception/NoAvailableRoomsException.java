package com.crio.stayease.exception;

public class NoAvailableRoomsException extends RuntimeException{
    public NoAvailableRoomsException(String message){
        super(message);
    }
}
