package org.bookstore.catalog.exception;

public class BookAlreadyExistsException extends Exception {

    public BookAlreadyExistsException(String isbn) {
        super("Book with ISBN " + isbn + " already exists");
    }
}
