package org.bookstore.payment.exception;

import javax.xml.ws.WebFault;

@WebFault(name = "PaymentFailedException")
public class PaymentFailedException extends Exception {


//    ErrorCode errorCode;

    public PaymentFailedException(String message) {
        super(message);
    }

    public String getFaultInfo() {
        return getMessage();
    }

//    public ErrorCode getCode() {
//        return errorCode;
//    }
//
//    public enum ErrorCode {
//        AMOUNT_EXCEEDS_LIMIT,
//        CREDIT_CARD_EXPIRED,
//        INVALID_CREDIT_CARD
//    }

}
