package org.bookstore.order.controller;

import org.bookstore.customer.exception.CustomerNotFoundException;
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

@RestControllerAdvice
public class OrderControllerExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorInfo handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
//        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
//        message.setStatus(HttpStatus.BAD_REQUEST);
//        message.setCode(ErrorCode.CUSTOMER_NOT_FOUND);
//        return message;
//    }
//
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorInfo handlePaymentFailed(CustomerNotFoundException ex, HttpServletRequest request) {
//        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
//        message.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
//        message.setCode(ErrorCode.INVALID_CREDIT_CARD);
//        return message;
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        ErrorInfo message = new ErrorInfo(ex.getFieldError().getDefaultMessage(),
//                ((ServletWebRequest) request).getRequest().getRequestURI());
//        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
//        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//    }



}