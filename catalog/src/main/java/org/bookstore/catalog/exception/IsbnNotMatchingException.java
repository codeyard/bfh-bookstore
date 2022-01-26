package org.bookstore.catalog.exception;

public class IsbnNotMatchingException extends Exception{
    public IsbnNotMatchingException() {
        super("ISBN not matching");
    }
}
