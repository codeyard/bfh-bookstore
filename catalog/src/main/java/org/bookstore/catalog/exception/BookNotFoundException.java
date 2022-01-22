package org.bookstore.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BookNotFoundException extends Exception {

    public BookNotFoundException(String message) {
        super(message);
    }

}
