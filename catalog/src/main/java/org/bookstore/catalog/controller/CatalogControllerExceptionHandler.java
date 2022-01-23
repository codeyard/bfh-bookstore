package org.bookstore.catalog.controller;

import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CatalogControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BookNotFoundException.class, BookAlreadyExistsException.class})
    public ResponseEntity<ErrorInfo> resourceNotFoundException(Exception ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());

        if (ex instanceof BookNotFoundException) {
            message.setCode(ErrorCode.BOOK_NOT_FOUND);
            message.setStatus(HttpStatus.NOT_FOUND);
        } else if (ex instanceof BookAlreadyExistsException) {
            message.setCode(ErrorCode.BOOK_ALREADY_EXISTS);
            message.setStatus(HttpStatus.CONFLICT);

        } else {
            message.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(message, message.getStatus());

    }


}
