package org.bookstore.catalog.exception;

public class BookNotFoundException extends Exception {

    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found");
    }

}
