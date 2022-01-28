package org.bookstore.order.exception;

import org.bookstore.order.controller.ErrorInfo;

public class BookNotFoundException extends RuntimeException {

    private ErrorInfo errorInfo;

    public BookNotFoundException(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
