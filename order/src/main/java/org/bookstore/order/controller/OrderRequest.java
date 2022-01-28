package org.bookstore.order.controller;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class OrderRequest {

    @NotNull(message = "Missing customerId")
    private Long customerId;
    @NotNull(message = "Missing items")
    private List<Item> items;

    public Long getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @NotNull(message = "Missing item isbn")
        @Pattern(regexp = "^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")
        private String isbn;
        @NotNull(message = "Missing item quantity")
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

}
