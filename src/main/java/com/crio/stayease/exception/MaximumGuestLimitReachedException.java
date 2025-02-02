package com.crio.stayease.exception;

public class MaximumGuestLimitReachedException extends RuntimeException{
    public MaximumGuestLimitReachedException(String message){
        super(message);
    }
}
