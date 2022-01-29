package org.bookstore.order.controller;


import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public class OrderRequest {

    @NotNull(message = "Missing customerId")
    private Long customerId;
    @NotEmpty(message = "Missing items")
    private List<Item> items;

    public Long getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
