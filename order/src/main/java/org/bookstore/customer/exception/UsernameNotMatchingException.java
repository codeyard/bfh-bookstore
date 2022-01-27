package org.bookstore.customer.exception;

public class UsernameNotMatchingException extends Exception {
    public UsernameNotMatchingException() {
        super("Username must not change");
    }
}
