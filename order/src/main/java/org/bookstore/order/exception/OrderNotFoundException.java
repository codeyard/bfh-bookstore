package org.bookstore.order.exception;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException(Long id) {
        super("Order " + id + " not found");
    }

}
