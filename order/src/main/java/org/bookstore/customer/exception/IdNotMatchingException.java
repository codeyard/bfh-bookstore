package org.bookstore.customer.exception;

public class IdNotMatchingException extends Exception {
    public IdNotMatchingException() {
        super("Identifier not matching");
    }
}
