package org.bookstore.catalog.controller;

import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.exception.IsbnNotMatchingException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class CatalogControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleBookNotFound(BookNotFoundException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setCode(ErrorCode.BOOK_NOT_FOUND);
        message.setStatus(HttpStatus.NOT_FOUND);
        return message;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo handleBookAlreadyExists(BookAlreadyExistsException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(HttpStatus.CONFLICT);
        message.setCode(ErrorCode.BOOK_ALREADY_EXISTS);
        return message;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleIsbnNotMatching(IsbnNotMatchingException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(HttpStatus.BAD_REQUEST);
        return message;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getFieldError().getDefaultMessage(),
        ((ServletWebRequest)request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public  ResponseEntity<Object> handleConstraint(ConstraintViolationException ex,
                                                    WebRequest request ) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }


}
