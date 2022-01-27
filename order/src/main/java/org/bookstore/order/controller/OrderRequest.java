package org.bookstore.order.controller;


import org.bookstore.order.entity.OrderItem;

import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {

    @NotNull (message = "Missing customerId")
    private Long customerId;
    @NotNull (message = "Missing items") private List<Item> items;

    public static class Item {
        @NotNull private String isbn;
        @NotNull private Integer quantity;

        public String getIsbn() {
            return isbn;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

}
