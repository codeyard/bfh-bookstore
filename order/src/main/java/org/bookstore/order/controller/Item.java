package org.bookstore.order.controller;

import javax.validation.constraints.NotEmpty;

public class Item {
    @NotEmpty(message = "Missing item isbn")
    private String isbn;
    @NotEmpty(message = "Missing item quantity")
    private Integer quantity;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
