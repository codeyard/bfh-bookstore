package org.bookstore.order.exception;

public class OrderAlreadyShippedException extends Exception {

    public OrderAlreadyShippedException(Long id) {
        super("Order " + id + " already shipped");
    }

}
