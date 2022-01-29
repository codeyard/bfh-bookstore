package org.bookstore.payment.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.bookstore.payment.dto.CreditCardType;
import org.bookstore.payment.exception.PaymentFailedException;
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
import javax.validation.UnexpectedTypeException;

@RestControllerAdvice
public class PaymentControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handleInvalidCreditCard(PaymentFailedException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(request.getRequestURI());
        message.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        message.setCode(ErrorCode.INVALID_CREDIT_CARD);
        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handleMissingCreditCardType(NullPointerException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(request.getRequestURI());
        message.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        message.setCode(ErrorCode.INVALID_CREDIT_CARD);
        return message;
    }

    /*@ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handleInvalidCreditCardType(UnexpectedTypeException ex, HttpServletRequest request) {
        ErrorInfo message = new ErrorInfo(request.getRequestURI());
        message.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        message.setCode(ErrorCode.INVALID_CREDIT_CARD);
        return message;
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handleInvalidCreditCardType(InvalidFormatException ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        if (ex.getTargetType().isAssignableFrom(CreditCardType.class))
            errorMessage = "Invalid credit card type";
        ErrorInfo message = new ErrorInfo(errorMessage,
            ((ServletWebRequest) request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        message.setCode(ErrorCode.INVALID_CREDIT_CARD);
        return message;
    }*/

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorInfo message = new ErrorInfo(ex.getFieldError().getDefaultMessage(),
            ((ServletWebRequest) request).getRequest().getRequestURI());
        message.setStatus(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
