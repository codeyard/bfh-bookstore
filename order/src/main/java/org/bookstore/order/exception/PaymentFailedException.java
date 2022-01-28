package org.bookstore.order.exception;

import org.bookstore.order.controller.ErrorInfo;

public class PaymentFailedException extends RuntimeException {

    private final ErrorInfo errorInfo;

    public PaymentFailedException(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
