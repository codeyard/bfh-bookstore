package org.bookstore.customer.exception;

public class UsernameAlreadyExistsException extends Exception {

    public UsernameAlreadyExistsException(String username) {
        super("Username '" + username + "' already exists");
    }

}
