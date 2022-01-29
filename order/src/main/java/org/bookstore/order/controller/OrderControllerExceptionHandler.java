package org.bookstore.order.controller;

import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.order.exception.BookNotFoundException;
import org.bookstore.order.exception.OrderAlreadyShippedException;
import org.bookstore.order.exception.OrderNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
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

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class OrderControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(HttpStatus.NOT_FOUND);
        message.setCode(ErrorCode.CUSTOMER_NOT_FOUND);
        return message;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handlePaymentFailed(PaymentFailedException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getErrorInfo().getMessage(), request.getRequestURI());
        message.setStatus(HttpStatus.valueOf(ex.getErrorInfo().getStatus()));
        message.setCode(ex.getErrorInfo().getCode());
        return message;
    }


    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorInfo handleBookNotFound(BookNotFoundException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getErrorInfo().getMessage(), request.getRequestURI());
        message.setStatus(NOT_FOUND);
        message.setCode(ErrorCode.BOOK_NOT_FOUND);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorInfo handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(NOT_FOUND);
        message.setCode(ErrorCode.ORDER_NOT_FOUND);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ErrorInfo handleOrderAlreadyShipped(OrderAlreadyShippedException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getMessage(), request.getRequestURI());
        message.setStatus(CONFLICT);
        message.setCode(ErrorCode.ORDER_ALREADY_SHIPPED);
        return message;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getFieldError().getDefaultMessage(),
            ((ServletWebRequest) request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }


}
