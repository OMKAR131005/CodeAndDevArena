package com.devconnect.bakend.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String username){
        super("User Already Exist With"+username);
    }
}
