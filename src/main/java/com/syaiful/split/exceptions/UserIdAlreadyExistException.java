package com.syaiful.split.exceptions;

public class UserIdAlreadyExistException extends RuntimeException {
    public UserIdAlreadyExistException(String userIdIsAlreadyTaken) {
        super(userIdIsAlreadyTaken);
    }
}
