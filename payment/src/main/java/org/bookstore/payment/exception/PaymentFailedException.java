package org.bookstore.payment.exception;

import javax.xml.ws.WebFault;

@WebFault(name = "PaymentFailedException")
public class PaymentFailedException extends Exception {

    public PaymentFailedException(String message) {
        super(message);
    }

    public String getFaultInfo() {
        return getMessage();
    }

}
