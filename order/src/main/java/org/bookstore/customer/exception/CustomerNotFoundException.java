package org.bookstore.customer.exception;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(Long id) {
        super("Customer " + id + " not found");
    }

}
