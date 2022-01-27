package org.bookstore.customer.controller;

import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.exception.IdNotMatchingException;
import org.bookstore.customer.exception.UsernameAlreadyExistsException;
import org.bookstore.customer.exception.UsernameNotMatchingException;
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
public class CustomerControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setCode(ErrorCode.CUSTOMER_NOT_FOUND);
        message.setStatus(HttpStatus.NOT_FOUND);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setCode(ErrorCode.USERNAME_ALREADY_EXISTS);
        message.setStatus(HttpStatus.CONFLICT);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo handleUsernameNotMatching(UsernameNotMatchingException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setCode(ErrorCode.USERNAME_NOT_MATCHING);
        message.setStatus(HttpStatus.CONFLICT);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleIdNotMatching(IdNotMatchingException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(HttpStatus.BAD_REQUEST);
        return message;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getFieldError().getDefaultMessage(),
            ((ServletWebRequest) request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraint(ConstraintViolationException ex, WebRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(),
            ((ServletWebRequest) request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
